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

package com.gigaspaces.internal.client.mutators;

import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.utils.CollectionUtils;
import com.gigaspaces.internal.utils.Textualizer;
import com.gigaspaces.server.MutableServerEntry;
import com.gigaspaces.sync.change.RemoveFromMapOperation;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Map;

/**
 * The set of changes to apply for matches entries of the change operation
 *
 * @author eitany
 * @since 9.1
 */
public final class RemoveFromMapSpaceEntryMutator
        extends SpaceEntryPathMutator {

    private static final long serialVersionUID = 1L;
    private Serializable _key;

    public RemoveFromMapSpaceEntryMutator() {
    }

    public RemoveFromMapSpaceEntryMutator(String path, Serializable key) {
        super(path);
        _key = key;
    }

    @Override
    public Object change(MutableServerEntry entry) {
        Map oldValue = (Map) entry.getPathValue(getPath());
        if (oldValue == null)
            throw new IllegalStateException("No map instance exists under the given path '" + getPath() + "', in order to remove a map instance must exists");
        Map newValue = CollectionUtils.cloneMap(oldValue);
        Object removedValue = newValue.remove(_key);
        int size = newValue.size();
        entry.setPathValue(getPath(), newValue);
        return new MapChangeSpaceEntryMutatorResult((Serializable) removedValue, size);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        IOUtils.writeObject(out, _key);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        _key = IOUtils.readObject(in);
    }

    @Override
    public void toText(Textualizer textualizer) {
        super.toText(textualizer);
        textualizer.append("key", _key);
    }

    @Override
    public String getName() {
        return RemoveFromMapOperation.NAME;
    }

    public Serializable getKey() {
        return _key;
    }

}
