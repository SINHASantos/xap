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

package com.j_spaces.core.cache.blobStore.memory_pool;

import com.gigaspaces.internal.utils.concurrent.UnsafeHolder;
import com.gigaspaces.metrics.Gauge;
import com.gigaspaces.metrics.LongCounter;
import com.gigaspaces.metrics.MetricRegistrator;
import com.j_spaces.core.cache.blobStore.BlobStoreRefEntryCacheInfo;
import com.j_spaces.core.cache.blobStore.IBlobStoreOffHeapInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yael Nahon
 * @since 12.2
 */
public class OffHeapMemoryPool extends AbstractMemoryPool {

    private Logger logger = LoggerFactory.getLogger(com.gigaspaces.logger.Constants.LOGGER_CACHE);
    private int minimalDiffToAllocate;
    private final LongCounter totalCounter = new LongCounter();
    private final Map<Short, LongCounter> typesCounters = new ConcurrentHashMap<Short, LongCounter>();

    public OffHeapMemoryPool(long threshold) {
        super(threshold);
        if (!UnsafeHolder.isAvailable()) {
            throw new RuntimeException(" unsafe instance could not be obtained");
        }
    }

    public void setMinimalDiffToAllocate(int minimalDiffToAllocate) {
        this.minimalDiffToAllocate = minimalDiffToAllocate;
    }

    @Override
    public void initMetrics(MetricRegistrator metricRegistrator) {
        setMetricRegistrator(metricRegistrator);
        getMetricRegistrator().register(metricsPath("total"), totalCounter);
        getMetricRegistrator().register(percent(), createPercentGauge());
    }

    private Gauge<Double> createPercentGauge() {
        return new Gauge<Double>() {
            @Override
            public Double getValue() {
                return threshold != 0 ? ( double )totalCounter.getCount()/threshold : 0;
            }
        };
    }

    @Override
    public void register(String typeName, short typeCode) {
        LongCounter counter = new LongCounter();
        typesCounters.put(typeCode, counter);
        getMetricRegistrator().register(metricsPath(typeName), counter);
    }

    @Override
    public void unregister(String typeName, short typeCode) {
        typesCounters.remove(typeCode);
        getMetricRegistrator().unregisterByPrefix(metricsPath(typeName));
    }

    @Override
    public long getUsedBytes() {
        return totalCounter.getCount();
    }

    @Override
    public void write(IBlobStoreOffHeapInfo info, byte[] buf) {
        allocateAndWriteImpl(info, buf, false);
    }

    @Override
    public byte[] get(IBlobStoreOffHeapInfo info) {
        if (info.getOffHeapAddress() == BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY) {
            throw new IllegalStateException("trying to read from off heap but no address found");
        }
        int headerSize = getHeaderSizeFromUnsafe(info.getOffHeapAddress());
        int numOfBytes = getHeaderFromUnsafe(info.getOffHeapAddress(), headerSize);
        return readBytes(info.getOffHeapAddress() + (long) (headerSize), numOfBytes);
    }

    @Override
    public void update(IBlobStoreOffHeapInfo info, byte[] buf) {
        if (info.getOffHeapAddress() == BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY) {
            throw new IllegalStateException("trying to update when no off heap memory is allocated");
        }
        int oldHeaderSize = getHeaderSizeFromUnsafe(info.getOffHeapAddress());
        int oldEntryLength = getHeaderFromUnsafe(info.getOffHeapAddress(), oldHeaderSize);
        if (oldEntryLength < buf.length || (oldEntryLength - buf.length >= minimalDiffToAllocate)) {
            deleteImpl(info, true);
            allocateAndWriteImpl(info, buf, true);
        } else {
            writeBytes(info.getOffHeapAddress() + (long) (oldHeaderSize), buf);
        }
    }

    @Override
    public void delete(IBlobStoreOffHeapInfo info) {
        deleteImpl(info, false);
    }

    @Override
    public boolean isPmem() {
        return false;
    }

    @Override
    public boolean isOffHeap() {
        return true;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("OffHeapMemoryPool.close() is not supported");
    }

    private void incrementMetrics(long n, short typeCode) {
        totalCounter.inc(n);
        LongCounter typeCounter = typesCounters.get(typeCode);
        if (typeCounter != null)
            typeCounter.inc(n);
    }

    private void decrementMetrics(long n, short typeCode) {
        totalCounter.dec(n);
        LongCounter typeCounter = typesCounters.get(typeCode);
        if (typeCounter != null)
            typeCounter.dec(n);
    }

