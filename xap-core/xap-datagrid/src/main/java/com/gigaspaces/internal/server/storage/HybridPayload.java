package com.gigaspaces.internal.server.storage;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.metadata.ITypeDesc;
import com.gigaspaces.internal.metadata.PropertyInfo;

import java.io.*;
import java.util.Arrays;

public class HybridPayload implements Externalizable {
    private Object[] serializedProperties;
    private Object[] nonSerializedProperties;
    private byte[] packedBinaryProperties;
    private boolean dirty;
    private boolean isDeserialized;
    private static final Object[] EMPTY_OBJECTS_ARRAY = new Object[0];
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public HybridPayload() {
        this.serializedProperties = EMPTY_OBJECTS_ARRAY;
        this.nonSerializedProperties = EMPTY_OBJECTS_ARRAY;
        this.packedBinaryProperties = EMPTY_BYTE_ARRAY;
        this.isDeserialized = true;
    }

    public HybridPayload(ITypeDesc typeDesc, Object[] nonSerializedProperties, byte[] packedBinaryProperties) {
        this.nonSerializedProperties = nonSerializedProperties;
        this.serializedProperties = new Object[typeDesc.getSerializedProperties().length];
        this.packedBinaryProperties = packedBinaryProperties;
        this.isDeserialized = false;
        this.dirty = false;
    }

    public HybridPayload(Object[] serializedProperties, Object[] nonSerializedProperties, byte[] packedBinaryProperties, boolean isDeserialized, boolean dirty) {
        this.serializedProperties = serializedProperties;
        this.nonSerializedProperties = nonSerializedProperties;
        this.packedBinaryProperties = packedBinaryProperties;
        this.isDeserialized = isDeserialized;
        this.dirty = dirty;
    }


    //wrap an object array with HybridBinaryData
    public HybridPayload(ITypeDesc typeDesc, Object[] values) {
        splitProperties(typeDesc, values);
        this.packedBinaryProperties = typeDesc.getClassBinaryStorageAdapter() != null ?
                serializeFields(typeDesc, this.serializedProperties) : EMPTY_BYTE_ARRAY;
        this.isDeserialized = true;
        this.dirty = false;
    }

    static byte[] serializeFields(ITypeDesc typeDesc, Object[] fieldsValues) {
        try {
            if (fieldsValues == null || fieldsValues.length == 0) {
                return EMPTY_BYTE_ARRAY;
            }
            return typeDesc.getClassBinaryStorageAdapter().toBinary(typeDesc, fieldsValues);
        } catch (IOException e) {
            throw new UncheckedIOException("com.gigaspaces.internal.server.storage.BinaryEntryData.serializeFields failed", e);
        }
    }

    public Object[] getFixedProperties(ITypeDesc typeDesc) {
        if (isDeserialized && this.nonSerializedProperties.length == 0 && this.serializedProperties.length == 0) {
            return this.nonSerializedProperties;
        }

        if (!isDeserialized && this.packedBinaryProperties.length > 0) {
            unpackSerializedProperties(typeDesc);
        }

        if(serializedProperties.length == 0){
            return this.nonSerializedProperties;
        }

        Object[] fields = new Object[typeDesc.getProperties().length];
        int i = 0;
        for (PropertyInfo property : typeDesc.getProperties()) {
            if (property.isBinarySpaceProperty()) {
                fields[i] = this.serializedProperties[typeDesc.findHybridIndex(i)];
            } else {
                fields[i] = this.nonSerializedProperties[typeDesc.findHybridIndex(i)];
            }
            i++;
        }
        return fields;

    }

    public Object getFixedProperty(ITypeDesc typeDesc, int position) {
        if (typeDesc.isBinaryProperty(position)) {
            if (!isDeserialized) {
                unpackSerializedProperties(typeDesc);
            }
            return serializedProperties[typeDesc.findHybridIndex(position)];
        } else {
            return nonSerializedProperties[typeDesc.findHybridIndex(position)];
        }
    }

