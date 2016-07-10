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

package com.gigaspaces.internal.serialization;

import com.gigaspaces.internal.io.IOUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Serializer for byte array.
 *
 * @author Niv Ingberg
 * @since 7.1
 */
@com.gigaspaces.api.InternalApi
public class ByteArrayClassSerializer implements IClassSerializer<byte[]> {
    public byte getCode() {
        return CODE_BYTE_ARRAY;
    }

    public byte[] read(ObjectInput in)
            throws IOException, ClassNotFoundException {
        return IOUtils.readByteArray(in);
    }

    public void write(ObjectOutput out, byte[] obj)
            throws IOException {
        IOUtils.writeByteArray(out, obj);
    }
}
