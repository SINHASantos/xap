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

import java.rmi.MarshalledObject;
import java.rmi.activation.ActivationID;

/**
 * Convenience class intended for use with the {@link com.sun.jini.start.ServiceStarter} framework
 * to start an implementation of Mahalo that is activatable, and which will log its state
 * information to persistent storage.
 *
 * @author Sun Microsystems, Inc.
 * @since 2.0
 */
class ActivatableMahaloImpl extends TxnManagerImpl {

    /**
     * Constructs a new instance of <code>TxnManagerImpl</code> that is activatable, and which will
     * persist its state. <p> A constructor having this signature is required for the class to be
     * activatable. This constructor is automatically called by the activation group when the
     * service is activated.
     *
     * @param activationID the activation ID generated by the activation system and assigned to the
     *                     instance of the server being activated
     * @param data         state data (represented as a <code>MarshalledObject</code>) which is
     *                     needed to re-activate this server
     * @throws Exception If there was a problem initializing the service.
     */
    ActivatableMahaloImpl(ActivationID activationID, MarshalledObject data)
            throws Exception {
        super(activationID, data);
    }//end constructor

}//end class ActivatableMahaloImpl

