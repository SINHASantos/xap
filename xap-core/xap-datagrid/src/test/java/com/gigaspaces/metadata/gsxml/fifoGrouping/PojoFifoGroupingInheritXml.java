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

package com.gigaspaces.metadata.gsxml.fifoGrouping;

import com.gigaspaces.metadata.index.SpaceIndexType;

import java.util.HashMap;
import java.util.Map;

@com.gigaspaces.api.InternalApi
public class PojoFifoGroupingInheritXml extends PojoBasicFifoGroupingXml {
    public static Map<String, SpaceIndexType> getIndexes() {
        Map<String, SpaceIndexType> indexes = new HashMap<String, SpaceIndexType>();
        indexes.put("id", SpaceIndexType.BASIC);
        indexes.put("symbol", SpaceIndexType.EXTENDED);
        indexes.put("reporter", SpaceIndexType.EXTENDED);
        indexes.put("processed", SpaceIndexType.BASIC);
        indexes.put("info", SpaceIndexType.BASIC);
        indexes.put("info.timeStamp", SpaceIndexType.EXTENDED);
        indexes.put("info.scans", SpaceIndexType.BASIC);
        indexes.put("formerReporters", SpaceIndexType.BASIC);
        indexes.put("time.nanos", SpaceIndexType.BASIC);
        return indexes;
    }

    public static String[] getFifoGroupingIndexes() {
        String[] res = {"reporter", "processed", "info", "info.scans", "time.nanos"};
        return res;
    }
}
