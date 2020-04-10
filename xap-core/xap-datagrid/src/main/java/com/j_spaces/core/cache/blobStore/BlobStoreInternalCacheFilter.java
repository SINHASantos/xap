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

package com.j_spaces.core.cache.blobStore;

import com.gigaspaces.cluster.activeelection.SpaceInitializationIndicator;
import com.gigaspaces.internal.client.spaceproxy.metadata.ObjectType;
import com.gigaspaces.internal.server.metadata.IServerTypeDesc;
import com.gigaspaces.internal.server.space.SpaceEngine;
import com.gigaspaces.internal.server.storage.IEntryHolder;
import com.gigaspaces.internal.server.storage.ITemplateHolder;
import com.gigaspaces.internal.server.storage.TemplateEntryData;
import com.gigaspaces.internal.server.storage.TemplateHolderFactory;
import com.gigaspaces.internal.transport.ITemplatePacket;
import com.gigaspaces.metrics.LongCounter;
import com.j_spaces.core.cache.context.Context;
import com.j_spaces.core.client.EntrySnapshot;
import com.j_spaces.core.client.SQLQuery;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kobi on 20/12/16.
 * @since 12.1
 */
public class BlobStoreInternalCacheFilter {
    private static final Logger _logger = LoggerFactory.getLogger(com.gigaspaces.logger.Constants.LOGGER_CACHE);

    final private SpaceEngine _spaceEngine;
    final private List<SQLQuery> _sqlQueries;
    final private List<ITemplateHolder> _templateHolders;

    private final LongCounter _insertedToBlobStoreInternalCache = new LongCounter();

    private final LongCounter _coldDataMiss = new LongCounter();


    public BlobStoreInternalCacheFilter(SpaceEngine spaceEngine, List<SQLQuery> sqlQueries) throws Exception {
        this._spaceEngine = spaceEngine;
        this._sqlQueries = sqlQueries;
        _templateHolders = new ArrayList<ITemplateHolder>(_sqlQueries.size());
        for(SQLQuery sqlQuery : _sqlQueries){
            _templateHolders.add(convertSQLQueryToTemplateHolder(sqlQuery));
        }
    }

    private ITemplateHolder convertSQLQueryToTemplateHolder(SQLQuery sqlQuery) throws Exception {
        final ObjectType queryObjectType = ObjectType.fromObject(sqlQuery, true);

        final ITemplatePacket templatePacket = _spaceEngine.getSpaceImpl()
                .getSingleProxy().getTypeManager().getTemplatePacketFromObject(sqlQuery, queryObjectType);

        try {
            //mark current thread as initializer thread to allow snapshot operation for Space in state STARTING
            SpaceInitializationIndicator.setInitializer();

            final ITemplatePacket fullTemplatePacket = ((EntrySnapshot<?>) _spaceEngine.getSpaceImpl()
                    .getSingleProxy().snapshot(templatePacket)).getTemplatePacket();

            final IServerTypeDesc typeDesc = _spaceEngine.getTypeManager()
                    .loadServerTypeDesc(templatePacket);

            return TemplateHolderFactory.createTemplateHolder(typeDesc, fullTemplatePacket, null, Long.MAX_VALUE);

        } finally {
            SpaceInitializationIndicator.unsetInitializer();
        }
    }

    public boolean isEntryHotData(IEntryHolder eh, Context context){
        for(ITemplateHolder templateHolder : _templateHolders){
            if(((TemplateEntryData)templateHolder.getEntryData()).isAssignableFrom(eh.getServerTypeDesc()))
                if(_spaceEngine.getTemplateScanner().match(context, eh, templateHolder)) {
                    return true;
                }
        }
        return false;
    }

    public List<SQLQuery> getSqlQueries() {
        return _sqlQueries;
    }

    public long getInsertedToBlobStoreInternalCacheOnInitialLoad() {
        return _insertedToBlobStoreInternalCache.getCount();
    }

    public void incrementInsertedToBlobStoreInternalCacheOnInitialLoad() {
        _insertedToBlobStoreInternalCache.inc();
    }

    public boolean isRelevantType(String entryTypeName){
        final IServerTypeDesc serverTypeDesc = _spaceEngine.getTypeManager().getServerTypeDesc(entryTypeName);
        if(serverTypeDesc == null) {
            return true;
        }
        for(ITemplateHolder templateHolder : _templateHolders){
            if(((TemplateEntryData)templateHolder.getEntryData()).isAssignableFrom(serverTypeDesc)){
                return true;
            }
        }
        return false;
    }

    public LongCounter getColdDataMissCount(){
        return _coldDataMiss;
    }

    public void incrementColdDataMisses(){
        _coldDataMiss.inc();
    }
}
