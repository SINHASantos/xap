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

package com.gigaspaces.internal.cluster.node.impl.backlog.globalorder;

import com.j_spaces.core.cluster.startup.RedoLogCompactionUtil;

/**
 * @since 8.0.1
 */
public abstract class AbstractSingleFileConfirmationHolder {

    private long _pendingErrorKey = -1;
    private Throwable _pendingError = null;
    private long _weight;
    private long _discardedPacketsCount;

    public AbstractSingleFileConfirmationHolder() {
    }

    public boolean setPendingError(long pendingErrorKey, Throwable error) {
        //This is not a pending error, because we have already advanced beyond it.
        if (getLastConfirmedKey() >= pendingErrorKey)
            return false;
        _pendingErrorKey = pendingErrorKey;
        _pendingError = error;
        return true;
    }

    public Throwable getPendingError() {
        return _pendingError;
    }

    public long getPendingErrorKey() {
        return _pendingErrorKey;
    }

    public boolean hasPendingError() {
        return _pendingErrorKey != -1;
    }

    public void clearPendingError() {
        _pendingError = null;
        _pendingErrorKey = -1;
    }

    public abstract long getLastConfirmedKey();

    @Override
    public String toString() {
        return "pendingErrorKey=" + _pendingErrorKey + ", pendingError=" + _pendingError;
    }

    public long getCalculatedWeight() {
        return RedoLogCompactionUtil.calculateWeight(_weight,_discardedPacketsCount);
    }

    public long getWeight() {
        return _weight;
    }

    public void setWeight(long _weight) {
        this._weight = _weight;
    }

    public long getDiscardedPacketsCount() {
        return _discardedPacketsCount;
    }

    public void setDiscardedPacketsCount(long _discardedPacketsCount) {
        this._discardedPacketsCount = _discardedPacketsCount;
    }
}