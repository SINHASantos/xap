package com.j_spaces.core.cache.mvcc;

import com.gigaspaces.internal.server.metadata.IServerTypeDesc;
import com.gigaspaces.internal.server.storage.*;
import com.gigaspaces.internal.transport.IEntryPacket;
import com.gigaspaces.internal.transport.mvcc.IMVCCEntryPacket;
import com.gigaspaces.internal.transport.mvcc.MVCCEntryPacketMetadata;
import com.gigaspaces.internal.utils.Textualizer;
import com.gigaspaces.time.SystemTime;
import com.j_spaces.core.Constants;
import com.j_spaces.core.server.transaction.EntryXtnInfo;
import com.j_spaces.kernel.locks.IMVCCLockObject;


@com.gigaspaces.api.InternalApi
public class MVCCEntryHolder extends EntryHolder implements IMVCCLockObject {

    private volatile long committedGeneration = -1;
    private volatile long overrideGeneration = -1;

    public MVCCEntryHolder(IServerTypeDesc typeDesc, String uid, long scn, boolean isTransient, ITransactionalEntryData entryData) {
        super(typeDesc, uid, scn, isTransient, entryData);

    }

    // build eHolder from entryPacket (only for recovery)
    public MVCCEntryHolder(IServerTypeDesc typeDesc, String uid, long scn, IEntryPacket entryPacket, ITransactionalEntryData entryData) {
        super(typeDesc, uid, scn, entryPacket.isTransient(), entryData);
        if (entryPacket instanceof IMVCCEntryPacket) { // recovery replication
            IMVCCEntryPacket mvccEntryPacket = (IMVCCEntryPacket)entryPacket;
            if (mvccEntryPacket.isMVCCEntryMetadataApplied()) {
                MVCCEntryPacketMetadata metadata = mvccEntryPacket.getMVCCEntryMetadata();
                this.committedGeneration = metadata.getCommittedGeneration();
                this.overrideGeneration = metadata.getOverrideGeneration();
                setLogicallyDeleted(metadata.isLogicallyDeleted());
                setOverridingAnother(metadata.isOverridingAnother());
            }
        }
    }

    protected MVCCEntryHolder(MVCCEntryHolder other) {
        super(other);
        this.committedGeneration = other.getCommittedGeneration();
        this.overrideGeneration = other.getOverrideGeneration();
    }

    @Override
    public IEntryHolder createCopy() {
        return new MVCCEntryHolder(this);
    }

    public MVCCEntryHolder createLogicallyDeletedDummyEntry(EntryXtnInfo entryXtnInfo) {
        ITransactionalEntryData ed = new FlatEntryData(
                new Object[0],
                null,
                getEntryData().getEntryTypeDesc(),
                0 /*versionID*/,
                Long.MAX_VALUE, /* expirationTime */
                entryXtnInfo);
        MVCCEntryHolder dummy = new MVCCEntryHolder(this.getServerTypeDesc(), this.getUID(), SystemTime.timeMillis(),
                this.isTransient(), ed);
        dummy.setLogicallyDeleted(true);
        return dummy;
    }

    @Override
    public int getLockedObjectHashCode() {
        return Math.abs(getUID().hashCode());
    }

    public long getCommittedGeneration() {
        return committedGeneration;
    }

    public void setCommittedGeneration(long committedGeneration) {
        this.committedGeneration = committedGeneration;
    }

    public long getOverrideGeneration() {
        return overrideGeneration;
    }

    public void setOverrideGeneration(long overrideGeneration) {
        this.overrideGeneration = overrideGeneration;
    }

    public boolean isOverridingAnother() {
        return getFlag(Constants.SpaceItem.IS_OVERRIDING_ANOTHER);
    }

    public void setOverridingAnother(boolean overridingAnother) {
        setFlag(Constants.SpaceItem.IS_OVERRIDING_ANOTHER, overridingAnother);
    }

    public boolean isLogicallyDeleted() {
        return getFlag(Constants.SpaceItem.IS_LOGICALLY_DELETED);
    }

    public void setLogicallyDeleted(boolean logicallyDeleted) {
        setFlag(Constants.SpaceItem.IS_LOGICALLY_DELETED, logicallyDeleted);
    }

    public void setVersion(int version){
        getEntryData().setVersion(version);
    }

    @Override
    public void toText(Textualizer textualizer) {
        textualizer.append("typeName", getClassName());
        textualizer.append("uid", getUID());
        textualizer.append("committedGeneration", getCommittedGeneration());
        textualizer.append("overrideGeneration", getOverrideGeneration());
        textualizer.append("isLogicallyDeleted", isLogicallyDeleted());
    }

}