    private void allocateAndWriteImpl(IBlobStoreOffHeapInfo info, byte[] buf, boolean fromUpdate) {
        long newAddress;

        if (info.getOffHeapAddress() == BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY) {
            fromUpdate = false;
        }

        if (info.getOffHeapAddress() != BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY && !fromUpdate) {
            throw new IllegalStateException("trying to allocateAndWrite when already allocated in off heap");
        }

        int headerSize = calculateHeaderSize(buf.length);
        try {
            if (fromUpdate) {
                newAddress = UnsafeHolder.reallocateMemory(info.getOffHeapAddress(), headerSize + buf.length);
            } else {
                newAddress = UnsafeHolder.allocateMemory(headerSize + buf.length);
            }

        } catch (Error e) {
            logger.error("failed to allocateAndWrite offheap space", e);
            throw e;
        } catch (Exception e) {
            logger.error("failed to allocateAndWrite offheap space");
            throw new RuntimeException("failed to allocateAndWrite offheap space", e);
        }
        if (newAddress == 0) {
            logger.error("failed to allocateAndWrite offheap space");
            throw new RuntimeException("failed to allocateAndWrite offheap space");
        }

        putHeaderToUnsafe(newAddress, buf.length);
        writeBytes(newAddress + headerSize, buf);
        info.setOffHeapAddress(newAddress);
        incrementMetrics(headerSize + buf.length, info.getServerTypeDescCode());
    }

    private void deleteImpl(IBlobStoreOffHeapInfo info, boolean fromUpdate) {
        long valuesAddress = info.getOffHeapAddress();
        if (valuesAddress != BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY) {
            int headerSize = getHeaderSizeFromUnsafe(info.getOffHeapAddress());
            int numOfBytes = getHeaderFromUnsafe(valuesAddress, headerSize);
            if (!fromUpdate) {
                UnsafeHolder.freeFromMemory(valuesAddress);
                info.setOffHeapAddress(BlobStoreRefEntryCacheInfo.UNALLOCATED_OFFHEAP_MEMORY);
            }
            decrementMetrics(headerSize + numOfBytes, info.getServerTypeDescCode());
        }
    }

    private static void writeBytes(long address, byte[] bytes) {
        UnsafeHolder.copyByteArrayToMemory(bytes, address, bytes.length);
    }

    private static byte[] readBytes(long address, int numOfBytes) {
        byte[] res = new byte[numOfBytes];
        UnsafeHolder.copyByteArrayFromMemory(res, address, numOfBytes);
        return res;
    }

    private static int calculateHeaderSize(int bufferLen) {
        if (bufferLen > Integer.MAX_VALUE / 2)
            throw new RuntimeException("Illegal buffer length =" + bufferLen);
        for (int left = 3; left >= 0; left--) {
            int next = (bufferLen >>> (left * 8)) & 0xFF;
            if (next != 0) {
                if ((next & 0x0C0) != 0)
                    return (left + 2);
                else
                    return left + 1;
            }
        }
        throw new RuntimeException("Illegal buffer length =" + bufferLen);
    }

    private static int putHeaderToUnsafe(long address, int bufferLen) {
        int headerSize = 0;
        boolean started = false;
        for (int left = 3; left >= 0; left--) {
            int next = (bufferLen >>> (left * 8)) & 0xFF;
            if (!started) {
                if (next == 0)
                    continue;
                if ((next & 0x0C0) != 0) {
                    headerSize = left + 1;
                    UnsafeHolder.putByte(address, (byte) (headerSize << 6));
                    address++;
                } else {
                    headerSize = left;
                    next |= ((headerSize << 6));
                }
                headerSize++;
                started = true;
            }
            UnsafeHolder.putByte(address, (byte) (next));
            address++;
        }
        return headerSize;
    }

    private static int getHeaderSizeFromUnsafe(long address) {
        return (((UnsafeHolder.getByte(address)) & 0xC0) >> 6) + 1;
    }

    private static int getHeaderFromUnsafe(long address, int headerSize) {
        int len = 0;
        for (int i = 0; i < headerSize; i++) {
            int intByte = UnsafeHolder.getByte(address);
            if (i == 0)
                intByte &= 0x3F;
            else
                intByte &= 0xFF;
            len |= (intByte << ((headerSize - 1 - i) * 8));
            address++;
        }
        return len;
    }
}