    private void unpackSerializedProperties(ITypeDesc typeDesc) {
        try {
            this.serializedProperties = typeDesc.getClassBinaryStorageAdapter().fromBinary(typeDesc, packedBinaryProperties);
            isDeserialized = true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public HybridPayload clone() {
        Object[] serializedProps = this.serializedProperties == null ? null : Arrays.copyOf(this.serializedProperties, this.serializedProperties.length);
        Object[] nonSerializedProps = this.nonSerializedProperties == null ? null : Arrays.copyOf(this.nonSerializedProperties, this.nonSerializedProperties.length);
        byte[] packedBinaryProps = this.packedBinaryProperties == null ? null : Arrays.copyOf(this.packedBinaryProperties, this.packedBinaryProperties.length);
        return new HybridPayload(serializedProps,
                nonSerializedProps,
                packedBinaryProps, this.isDeserialized, this.dirty);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(isDeserialized);
        out.writeBoolean(dirty);
        if (isDeserialized && dirty) {
            IOUtils.writeObjectArrayCompressed(out, nonSerializedProperties);
            IOUtils.writeObjectArrayCompressed(out, serializedProperties);
        } else {
            IOUtils.writeObjectArrayCompressed(out, nonSerializedProperties);
            IOUtils.writeInt(out, serializedProperties.length);
            IOUtils.writeByteArray(out, packedBinaryProperties);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        isDeserialized = in.readBoolean();
        dirty = in.readBoolean();
        if (isDeserialized && dirty) {
            nonSerializedProperties = IOUtils.readObjectArrayCompressed(in);
            serializedProperties = IOUtils.readObjectArrayCompressed(in);
            this.packedBinaryProperties = EMPTY_BYTE_ARRAY;
        } else {
            nonSerializedProperties = IOUtils.readObjectArrayCompressed(in);
            serializedProperties = new Object[IOUtils.readInt(in)];
            packedBinaryProperties = IOUtils.readByteArray(in);
            isDeserialized = false;
        }
    }

    public boolean isDeserialized() {
        return isDeserialized;
    }

    public Object[] getNonSerializedProperties() {
        return nonSerializedProperties;
    }

    public byte[] getPackedBinaryProperties() {
        return packedBinaryProperties;
    }

    public Object[] getSerializedProperties() {
        return serializedProperties;
    }

    @Override
    public String toString() {
        return "HybridBinaryData{" +
                "serializedProperties=" + Arrays.toString(serializedProperties) +
                ", nonSerializedProperties=" + Arrays.toString(nonSerializedProperties) +
                ", packedBinaryProperties=" + Arrays.toString(packedBinaryProperties) +
                ", isDeserialized=" + isDeserialized +
                ", dirty=" + dirty +
                '}';
    }

    public boolean allNulls() {
        return this.serializedProperties == null && this.packedBinaryProperties == null && this.nonSerializedProperties == null;
    }

    public void copyFieldsArray() {
        Object[] src = nonSerializedProperties;
        Object[] target = new Object[src.length];
        System.arraycopy(src, 0, target, 0, src.length);
        this.nonSerializedProperties = target;
        if (isDeserialized) {
            src = serializedProperties;
            target = new Object[src.length];
            System.arraycopy(src, 0, target, 0, src.length);
            this.serializedProperties = target;
        }
    }

    public void setFixedProperties(ITypeDesc typeDescriptor, Object[] values) {
        splitProperties(typeDescriptor, values);
        this.isDeserialized = true;
        this.dirty = true;
    }

    public void setFixedProperty(ITypeDesc typeDesc, int position, Object value) {
        if (typeDesc.isBinaryProperty(position)) {
            if (!isDeserialized) {
                unpackSerializedProperties(typeDesc);
            }
            serializedProperties[typeDesc.findHybridIndex(position)] = value;
            dirty = true;
        } else {
            nonSerializedProperties[typeDesc.findHybridIndex(position)] = value;
        }
    }

    private void splitProperties(ITypeDesc typeDesc, Object[] values) {
        if(typeDesc.getSerializedProperties().length == 0){
            this.nonSerializedProperties = values == null ? EMPTY_OBJECTS_ARRAY : values;
            this.serializedProperties = EMPTY_OBJECTS_ARRAY;
            return;
        }

        this.nonSerializedProperties = new Object[typeDesc.getNonSerializedProperties().length];
        this.serializedProperties = new Object[typeDesc.getSerializedProperties().length];
        if(values != null && values.length > 0) {
            int nonSerializedFieldsIndex = 0;
            int serializedFieldsIndex = 0;
            for (PropertyInfo property : typeDesc.getProperties()) {
                if (property.getStorageType() != null && property.isBinarySpaceProperty()) {
                    this.serializedProperties[serializedFieldsIndex] = values[typeDesc.getFixedPropertyPosition(property.getName())];
                    serializedFieldsIndex++;
                } else {
                    this.nonSerializedProperties[nonSerializedFieldsIndex] = values[typeDesc.getFixedPropertyPosition(property.getName())];
                    nonSerializedFieldsIndex++;
                }
            }
        }
    }

    public void setFixedProperties(Object[] values) {
        this.nonSerializedProperties = values;
        this.serializedProperties = EMPTY_OBJECTS_ARRAY;
        this.isDeserialized = true;
        this.dirty = true;
    }

    public void setFixedProperty(int position, Object value) {
        this.nonSerializedProperties[position] = value;
    }

    public boolean isDirty() {
        return dirty;
    }
}
