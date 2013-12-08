package org.vladmihalcea.customerlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CustomerLockSerializedExecution - Lock execution based for a given customer
 *
 * @author Vlad Mihalcea
 */
public class CustomerLockSerializedExecution<K> {

    private Map<K, ReentrantLock> lockMap = new HashMap<K, ReentrantLock>();

    private synchronized Lock getLock(K customerId) {
        ReentrantLock lock = lockMap.get(customerId);
        if (lock == null) {
            lock = new ReentrantLock();
            lockMap.put(customerId, lock);
        }
        return lock;
    }

    /**
     * Lock on the customer and execute the specific logic
     *
     * @param customerId customer id
     * @param callable   custom logic callback
     */
    public <T> void lockExecution(K customerId, Callable<T> callable) {
        Lock lock = getLock(customerId);
        try {
            lock.lockInterruptibly();
            callable.call();
        } catch (Exception e) {
            throw new CallableException(e, callable);
        } finally {
            lock.unlock();
        }
    }
}
