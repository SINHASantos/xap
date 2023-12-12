package com.gigaspaces.internal.server.space.mvcc;

import com.gigaspaces.internal.server.metadata.IServerTypeDesc;
import com.gigaspaces.internal.server.space.SpaceEngine;
import com.gigaspaces.internal.server.space.SpaceUidFactory;
import com.gigaspaces.internal.server.storage.EntryHolder;
import com.gigaspaces.internal.server.storage.MVCCEntryMetaData;
import com.j_spaces.core.cache.context.Context;
import com.j_spaces.core.cache.mvcc.MVCCEntryCacheInfo;
import com.j_spaces.core.cache.mvcc.MVCCEntryHolder;
import com.j_spaces.core.cache.mvcc.MVCCShellEntryCacheInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class MVCCUtils {
    public static ArrayList<MVCCEntryMetaData> getMVCCEntryMetaData(SpaceEngine engine, String typeName, Object id) {
        Context context = engine.getCacheManager().getCacheContext();
        ArrayList<MVCCEntryMetaData> metaDataList = new ArrayList<>();

        IServerTypeDesc typeDesc = engine.getTypeManager().getServerTypeDesc(typeName);
        String uid = SpaceUidFactory.createUidFromTypeAndId(typeDesc.getTypeDesc(), id);

        MVCCShellEntryCacheInfo mvccShellEntryCacheInfo = engine.getCacheManager().getMVCCShellEntryCacheInfoByUid(uid);
        if (mvccShellEntryCacheInfo != null) { // When we abort write operation, backup space does not contain the shell.
            Iterator<MVCCEntryCacheInfo> mvccEntryCacheInfoIterator = mvccShellEntryCacheInfo.descIterator();
            while(mvccEntryCacheInfoIterator.hasNext()) {
                MVCCEntryHolder next = mvccEntryCacheInfoIterator.next().getEntryHolder();
                MVCCEntryMetaData metaData = new MVCCEntryMetaData();
                metaData.setCommittedGeneration(next.getCommittedGeneration());
                metaData.setOverrideGeneration(next.getOverrideGeneration());
                metaData.setLogicallyDeleted(next.isLogicallyDeleted());
                metaData.setOverridingAnother(next.isOverridingAnother());
                metaData.setVersion(next.getVersionID());
                metaDataList.add(metaData);
            }
        } else {
            return null; // shell does not exists
        }
        engine.getCacheManager().freeCacheContext(context);
        return metaDataList;
    }

    public static MVCCEntryMetaData getMVCCDirtyEntryUnderTransaction(SpaceEngine engine, String typeName, Object id,
                                                                      long transactionId) {
        if (transactionId == -1) return null;
        IServerTypeDesc typeDesc = engine.getTypeManager().getServerTypeDesc(typeName);
        String uid = SpaceUidFactory.createUidFromTypeAndId(typeDesc.getTypeDesc(), id);
        MVCCShellEntryCacheInfo mvccShellEntryCacheInfo = engine.getCacheManager().getMVCCShellEntryCacheInfoByUid(uid);
        boolean isDirtyUnderTransaction = Optional.ofNullable(mvccShellEntryCacheInfo)
                .map(MVCCShellEntryCacheInfo::getDirtyEntryHolder)
                .map(EntryHolder::getXidOriginated)
                .map(xtnEntry -> xtnEntry.m_Transaction.id)
                .equals(Optional.of(transactionId));
        return isDirtyUnderTransaction ? mvccEntryHolderToMVCCEntryMetaData(mvccShellEntryCacheInfo.getDirtyEntryHolder()) : null;
    }

    private static MVCCEntryMetaData mvccEntryHolderToMVCCEntryMetaData(MVCCEntryHolder entryHolder) {
        if (entryHolder == null) {
            return null;
        }
        MVCCEntryMetaData mvccDirtyEntryMetaData = new MVCCEntryMetaData();
        mvccDirtyEntryMetaData.setCommittedGeneration(entryHolder.getCommittedGeneration());
        mvccDirtyEntryMetaData.setOverrideGeneration(entryHolder.getOverrideGeneration());
        mvccDirtyEntryMetaData.setLogicallyDeleted(entryHolder.isLogicallyDeleted());
        mvccDirtyEntryMetaData.setOverridingAnother(entryHolder.isOverridingAnother());
        return mvccDirtyEntryMetaData;
    }
}
