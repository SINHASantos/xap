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

package com.gigaspaces.internal.cluster.node.impl.router.spacefinder;

import com.gigaspaces.internal.server.space.IRemoteSpace;

@com.gigaspaces.api.InternalApi
public class RemoteSpaceResult {
    private final IRemoteSpace _remoteSpace;
    private final String _endpointLookupName;

    public RemoteSpaceResult(IRemoteSpace remoteSpace, String endpointLookupName) {
        _remoteSpace = remoteSpace;
        _endpointLookupName = endpointLookupName;
    }

    public String getEndpointLookupName() {
        return _endpointLookupName;
    }

    public IRemoteSpace getRemoteSpace() {
        return _remoteSpace;
    }
}
