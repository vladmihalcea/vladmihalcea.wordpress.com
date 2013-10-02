package vladmihalcea.customerlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CustomerLockSerializedExecution - Serialize execution on locking based on a given number
 *
 * @author Vlad Mihalcea
 */
public class CustomerLockSerializedExecution {

    private Map<Long, ReentrantLock> lockMap = new HashMap<Long, ReentrantLock>();

    private synchronized Lock getLock(Long customerId) {
        ReentrantLock lock = lockMap.get(customerId);
        if (lock == null) {
            lock = new ReentrantLock();
            lockMap.put(customerId, lock);
        }
        return lock;
    }

    /**
     * Lock on the customer lock and execute the specific logic
     *
     * @param customerId customer id
     * @param callable   custom logic
     */
    public <T> void lockExecution(Long customerId, Callable<T> callable) {
        Lock lock = getLock(customerId);
        try {
            lock.lockInterruptibly();
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
