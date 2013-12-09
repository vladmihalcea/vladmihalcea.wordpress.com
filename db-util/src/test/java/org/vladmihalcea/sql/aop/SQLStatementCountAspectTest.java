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

package org.vladmihalcea.sql.aop;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vladmihalcea.sql.exception.SQLStatementCountHolderAlreadyInitializedException;
import org.vladmihalcea.sql.exception.SQLStatementCountMismatchException;
import org.vladmihalcea.sql.service.CustomerService;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * SQLStatementCountAspectTest - SQLStatementCountAspect Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SQLStatementCountAspectTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLStatementCountAspect.class);

    @Autowired
    private CustomerService customerService;

    @Test
    public void testCountSuccess() {
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
        customerService.saveCustomerSuccess();
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }

    @Test
    public void testSQLStatementCountHolderAlreadyInitializedException() {
        QueryCount queryCount = new QueryCount();
        queryCount.setSelect(1);
        QueryCountHolder.put("jmsConnectionFactory", queryCount);
        try {
            customerService.saveCustomerSuccess();
            fail("Should have thrown SQLStatementCountHolderAlreadyInitializedException!");
        } catch (SQLStatementCountHolderAlreadyInitializedException expected) {
            LOGGER.error("Failed!", expected);
        }
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }

    @Test
    public void testCountSelectFailure() {
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
        try {
            customerService.saveCustomerSelectFailure();
            fail("Should have thrown SQLStatementCountMismatchException!");
        } catch (SQLStatementCountMismatchException expected) {
        }
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }

    @Test
    public void testCountInsertFailure() {
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
        try {
            customerService.saveCustomerInsertFailure();
            fail("Should have thrown SQLStatementCountMismatchException!");
        } catch (SQLStatementCountMismatchException expected) {
        }
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }

    @Test
    public void testCountUpdateFailure() {
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
        try {
            customerService.saveCustomerUpdateFailure();
            fail("Should have thrown SQLStatementCountMismatchException!");
        } catch (SQLStatementCountMismatchException expected) {
        }
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }

    @Test
    public void testCountDeleteFailure() {
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
        try {
            customerService.saveCustomerDeleteFailure();
            fail("Should have thrown SQLStatementCountMismatchException!");
        } catch (SQLStatementCountMismatchException expected) {
        }
        assertTrue(QueryCountHolder.getDataSourceNames().isEmpty());
    }
}
