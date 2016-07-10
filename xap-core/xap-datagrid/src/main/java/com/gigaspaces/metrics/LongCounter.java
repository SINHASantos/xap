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


package com.gigaspaces.metrics;

import com.gigaspaces.internal.backport.java.util.concurrent.atomic.LongAdder;

/**
 * An incrementing and decrementing counter metric for long values.
 *
 * @author Niv Ingberg
 * @since 10.1
 */

public class LongCounter extends Metric {
    private final LongAdder count;

    public LongCounter() {
        this.count = new LongAdder();
    }

    public LongCounter(LongAdder longAdder) {
        this.count = longAdder;
    }

    /**
     * Returns the counter's current value.
     *
     * @return the counter's current value
     */
    public long getCount() {
        return count.sum();
    }

    /**
     * Increment the counter by one.
     */
    public void inc() {
        count.add(1);
    }

    /**
     * Increment the counter by {@code n}.
     *
     * @param n the amount by which the counter will be increased
     */
    public void inc(long n) {
        count.add(n);
    }

    /**
     * Decrement the counter by one.
     */
    public void dec() {
        count.add(-1);
    }

    /**
     * Decrement the counter by {@code n}.
     *
     * @param n the amount by which the counter will be decreased
     */
    public void dec(long n) {
        count.add(-n);
    }

    /**
     * Resets the counter to zero.
     */
    public void reset() {
        count.reset();
    }
}
