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

package com.j_spaces.map;

import com.gigaspaces.internal.client.cache.SpaceCacheException;
import com.j_spaces.core.IJSpace;

import java.rmi.RemoteException;

/**
 * @author Niv Ingberg
 * @since 8.0.5
 */
@com.gigaspaces.api.InternalApi
public class MapEntryFactory {
    public static void initialize(IJSpace space) {
        try {
            space.snapshot(MapEntryFactory.create());
        } catch (RemoteException e) {
            throw new SpaceCacheException("Failed to initialize space cache", e);
        }
    }

    public static SpaceMapEntry create() {
        return create(null, null, null);
    }

    public static SpaceMapEntry create(Object key) {
        return create(key, null, null);
    }

    public static SpaceMapEntry create(Object key, Object value) {
        return create(key, value, null);
    }

    public static SpaceMapEntry create(Object key, Object value, String cacheID) {
        return new Envelope(key, value, cacheID);
    }
}
