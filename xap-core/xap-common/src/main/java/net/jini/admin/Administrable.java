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

package net.jini.admin;

/**
 * Every administrable service should implement this interface.
 *
 * @author Sun Microsystems, Inc.
 */
public interface Administrable {
    /**
     * Returns an object that implements whatever administration interfaces are appropriate for the
     * particular service.
     *
     * @return an object that implements whatever administration interfaces are appropriate for the
     * particular service.
     * @see JoinAdmin
     */
    Object getAdmin() throws java.rmi.RemoteException;
}
