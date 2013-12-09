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
