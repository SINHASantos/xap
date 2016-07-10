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

package com.gigaspaces.metadata.pojos;

import com.gigaspaces.annotation.pojo.SpaceStorageType;
import com.gigaspaces.metadata.StorageType;

@com.gigaspaces.api.InternalApi
public class PojoIllegalStorageTypeDeclarationLocation {
    public class OnSet {
        Object object;

        public Object getObject() {
            return object;
        }

        @SpaceStorageType(storageType = StorageType.BINARY)
        public void setObject(Object object) {
            this.object = object;
        }
    }

    public class OnBothGetAndSet {
        Object object;

        @SpaceStorageType(storageType = StorageType.BINARY)
        public Object getObject() {
            return object;
        }

        @SpaceStorageType(storageType = StorageType.BINARY)
        public void setObject(Object object) {
            this.object = object;
        }
    }

    public class OnSomeMethod {
        Object object;

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        @SpaceStorageType(storageType = StorageType.BINARY)
        public Object someMethod() {
            return null;
        }
    }

    public class OnBothGetAndSomeMethod {
        Object object;

        @SpaceStorageType(storageType = StorageType.BINARY)
        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        @SpaceStorageType(storageType = StorageType.BINARY)
        public Object someMethod() {
            return null;
        }
    }
}
