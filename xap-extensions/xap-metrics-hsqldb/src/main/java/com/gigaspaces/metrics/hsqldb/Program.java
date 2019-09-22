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
package com.gigaspaces.metrics.hsqldb;

import com.gigaspaces.metrics.*;
import com.gigaspaces.metrics.reporters.ConsoleReporterFactory;
import com.gigaspaces.metrics.reporters.FileReporterFactory;

import java.util.*;

public class Program {
    public static void main(String[] args) throws Exception {

        MetricRegistry registry = new MetricRegistry("test");
        MetricTags tags = new MetricTags(Collections.singletonMap("host", "my-host"));
        LongCounter metric1 = new LongCounter();
        LongCounter metric2 = new LongCounter();
        registry.register("counter1", tags, metric1);
        registry.register("counter2", tags, metric2);

        Collection<MetricReporter> reporters = new ArrayList<>();
        reporters.add(createConsoleReporter());
        //reporters.add(createFileReporter("/path/to/metrics.txt"));

        long timestamp = System.currentTimeMillis();
        for (int i=0 ; i < 10 ; i++) {
            metric1.inc(10 + i);
            metric2.inc(100 + i);
            List<MetricRegistrySnapshot> snapshotList = Collections.singletonList(registry.snapshot(timestamp + i));
            for (MetricReporter reporter : reporters) {
                reporter.report(snapshotList);
            }
        }
    }

    private static MetricReporter createConsoleReporter() throws Exception {
        return new ConsoleReporterFactory().create();
    }

    private static MetricReporter createFileReporter(String path) throws Exception {
        Properties properties = new Properties();
        FileReporterFactory factory = new FileReporterFactory();
        factory.load(properties);
        return factory.create();
    }
}
