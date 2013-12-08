package org.vladmihalcea.mongo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.vladmihalcea.mongo.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vladmihalcea.mongo.service.ProductService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * ProductRepositoryIT - ProductRepository IT
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-it.xml")
public class ProductRepositoryIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepositoryIT.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        mongoTemplate.dropCollection(Product.class);
    }

    @Test
    public void testSave() {
        Product product = new Product();
        product.setId(123L);
        product.setName("Tv");
        productRepository.save(product);
        Product savedProduct = productRepository.findOne(123L);
        assertEquals(savedProduct, product);
        assertEquals(savedProduct.hashCode(), product.hashCode());
        assertEquals(Long.valueOf(0), product.getVersion());
        assertEquals("Tv", product.getName());
        savedProduct.setName("Dvd");
        savedProduct = productRepository.save(savedProduct);
        assertEquals(Long.valueOf(1), savedProduct.getVersion());
        savedProduct.setVersion(0L);
        try {
            productRepository.save(savedProduct);
            fail("Expected OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }
    }

    @Test
     public void testFindAndInsert() {
        Long randomId = new Random().nextLong();
        assertNull(productRepository.findOne(randomId));
        Product product = productRepository.findAndInsert(randomId);
        assertEquals(product.getId(), productRepository.findOne(randomId).getId());
    }

    @Test
    public void testRetries() throws InterruptedException {
        Product product = new Product();
        product.setId(123L);
        product.setName("Tv");
        productRepository.save(product);
        Product savedProduct = productRepository.findOne(123L);
        assertEquals(savedProduct, product);

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
                        productService.updateName(123L, UUID.randomUUID().toString());
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
