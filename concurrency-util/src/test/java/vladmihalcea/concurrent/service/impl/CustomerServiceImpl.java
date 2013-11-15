package vladmihalcea.concurrent.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vladmihalcea.concurrent.Retry;
import vladmihalcea.concurrent.exception.OptimisticLockingException;
import vladmihalcea.concurrent.service.CustomerService;

/**
 * CustomerServiceImpl - CustomerService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private volatile int calls = 0;

    @Override
    @Retry(times = 2, on = OptimisticLockingException.class)
    public void saveCustomer() {
        calls++;
        LOGGER.info("Save Customer!");
        throw new OptimisticLockingException("Save Customer!");
    }

    @Override
    public int getRegisteredCalls() {
        return calls;
    }
}
