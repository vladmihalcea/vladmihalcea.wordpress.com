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

package org.vladmihalcea;

import net.ttddyy.dsproxy.QueryCountHolder;
import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.vladmihalcea.hibernate.model.store.*;
import org.vladmihalcea.service.WarehouseProductInfoService;
import org.vladmihalcea.sql.exception.SQLStatementCountMismatchException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateSQLStatementCountTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateSQLStatementCountTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WarehouseProductInfoService warehouseProductInfoService;

    @Before
    public void beforeTest() {
        CleanDbUtil.cleanStore(transactionTemplate, entityManager);
    }

    @Test
    public void test() {

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product1 = new Product("tvCode");
                product1.setName("TV");
                product1.setCompany(company);

                WarehouseProductInfo warehouseProductInfo1 = new WarehouseProductInfo();
                warehouseProductInfo1.setQuantity(101);
                product1.addWarehouse(warehouseProductInfo1);

                Product product2 = new Product("cdCode");
                product2.setName("CD");
                product2.setCompany(company);

                WarehouseProductInfo warehouseProductInfo2 = new WarehouseProductInfo();
                warehouseProductInfo2.setQuantity(50);
                product2.addWarehouse(warehouseProductInfo2);

                entityManager.persist(product1);
                entityManager.persist(product2);
                entityManager.flush();
                return null;
            }
        });

        try {
            QueryCountHolder.clear();
            warehouseProductInfoService.findAllWithNPlusOne();
        } catch (SQLStatementCountMismatchException expected) {
            assertEquals(3, expected.getQueryCount().getSelect());
        }

        QueryCountHolder.clear();
        warehouseProductInfoService.findAllWithFetch();

        QueryCountHolder.clear();
        warehouseProductInfoService.newWarehouseProductInfo();
    }
}
