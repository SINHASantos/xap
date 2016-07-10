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

package com.gigaspaces.internal.cluster.node.impl.backlog.globalorder;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


@com.gigaspaces.api.InternalApi
public class GlobalOrderReliableAsyncBacklogHandshakeRequest
        extends GlobalOrderBacklogHandshakeRequest {
    private static final long serialVersionUID = 1L;
    private GlobalOrderReliableAsyncState _reliableAsyncState;

    public GlobalOrderReliableAsyncBacklogHandshakeRequest() {
    }

    public GlobalOrderReliableAsyncBacklogHandshakeRequest(
            GlobalOrderBacklogHandshakeRequest handshakeRequest,
            GlobalOrderReliableAsyncState reliableAsyncState) {
        super(handshakeRequest.isFirstHandshake(), handshakeRequest.getLastConfirmedKey());
        _reliableAsyncState = reliableAsyncState;
    }

    public GlobalOrderReliableAsyncState getReliableAsyncState() {
        return _reliableAsyncState;
    }

    @Override
    public String toLogMessage() {
        return super.toLogMessage() + " Reliable Async State " + _reliableAsyncState;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        _reliableAsyncState.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        _reliableAsyncState = new GlobalOrderReliableAsyncState();
        _reliableAsyncState.readExternal(in);
    }

}
