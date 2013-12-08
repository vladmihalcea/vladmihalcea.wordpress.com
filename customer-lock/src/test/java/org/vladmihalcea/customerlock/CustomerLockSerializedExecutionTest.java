package org.vladmihalcea.customerlock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * CustomerLockSerializedExecutionTest - CustomerLockSerializedExecution Test
 *
 * @author Vlad Mihalcea
 */
public class CustomerLockSerializedExecutionTest {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerLockSerializedExecutionTest.class);

    private CustomerLockSerializedExecution<Long> customerLockSerializedExecution = new CustomerLockSerializedExecution<Long>();

    private CopyOnWriteArrayList<Long> buffer = new CopyOnWriteArrayList<Long>();

    public static final int appendsNumber = 3;

    private class TestCallable implements Callable<Void> {

        private final long index;

        private TestCallable(long index) {
            this.index = index;
        }

        @Override
        public Void call() throws Exception {
            LOG.info("Running thread index {}", index);
            for(int i = 0; i < appendsNumber; i++) {
                long number = index + i;
                LOG.info("Adding {}", number);
                buffer.add(number);
            }
            return null;
        }
    }

    @Test
    public void testAwaitExecutionForSameIntegratedSource() throws InterruptedException {
        final int threadsNumber = 10;

        final AtomicInteger atomicInteger = new AtomicInteger();
        final CountDownLatch startLatch = new CountDownLatch(threadsNumber + 1);
        final CountDownLatch endLatch = new CountDownLatch(threadsNumber + 1);

        for (; atomicInteger.get() < threadsNumber; atomicInteger.incrementAndGet()) {
            final long index = (long) atomicInteger.get() * threadsNumber;
            LOG.info("Scheduling thread index {}", index);
            Thread testThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.countDown();
                        startLatch.await();
                        customerLockSerializedExecution.lockExecution(0L, new TestCallable(index));
                        endLatch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            testThread.start();
        }
        startLatch.countDown();
        LOG.info("Waiting for threads to be done");
        endLatch.countDown();
        endLatch.await();
        LOG.info("Threads are done");
        for(int i = 0; i < threadsNumber; i += appendsNumber) {
            long reference = buffer.get(i);
            for(int j = 0; j < appendsNumber; j++) {
                assertEquals(reference + j, (long) buffer.get(i + j));
            }
        }
    }

    @Test
    public void testAwaitExecutionForDifferentIntegratedSource() throws InterruptedException {
        final int threadsNumber = 10;

        final AtomicInteger atomicInteger = new AtomicInteger();
        final CountDownLatch startLatch = new CountDownLatch(threadsNumber + 1);
        final CountDownLatch endLatch = new CountDownLatch(threadsNumber);

        for (; atomicInteger.get() < threadsNumber; atomicInteger.incrementAndGet()) {
            final long index = (long) atomicInteger.get() * threadsNumber;
            LOG.info("Scheduling thread index {}", index);
            Thread testThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.countDown();
                        startLatch.await();
                        customerLockSerializedExecution.lockExecution(index, new TestCallable(index));
                        endLatch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            testThread.start();
        }
        startLatch.countDown();
        LOG.info("Waiting for threads to be done");
        endLatch.await();
        LOG.info("Threads are done");
        for(long i = 0; i < threadsNumber; i++) {
            long index = i * threadsNumber;
            for(long j = 0; j < appendsNumber; j++) {
                assertTrue("Buffer doesn't contain" + (index + j), buffer.contains(index + j));
            }
        }
    }
}
