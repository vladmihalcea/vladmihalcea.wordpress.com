package vladmihalcea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import vladmihalcea.hibernate.model.store.Product;
import vladmihalcea.service.ItemService;
import vladmihalcea.service.ProductService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class OptimisticLockingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockingTest.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductService productService;

    @PersistenceContext(unitName = "testPersistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Before
    public void beforeTest() {
        clean();
    }

    /*@Test(expected = IllegalTransactionStateException.class)
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
    }*/

    @Test
    public void testRetries() throws InterruptedException {
        final Product product = productService.newProduct();

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

    protected void clean() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from SubVersion where id > 0").executeUpdate();
                entityManager.createQuery("delete from Version where id > 0").executeUpdate();
                entityManager.createQuery("delete from Image where id > 0").executeUpdate();
                entityManager.createQuery("delete from WarehouseProductInfo where id > 0").executeUpdate();
                entityManager.createQuery("delete from Product where id > 0").executeUpdate();
                entityManager.createQuery("delete from Company where id > 0").executeUpdate();
                return null;
            }
        });
    }

}
