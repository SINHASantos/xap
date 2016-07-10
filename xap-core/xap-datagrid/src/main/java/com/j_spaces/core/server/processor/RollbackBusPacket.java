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

package com.j_spaces.core.server.processor;


import com.gigaspaces.internal.server.storage.IEntryHolder;
import com.j_spaces.core.OperationID;
import com.j_spaces.core.XtnEntry;

/**
 * A RollbackBusPacket represents a transaction rollback.
 */
@com.gigaspaces.api.InternalApi
public class RollbackBusPacket extends BusPacket<Processor> {
    final XtnEntry _xtnEntry;

    public RollbackBusPacket(XtnEntry xtnEntry) {
        super((OperationID) null, (IEntryHolder) null, xtnEntry.m_Transaction, 0);
        _xtnEntry = xtnEntry;
    }

    @Override
    public void execute(Processor processor) throws Exception {
        processor.handleRollbackSA(this);
    }
}

