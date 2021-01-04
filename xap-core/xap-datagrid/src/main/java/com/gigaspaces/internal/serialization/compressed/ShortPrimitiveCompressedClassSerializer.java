package com.gigaspaces.internal.serialization.compressed;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.serialization.IClassSerializer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author Niv Ingberg
 * @since 15.8
 */
@com.gigaspaces.api.InternalApi
public class ShortPrimitiveCompressedClassSerializer implements IClassSerializer<Short> {
    private static final Short DEFAULT_VALUE = 0;

    @Override
    public byte getCode() {
        return CODE_SHORT;
    }

    @Override
    public void write(ObjectOutput out, Short obj) throws IOException {
        IOUtils.writeShort(out, obj);
    }

    @Override
    public Short read(ObjectInput in) throws IOException, ClassNotFoundException {
        return IOUtils.readShort(in);
    }

    @Override
    public Short getDefaultValue() {
        return DEFAULT_VALUE;
    }
}
