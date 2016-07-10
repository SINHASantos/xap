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

package com.gigaspaces.internal.cluster.node.impl.backlog.sync;

import com.gigaspaces.internal.cluster.node.impl.backlog.IReplicationGroupBacklog;

import java.io.Externalizable;

/**
 * A wire form of {@link IMarker} which can be serialized and deserialized
 *
 * @author eitany
 * @since 9.1
 */
public interface IMarkerWireForm extends Externalizable {
    IMarker toFinalizedForm(IReplicationGroupBacklog backlog);

    String getGroupName();
}
