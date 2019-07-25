/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.shardingjdbc.jdbc.core.context;

import lombok.Getter;
import org.apache.shardingsphere.core.constant.properties.ShardingProperties;
import org.apache.shardingsphere.core.parse.SQLParseEngine;
import org.apache.shardingsphere.core.parse.SQLParseEngineFactory;
import org.apache.shardingsphere.core.rule.MasterSlaveRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.metadata.CachedDatabaseMetaData;
import org.apache.shardingsphere.spi.database.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Runtime context for master-slave.
 * 
 * @author zhangliang
 */
@Getter
public final class MasterSlaveRuntimeContext implements RuntimeContext {
    
    private final MasterSlaveRule rule;
    
    private final ShardingProperties shardingProperties;
    
    private final DatabaseMetaData cachedDatabaseMetaData;
    
    private final SQLParseEngine parseEngine;
    
    public MasterSlaveRuntimeContext(final Map<String, DataSource> dataSourceMap, final MasterSlaveRule rule, final Properties props, final DatabaseType databaseType) throws SQLException {
        this.rule = rule;
        shardingProperties = new ShardingProperties(null == props ? new Properties() : props);
        cachedDatabaseMetaData = createCachedDatabaseMetaData(dataSourceMap);
        parseEngine = SQLParseEngineFactory.getSQLParseEngine(databaseType);
    }
    
    private DatabaseMetaData createCachedDatabaseMetaData(final Map<String, DataSource> dataSourceMap) throws SQLException {
        try (Connection connection = dataSourceMap.values().iterator().next().getConnection()) {
            return new CachedDatabaseMetaData(connection.getMetaData(), dataSourceMap, null);
        }
    }
    
    @Override
    public void close() {
    }
}