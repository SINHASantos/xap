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


package com.gigaspaces.cluster.replication.gateway.conflict;

/**
 * A {@link ConflictCause} implementation for an entry not in space conflict. <p> Occurs when
 * there's an attempt to update an entry which doesn't exist in the target space. </p>
 *
 * @author idan
 * @since 8.0.3
 */

public class EntryNotInSpaceConflict
        extends ConflictCause {

    @Override
    public String toString() {
        return "entry is not in space";
    }

}
