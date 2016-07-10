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

package com.gigaspaces.internal.cluster.node.impl.handlers;

import com.j_spaces.core.OperationID;

import net.jini.core.lease.UnknownLeaseException;


@com.gigaspaces.api.InternalApi
public class UnknownEntryLeaseException
        extends Exception {
    private static final long serialVersionUID = 1L;
    private final String _className;
    private final String _uid;
    private final OperationID _operationID;

    public UnknownEntryLeaseException(String className, String uid,
                                      OperationID operationID, UnknownLeaseException ex) {
        super(ex);
        _className = className;
        _uid = uid;
        _operationID = operationID;
    }

    public String getClassName() {
        return _className;
    }

    public String getUID() {
        return _uid;
    }

    public OperationID getOperationID() {
        return _operationID;
    }


}
