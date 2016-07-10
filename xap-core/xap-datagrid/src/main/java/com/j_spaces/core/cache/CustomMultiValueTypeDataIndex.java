/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.j_spaces.core.cache;

import com.gigaspaces.internal.server.storage.IEntryHolder;
import com.gigaspaces.metadata.index.ISpaceIndex;
import com.j_spaces.kernel.IObjectInfo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Custom index for a multi-value type collection/array NOTE- it is assumed that each element  has
 * consistent hashCode() and value() methods
 *
 * @author Yechiel Fefer
 * @version 1.0
 * @since 8.0
 */
@com.gigaspaces.api.InternalApi
public class CustomMultiValueTypeDataIndex<K>
        extends CustomTypeDataIndex<K> {
    private final AbstractMultiValueIndexHandler<K> _multiValueIndexHandler;
    private final ISpaceIndex.MultiValuePerEntryIndexTypes _multiValueType;

    public CustomMultiValueTypeDataIndex(CacheManager cacheManager, ISpaceIndex index, int pos,
                                         int indexCreationNumber, ISpaceIndex.MultiValuePerEntryIndexTypes multiValueType) {
        super(cacheManager, index, pos, indexCreationNumber);

        _multiValueType = multiValueType;
        switch (_multiValueType) {
            case COLLECTION:
                _multiValueIndexHandler = new CollectionIndexHandler<K>(this);
                break;
            case ARRAY:
                _multiValueIndexHandler = new ArrayIndexHandler<K>(this);
                break;
            case GENERAL:
                _multiValueIndexHandler = new GeneralMultiValueIndexHandler<K>(this);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isMultiValuePerEntryIndex() {
        return true;
    }

    @Override
    public void insertEntryIndexedField(IEntryCacheInfo pEntry, K fieldValue, TypeData pType) {
        _multiValueIndexHandler.insertEntryIndexedField(pEntry, fieldValue, pType, pEntry.getBackRefs());
    }

    @Override
    public void insertEntryIndexedField(IEntryCacheInfo pEntry, K fieldValue, TypeData pType, ArrayList<IObjectInfo<IEntryCacheInfo>> insertBackRefs) {
        _multiValueIndexHandler.insertEntryIndexedField(pEntry, fieldValue, pType, insertBackRefs);
    }

    @Override
    public int removeEntryIndexedField(IEntryHolder eh, ArrayList<IObjectInfo<IEntryCacheInfo>> deletedBackRefs,
                                       K fieldValue, int refpos, boolean removeIndexedValue, IEntryCacheInfo pEntry) {
        return _multiValueIndexHandler.removeEntryIndexedField(eh, deletedBackRefs,
                fieldValue, refpos, removeIndexedValue, pEntry);
    }

    @Override
    public int updateIndexValue(TypeData pType, IEntryHolder eh, IEntryCacheInfo pEntry,
                                K original, K updated, ArrayList<IObjectInfo<IEntryCacheInfo>> originalBackRefs,
                                int refpos, UpdateIndexModes updateMode) {
        return _multiValueIndexHandler.updateMultiValueIndex(pType, eh, pEntry, original,
                updated, originalBackRefs, refpos, updateMode);
    }


    @Override
    public int updateIndexValueUndexXtn(TypeData pType, IEntryHolder eh, IEntryCacheInfo pEntry,
                                        K previous, K updated, ArrayList<IObjectInfo<IEntryCacheInfo>> previousBackRefs,
                                        int refpos, boolean entry_double_update) {
        return _multiValueIndexHandler.updateMultiValueIndexUndexXtn(pType, eh,
                pEntry, previous, updated, previousBackRefs, refpos);

    }

    @Override
    int consolidateIndexValueOnXtnEnd(IEntryHolder eh, IEntryCacheInfo pEntry, K keptValue,
                                      K deletedVlaue, ArrayList<IObjectInfo<IEntryCacheInfo>> deletedBackRefs,
                                      int refpos, boolean onError) {
        return _multiValueIndexHandler.consolidateMultiValueIndexOnXtnEnd(eh, pEntry,
                keptValue, deletedVlaue, deletedBackRefs, refpos, onError);
    }

    @Override
    protected int multiValueSize(Object mvo) {
        return _multiValueIndexHandler.multiValueSize(mvo);
    }

    @Override
    protected Iterator<K> multiValueIterator(Object mvo) {
        return _multiValueIndexHandler.multiValueIterator(mvo);
    }


}
