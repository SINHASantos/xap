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

package com.gigaspaces.internal.collections;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * @author Niv Ingberg
 * @since 12.0
 */
public interface ObjectShortMap<K> {
    boolean containsKey(K key);

    short get(K key);

    void put(K key, short value);

    void serialize(ObjectOutput out) throws IOException;
}
