/*
 * 
 * Copyright 2005 Sun Microsystems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.sun.jini.mahalo;

/**
 * This exception denotes a problem with the transaction manager implementation.
 *
 * @author Sun Microsystems, Inc.
 */
class InternalManagerException extends RuntimeException {
    static final long serialVersionUID = -8249846562594717389L;

    InternalManagerException() {
        super();
    }

    InternalManagerException(String msg) {
        super(msg);
    }
}
