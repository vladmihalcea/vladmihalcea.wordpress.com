/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vladmihalcea.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseScriptLifecycleHandler implements InitializingBean, DisposableBean {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DatabaseScriptLifecycleHandler.class);

    private final Resource[] initScripts;
    private final Resource[] destroyScripts;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private String sqlScriptEncoding = "UTF-8";
    private String commentPrefix = "--";
    private boolean continueOnError;
    private boolean ignoreFailedDrops;
    private boolean transactional = true;

    public DatabaseScriptLifecycleHandler(DataSource dataSource,
                                          Resource[] initScripts,
                                          Resource[] destroyScripts) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.initScripts = initScripts;
        this.destroyScripts = destroyScripts;
    }

    public Resource[] getInitScripts() {
        return initScripts;
    }

    public Resource[] getDestroyScripts() {
        return destroyScripts;
    }

    public String getCommentPrefix() {
        return commentPrefix;
    }

    public void setCommentPrefix(String commentPrefix) {
        this.commentPrefix = commentPrefix;
    }

    public boolean isContinueOnError() {
        return continueOnError;
    }

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public boolean isIgnoreFailedDrops() {
        return ignoreFailedDrops;
    }

    public void setIgnoreFailedDrops(boolean ignoreFailedDrops) {
        this.ignoreFailedDrops = ignoreFailedDrops;
    }

    public String getSqlScriptEncoding() {
        return sqlScriptEncoding;
    }

    public void setSqlScriptEncoding(String sqlScriptEncoding) {
        this.sqlScriptEncoding = sqlScriptEncoding;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public void afterPropertiesSet() throws Exception {
        initDatabase();
    }

    public void destroy() throws Exception {
        destroyDatabase();
    }

    public void initDatabase() {
        if (transactional) {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    runInitScripts();
                    return null;
                }
            });
        } else {
            runInitScripts();
        }
    }

    private void runInitScripts() {
        final ResourceDatabasePopulator resourceDatabasePopulator = createResourceDatabasePopulator();
        jdbcTemplate.execute(new ConnectionCallback<Void>() {
            @Override
            public Void doInConnection(Connection con) throws SQLException, DataAccessException {
                resourceDatabasePopulator.setScripts(getInitScripts());
                resourceDatabasePopulator.populate(con);
                return null;
            }
        });
    }

    public void destroyDatabase() {
        if (transactional) {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    runDestroyScripts();
                    return null;
                }
            });
        } else {
            runDestroyScripts();
        }
    }

    private void runDestroyScripts() {
        final ResourceDatabasePopulator resourceDatabasePopulator = createResourceDatabasePopulator();
        jdbcTemplate.execute(new ConnectionCallback<Void>() {
            @Override
            public Void doInConnection(Connection con) throws SQLException, DataAccessException {
                resourceDatabasePopulator.setScripts(getDestroyScripts());
                resourceDatabasePopulator.populate(con);
                return null;
            }
        });
    }

    protected ResourceDatabasePopulator createResourceDatabasePopulator() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.setCommentPrefix(getCommentPrefix());
        resourceDatabasePopulator.setContinueOnError(isContinueOnError());
        resourceDatabasePopulator.setIgnoreFailedDrops(isIgnoreFailedDrops());
        resourceDatabasePopulator.setSqlScriptEncoding(getSqlScriptEncoding());
        return resourceDatabasePopulator;
    }

}
