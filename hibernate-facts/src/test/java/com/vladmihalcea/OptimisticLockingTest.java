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

import com.vladmihalcea.hibernate.model.store.Product;
import com.vladmihalcea.service.ItemService;
import com.vladmihalcea.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OptimisticLockingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockingTest.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductService productService;

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        CleanDbUtil.cleanStore(transactionTemplate, entityManager);
    }

    @Test(expected = IllegalTransactionStateException.class)
    public void testRetryFailsOnTransaction() {
        itemService.saveItem();
    }

    @Test
    public void testRetryRunsOnTransaction() {
        assertEquals(0, itemService.getRegisteredCalls());
        try {
            itemService.saveItems();
        } catch (OptimisticLockException expected) {
        }
        assertEquals(3, itemService.getRegisteredCalls());
    }

    @Test
    public void testRetries() throws InterruptedException {
        final Product product = productService.newProduct();
        assertEquals(0, product.getVersion());
        Product savedProduct = productService.updateName(product.getId(), "name");
        assertEquals(1, savedProduct.getVersion());

        final int threadsNumber = 10;

        final AtomicInteger atomicInteger = new AtomicInteger();
        final CountDownLatch startLatch = new CountDownLatch(threadsNumber + 1);
        final CountDownLatch endLatch = new CountDownLatch(threadsNumber + 1);

        for (; atomicInteger.get() < threadsNumber; atomicInteger.incrementAndGet()) {
            final long index = (long) atomicInteger.get() * threadsNumber;
            LOGGER.info("Scheduling thread index {}", index);
            Thread testThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.countDown();
                        startLatch.await();
                        productService.updateName(product.getId(), UUID.randomUUID().toString());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        LOGGER.error("Exception thrown!", e);
                    } finally {
                        endLatch.countDown();
                    }
                }
            });
            testThread.start();
        }
        startLatch.countDown();
        LOGGER.info("Waiting for threads to be done");
        endLatch.countDown();
        endLatch.await();
        LOGGER.info("Threads are done");
    }
}
