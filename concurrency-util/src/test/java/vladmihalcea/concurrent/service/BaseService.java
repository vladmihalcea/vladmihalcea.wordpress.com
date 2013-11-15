package vladmihalcea.concurrent.service;

import vladmihalcea.concurrent.Retry;
import vladmihalcea.concurrent.exception.OptimisticLockingException;

/**
 * BaseService - Base Service
 *
 * @author Vlad Mihalcea
 */
public interface BaseService {

    int getRegisteredCalls();
}
