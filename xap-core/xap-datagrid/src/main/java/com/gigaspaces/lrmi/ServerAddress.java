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

package com.gigaspaces.lrmi;

/**
 * A holder for remote LRMI server address
 *
 * @author eitany
 * @since 8
 */
@com.gigaspaces.api.InternalApi
public class ServerAddress {
    private final String _host;
    private final int _port;

    public ServerAddress(String host, int port) {
        if (host == null)
            throw new IllegalArgumentException("host cannot be null");
        this._host = host;
        this._port = port;
    }

    public String getHost() {
        return _host;
    }

    public int getPort() {
        return _port;
    }

    @Override
    public int hashCode() {
        return _host.hashCode() ^ _port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof ServerAddress))
            return false;


        ServerAddress other = (ServerAddress) obj;
        return _host.equals(other.getHost()) && _port == other.getPort();
    }

    @Override
    public String toString() {
        return _host + ":" + _port;
    }

}
