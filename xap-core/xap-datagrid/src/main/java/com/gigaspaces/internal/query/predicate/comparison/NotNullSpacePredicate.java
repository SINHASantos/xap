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

package com.gigaspaces.internal.query.predicate.comparison;

import com.gigaspaces.internal.query.predicate.AbstractSpacePredicate;

/**
 * A predicate to test for non-null values. This predicate returns true if and only if the
 * predicate's argument is not null.
 *
 * @author Niv ingberg
 * @since 7.1
 */
@com.gigaspaces.api.InternalApi
public class NotNullSpacePredicate extends AbstractSpacePredicate {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for Externalizable.
     */
    public NotNullSpacePredicate() {
    }

    @Override
    public boolean execute(Object target) {
        return target != null;
    }

    @Override
    public String toString() {
        return "NOTNULL()";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null && this.getClass().equals(obj.getClass()));
    }
}
