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

package com.gigaspaces.internal.cluster.node.impl.backlog.multisourcesinglefile;

import com.gigaspaces.internal.cluster.node.impl.ReplicationMultipleOperationType;
import com.gigaspaces.internal.cluster.node.impl.ReplicationSingleOperationType;
import com.gigaspaces.internal.cluster.node.impl.backlog.async.IReplicationAsyncGroupBacklog;
import com.gigaspaces.internal.cluster.node.impl.config.DynamicSourceGroupConfigHolder;
import com.gigaspaces.internal.cluster.node.impl.groups.async.IAsyncReplicationGroupOutContext;
import com.gigaspaces.internal.cluster.node.impl.packets.IReplicationOrderedPacket;
import com.gigaspaces.internal.cluster.node.impl.packets.data.IReplicationPacketDataProducer;
import com.gigaspaces.internal.server.storage.IEntryHolder;

import net.jini.core.transaction.server.ServerTransaction;

import java.util.ArrayList;

@com.gigaspaces.api.InternalApi
public class MultiSourceSingleFileAsyncGroupBacklog extends AbstractMultiSourceSingleFileGroupBacklog
        implements IReplicationAsyncGroupBacklog {

    public MultiSourceSingleFileAsyncGroupBacklog(
            DynamicSourceGroupConfigHolder groupConfig, String name,
            IReplicationPacketDataProducer<?> dataProducer) {
        super(groupConfig, name, dataProducer);
    }

    @Override
    public void add(IAsyncReplicationGroupOutContext groupContext,
                    IEntryHolder entryHolder,
                    ReplicationSingleOperationType operationType) {
        if (!hasExistingMember())
            return;

        IReplicationOrderedPacket packet = addSingleOperationPacket(groupContext,
                entryHolder,
                operationType);
        if (packet != null)
            groupContext.addOrderedPacket(packet);
    }

    @Override
    public void addGeneric(IAsyncReplicationGroupOutContext groupContext,
                           Object operationData, ReplicationSingleOperationType operationType) {
        if (!hasExistingMember())
            return;

        IReplicationOrderedPacket packet = addGenericOperationPacket(groupContext,
                operationData,
                operationType);
        if (packet != null)
            groupContext.addOrderedPacket(packet);
    }

    @Override
    public void addTransaction(IAsyncReplicationGroupOutContext groupContext,
                               ServerTransaction transaction,
                               ArrayList<IEntryHolder> lockedEntries,
                               ReplicationMultipleOperationType operationType) {
        if (!hasExistingMember())
            return;

        IReplicationOrderedPacket packet = addTransactionOperationPacket(groupContext,
                transaction,
                lockedEntries,
                operationType);
        if (packet != null)
            groupContext.addOrderedPacket(packet);
    }

}
