package com.vladmihalcea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractTest {

    static {
        Thread.currentThread().setName("Alice");
    }

    protected enum LockType {
        LOCKS,
        MVLOCKS,
        MVCC
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread bob = new Thread(r);
        bob.setName("Bob");
        return bob;
    });

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @FunctionalInterface
    protected interface VoidCallable extends Callable<Void> {

        void execute();

        default Void call() throws Exception {
            execute();
            return null;
        }
    }

    @FunctionalInterface
    protected interface EntityManagerCallable<T> {
        T execute(EntityManager entityManager);
    }

    @FunctionalInterface
    protected interface EntityManagerVoidCallable {
        void execute(EntityManager entityManager);
    }

    @FunctionalInterface
    protected interface TransactionCallable<T> extends EntityManagerCallable<T> {
        default void beforeTransactionCompletion() {

        }

        default void afterTransactionCompletion() {

        }
    }

    @FunctionalInterface
    protected interface TransactionVoidCallable extends EntityManagerVoidCallable {
    }

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    protected <T> T doInTransaction(TransactionCallable<T> callable) {
        return transactionTemplate.execute(status -> callable.execute(entityManager));
    }

    protected void doInTransaction(TransactionVoidCallable callable) {
        transactionTemplate.execute(status -> {
            callable.execute(entityManager);
            return null;
        });
    }

    protected void executeSync(VoidCallable callable) {
        executeSync(Collections.singleton(callable));
    }

    protected void executeSync(Collection<VoidCallable> callables) {
        try {
            List<Future<Void>> futures = executorService.invokeAll(callables);
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> void executeAsync(Runnable callable, final Runnable completionCallback) {
        final Future future = executorService.submit(callable);
        new Thread(() -> {
            while (!future.isDone()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            try {
                completionCallback.run();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).start();
    }

    protected Future<?> executeAsync(Runnable callable) {
        return executorService.submit(callable);
    }

    protected <V> Future<V> executeAsync(Callable<V> callable) {
        return executorService.submit(callable);
    }

    protected LockType lockType() {
        return LockType.LOCKS;
    }

    protected void awaitOnLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void sleep(int millis) {
        sleep(millis, null);
    }

    protected <V> V sleep(int millis, Callable<V> callable) {
        V result = null;
        try {
            LOGGER.info("Wait {} ms!", millis);
            if (callable != null) {
                result = callable.call();
            }
            Thread.sleep(millis);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}
