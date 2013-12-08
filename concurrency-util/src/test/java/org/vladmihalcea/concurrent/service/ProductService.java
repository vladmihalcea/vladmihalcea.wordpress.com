package org.vladmihalcea.concurrent.service;

import org.vladmihalcea.concurrent.Retry;
import org.vladmihalcea.concurrent.exception.OptimisticLockingException;

/**
 * ProductService - Product Service
 *
 * @author Vlad Mihalcea
 */
public interface ProductService extends BaseService {

    @Retry(times = 2, on = OptimisticLockingException.class)
    void saveProduct();
}
