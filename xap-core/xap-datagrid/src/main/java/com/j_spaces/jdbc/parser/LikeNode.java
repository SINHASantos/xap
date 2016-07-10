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


package com.j_spaces.jdbc.parser;

import com.j_spaces.core.client.TemplateMatchCodes;
import com.j_spaces.jdbc.builder.QueryTemplateBuilder;
import com.j_spaces.sadapter.datasource.DefaultSQLQueryBuilder;

import java.sql.SQLException;

/**
 * This is the LIKE operator Node. can also represent NOT LIKE
 *
 * @author Michael Mitrani, 2Train4, 2004
 */
@com.gigaspaces.api.InternalApi
public class LikeNode extends ExpNode {


    public LikeNode() {
        super();

    }

    @Override
    public boolean isValidCompare(Object ob1, Object ob2) throws ClassCastException {
        return false;
    }


    /* (non-Javadoc)
     * @see com.j_spaces.jdbc.parser.ExpNode#newInstance()
     */
    @Override
    public ExpNode newInstance() {
        return new LikeNode();
    }

    /* (non-Javadoc)
     * @see com.j_spaces.jdbc.parser.ExpNode#accept(com.j_spaces.jdbc.builder.QueryTemplateBuilder)
     */
    @Override
    public void accept(QueryTemplateBuilder builder) throws SQLException {

        builder.build(this, TemplateMatchCodes.REGEX);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return toString(DefaultSQLQueryBuilder.mapCodeToSign(TemplateMatchCodes.REGEX));
    }


}
