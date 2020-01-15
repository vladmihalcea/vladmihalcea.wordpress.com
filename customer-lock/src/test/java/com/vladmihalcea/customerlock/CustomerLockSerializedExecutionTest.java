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

package com.vladmihalcea.customerlock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CustomerLockSerializedExecutionTest - CustomerLockSerializedExecution Test
 *
 * @author Vlad Mihalcea
 */
public class CustomerLockSerializedExecutionTest {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerLockSerializedExecutionTest.class);

    private CustomerLockedExecution<Long> execution = new CustomerLockedExecution<>();

    private CopyOnWriteArrayList<Long> buffer = new CopyOnWriteArrayList<>();

    private static final int appendTries = 3;

    private final int threadCount = 10;

    private ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    @Test
    public void testAwaitExecutionForSameIntegratedSource() throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(threadCount + 1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount + 1);

        for (long i = 0; i < threadCount; i++) {
            final long index = i * threadCount;

            LOG.info("Scheduling thread index {}", index);

            executorService.submit(
                () -> {
                    try {
                        startLatch.countDown();
                        startLatch.await();
                        execution.lockExecution(
                            0L,
                            () -> {
                                LOG.info("Running thread index {}", index);
                                for (int j = 0; j < appendTries; j++) {
                                    long number = index + j;
                                    LOG.info("Adding {}", number);
                                    buffer.add(number);
                                }

                                return null;
                            }
                        );
                        endLatch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            );
        }

        startLatch.countDown();

        LOG.info("Waiting for threads to be done");

        endLatch.countDown();
        endLatch.await();

        LOG.info("Threads are done processing");

        for (int i = 0; i < threadCount; i += appendTries) {
            long reference = buffer.get(i);
            for (int j = 0; j < appendTries; j++) {
                assertEquals(reference + j, (long) buffer.get(i + j));
            }
        }
    }

    @Test
    public void testAwaitExecutionForDifferentIntegratedSource() throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(threadCount + 1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount + 1);

        for (long i = 1; i <= threadCount; i++) {
            final long index = i * threadCount;

            LOG.info("Scheduling thread index {}", index);

            executorService.submit(
                () -> {
                    try {
                        startLatch.countDown();
                        startLatch.await();
                        execution.lockExecution(
                            index,
                            () -> {
                                LOG.info("Running thread index {}", index);
                                for (int j = 0; j < appendTries; j++) {
                                    long number = index + j;
                                    LOG.info("Adding {}", number);
                                    buffer.add(number);
                                }

                                return null;
                            }
                        );
                        endLatch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            );
        }

        startLatch.countDown();

        LOG.info("Waiting for threads to be done");

        endLatch.countDown();
        endLatch.await();

        LOG.info("Threads are done processing");

        for (long i = 1; i <= threadCount; i++) {
            long index = i * threadCount;
            for (long j = 0; j < appendTries; j++) {
                assertTrue("Buffer doesn't contain" + (index + j), buffer.contains(index + j));
            }
        }
    }
}
