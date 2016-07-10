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

/**
 *
 */
package com.j_spaces.jdbc.query;

import com.gigaspaces.internal.transport.IEntryPacket;
import com.j_spaces.jdbc.SelectColumn;

import java.util.Collection;


/**
 * @author anna
 * @since 7.1
 */
public interface IQueryResultSet<T extends IEntryPacket>
        extends Collection<T> {

    IQueryResultSet<T> newResultSet();

    Object getFieldValue(SelectColumn column, T entryPacket);

    IQueryResultSet<T> intersect(IQueryResultSet<T> set);

    IQueryResultSet<T> union(IQueryResultSet<T> set);

}