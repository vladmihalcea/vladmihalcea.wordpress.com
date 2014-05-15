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

package com.vladmihalcea;

import com.vladmihalcea.hibernate.model.store.Company;
import com.vladmihalcea.hibernate.model.store.Image;
import com.vladmihalcea.hibernate.model.store.Product;
import com.vladmihalcea.hibernate.model.store.WarehouseProductInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HibernateOperationsOrderTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateOperationsOrderTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        CleanDbUtil.cleanStore(transactionTemplate, entityManager);
    }

    @Test
    public void test() {

        final Long productId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product = new Product("tvCode");
                product.setName("TV");
                product.setCompany(company);

                Image frontImage = new Image();
                frontImage.setName("front image");
                frontImage.setIndex(0);

                Image sideImage = new Image();
                sideImage.setName("side image");
                sideImage.setIndex(1);

                product.addImage(frontImage);
                product.addImage(sideImage);

                WarehouseProductInfo warehouseProductInfo = new WarehouseProductInfo();
                warehouseProductInfo.setQuantity(101);
                product.addWarehouse(warehouseProductInfo);

                entityManager.persist(product);
                return product.getId();
            }
        });
        try {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus transactionStatus) {


                    Product product = entityManager.find(Product.class, productId);
                    assertEquals(2, product.getImages().size());
                    Iterator<Image> imageIterator = product.getImages().iterator();

                    Image frontImage = imageIterator.next();
                    assertEquals("front image", frontImage.getName());
                    assertEquals(0, frontImage.getIndex());
                    Image sideImage = imageIterator.next();
                    assertEquals("side image", sideImage.getName());
                    assertEquals(1, sideImage.getIndex());

                    Image backImage = new Image();
                    sideImage.setName("back image");
                    sideImage.setIndex(1);

                    product.removeImage(sideImage);
                    product.addImage(backImage);
                    product.setName("tv set");

                    entityManager.flush();
                    return null;
                }
            });
            fail("Expected ConstraintViolationException");
        } catch (PersistenceException expected) {
            assertEquals(ConstraintViolationException.class, expected.getCause().getClass());
        }

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Product product = entityManager.find(Product.class, productId);
                assertEquals(2, product.getImages().size());
                Iterator<Image> imageIterator = product.getImages().iterator();

                Image frontImage = imageIterator.next();
                assertEquals("front image", frontImage.getName());
                Image sideImage = imageIterator.next();
                assertEquals("side image", sideImage.getName());

                Image backImage = new Image();
                backImage.setName("back image");
                backImage.setIndex(1);

                //http://docs.jboss.org/hibernate/orm/4.2/javadocs/org/hibernate/event/internal/AbstractFlushingEventListener.html#performExecutions%28org.hibernate.event.spi.EventSource%29
                product.removeImage(sideImage);
                entityManager.flush();

                product.addImage(backImage);
                product.setName("tv set");

                entityManager.flush();
                return null;
            }
        });
    }
}
