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


package com.j_spaces.kernel.list;

import com.j_spaces.kernel.IStoredList;
import com.j_spaces.kernel.IStoredListIterator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO	add Javadoc
 *
 * @author Yechiel Fefer
 * @version 1.0
 * @since 8.03
 */
/*
 * scan iterator for a single S.L./single object
 * NOTE !!!- for single threaded use
 */
@com.gigaspaces.api.InternalApi
public class ScanSingleListIterator<T>
        implements IScanListIterator<T> {

    private IStoredList<T> _list;

    private T _nextObj;

    private boolean _singleObjectResult;

    private boolean _gotFirst;

    private IStoredListIterator<T> _pos;

    private final boolean _fifoScan;

    private final boolean _alternatingThread;
    private final AtomicInteger _alternatingThreadBarrier; //pass thru volatile

    public ScanSingleListIterator(IStoredList<T> list, boolean fifoScan) {
        this(list,fifoScan,false);
    }
    public ScanSingleListIterator(IStoredList<T> list, boolean fifoScan,boolean alternatingThread) {
        _list = list;
        _fifoScan = fifoScan;
        _alternatingThread = alternatingThread;
        if (_alternatingThread) {
            _alternatingThreadBarrier = new AtomicInteger(0);
            _alternatingThreadBarrier.incrementAndGet(); //set to 1 bypass jvm optimizations
        }
        else
            _alternatingThreadBarrier = null;

    }

    @Override
    public boolean hasSize() {
        return true;
    }

    @Override
    public int size() {
        return _list.size();
    }

    /*
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        //a dummy check just to go thru volatile barrier
        if (_alternatingThreadBarrier != null && _alternatingThreadBarrier.get() == 0)
             throw new RuntimeException("internal error alternating thread");
        try {
            if (_gotFirst && _singleObjectResult)
                return false;
            if (!_gotFirst) {
                if (_list.isMultiObjectCollection()) {
                    if (_list.optimizeScanForSingleObject()) {
                        _singleObjectResult = true;
                        _nextObj = _list.getObjectFromHead();
                    } else {
                        if (_alternatingThread)
                            _pos = _list.establishListScan(!_fifoScan, _alternatingThread);
                        else
                            _pos = _list.establishListScan(!_fifoScan);
                        _nextObj = getNext();
                    }
                } else {
                    _singleObjectResult = true;
                    _nextObj = (T) _list;
                }
                _gotFirst = true;
            } else {
                _pos = _list.next(_pos);
                _nextObj = getNext();
            }
            return _nextObj != null;
        }
        finally
        {
            if (_alternatingThreadBarrier != null )
                _alternatingThreadBarrier.incrementAndGet(); //set to 1 bypass jvm optimizations
        }
    }

    /**
     * Advance the iterator until a valid data is reached. some of the entries can be null due to
     * deletion, so they need to be skipped.
     */
    private T getNext() {
        if (_alternatingThreadBarrier != null && _alternatingThreadBarrier.get() == 0) //barrier
            //a dummy check just to go thru volatile barrier
            throw new RuntimeException("internal error alternating thread");
        try {
            while (_pos != null) {
                T nextObj = _pos.getSubject();
                if (nextObj != null)
                    return nextObj;

                _pos = _list.next(_pos);
            }
            return null;
        }
        finally
        {
            if (_alternatingThreadBarrier != null )
                _alternatingThreadBarrier.incrementAndGet(); //set to 1 bypass jvm optimizations
        }
    }

    /*
     * @see java.util.Iterator#next()
     */
    public T next() {
        // TODO Auto-generated method stub
        return _nextObj;
    }

    /*
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();

    }

    /**
     * release SLHolder for this scan
     */
    public void releaseScan() {
        if (!_singleObjectResult && _pos != null)
            _list.freeSLHolder(_pos);
    }

    //TBD- we can optimize here
    public int getAlreadyMatchedFixedPropertyIndexPos() {
        return -1;
    }

    public boolean isAlreadyMatched() {
        return false;
    }

    public boolean isIterator() {
        return true;
    }

    //reuse this object for scanning of another list
    public void reuse(IStoredList<T> list) {
        clean();
        _list = list;

    }

    private void clean() {
        _list = null;
        _nextObj = null;
        _singleObjectResult = false;
        _gotFirst = false;
        _pos = null;
    }

}
