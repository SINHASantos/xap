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

package com.gigaspaces.internal.cluster.node.impl.packets;

import com.gigaspaces.internal.cluster.node.impl.IIncomingReplicationFacade;
import com.gigaspaces.internal.cluster.node.impl.groups.IReplicationTargetGroup;
import com.gigaspaces.internal.cluster.node.impl.router.AbstractGroupNameReplicationPacket;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.utils.Textualizer;
import com.gigaspaces.internal.version.PlatformLogicalVersion;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

@com.gigaspaces.api.InternalApi
public class BatchReplicatedDataPacket
        extends AbstractGroupNameReplicationPacket<Object> {
    private static final long serialVersionUID = 1L;
    private List<IReplicationOrderedPacket> _batch;

    private transient boolean _clean = true;

    public BatchReplicatedDataPacket() {
    }

    public BatchReplicatedDataPacket(String groupName) {
        super(groupName);
    }

    @Override
    public Object accept(IIncomingReplicationFacade replicationFacade) {
        IReplicationTargetGroup targetGroup = replicationFacade.getReplicationTargetGroup(getGroupName());
        return targetGroup.processBatch(getSourceLookupName(), getSourceUniqueId(), _batch);
    }

    public void readExternalImpl(ObjectInput in, PlatformLogicalVersion endpointLogicalVersion) throws IOException,
            ClassNotFoundException {
        _batch = IOUtils.readObject(in);
    }

    public void writeExternalImpl(ObjectOutput out, PlatformLogicalVersion endpointLogicalVersion) throws IOException {
        IOUtils.writeObject(out, _batch);
    }

    public void setBatch(List<IReplicationOrderedPacket> batch) {
        if (!_clean)
            throw new IllegalStateException("Attempt to override packet batch when it was not released");
        _batch = batch;
        _clean = false;
    }

    public List<IReplicationOrderedPacket> getBatch() {
        return _batch;
    }

    public void clean() {
        _clean = true;
        _batch = null;
    }

    @Override
    public void toText(Textualizer textualizer) {
        super.toText(textualizer);
        textualizer.append("batch", _batch);
    }

}
