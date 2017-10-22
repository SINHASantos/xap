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

//

package com.j_spaces.core.cache.offHeap.optimizations;

import com.j_spaces.core.cache.offHeap.OffHeapRefEntryCacheInfo;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Yael Nahon
 * @since 12.2
 */
public class OffHeapIndexesValuesHandler {

    private volatile static HTreeMap<String, byte[]> _mapDB;
    //private static final int numOfSegments=8;
    private static Logger logger = Logger.getLogger("MyLogger");

    private static HTreeMap<String, byte[]> getMapDB() {
        if (_mapDB == null) {
            Logger.getLogger("MyLogger").log(Level.INFO,"***** created mapDB instance *****");
            try {
                _mapDB = DBMaker
                        //TODO  * You should increase amount of direct memory with
                        //TODO  * {@code -XX:MaxDirectMemorySize=10G} JVM param
                        .memoryDirectDB() //off-heap
                        .make()

                        .hashMap("mapDB")
                        //.memoryShardedHashMap(numOfSegments)
                        .keySerializer(Serializer.STRING)
                        .valueSerializer(Serializer.BYTE_ARRAY)
                        .create();
            } catch (Exception e){
                throw new RuntimeException("getMapDB failed!");
            }
        }
        return _mapDB;
    }


    public static boolean allocate(String key){
        if(key != null) {
            try {
                getMapDB().put(key, "allocate".getBytes());
            } catch (Exception e) {
                throw new RuntimeException("allocate mapDB failed!");
            }
            Logger.getLogger("MyLogger").log(Level.INFO, "***** allocating off heap memory , key = " + key + " *****");
            return true;
        }else{
            return false;
        }
    }

    public static byte[] get(String key){
        if(key != null) {
            byte[] bytes = getMapDB().get(key);
            Logger.getLogger("MyLogger").log(Level.INFO, "***** reading off heap memory , key = " + key + " *****");
            return bytes;
        }else{
            throw new RuntimeException("get:: key is null!");
        }
    }


    public static void delete(String key){
        if(key != null) {
            getMapDB().remove(key);
            Logger.getLogger("MyLogger").log(Level.INFO, "***** freeing off heap memory , key = " + key + " *****");
        }
    }

    public static void update(String key){
        if(key != null) {
            getMapDB().replace(key, "update".getBytes());
            Logger.getLogger("MyLogger").log(Level.INFO, "***** updating off heap memory , key = " + key + " *****");
        }
    }

}
