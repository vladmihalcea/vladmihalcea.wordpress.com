package vladmihalcea.concurrent.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vladmihalcea.concurrent.exception.OptimisticLockingException;
import vladmihalcea.concurrent.service.ProductService;

/**
 * ProductServiceImpl - ProductService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private volatile int calls = 0;

    @Override
    public void saveProduct() {
        calls++;
        LOGGER.info("Save Product!");
        throw new OptimisticLockingException("Save Product!");
    }

    @Override
    public int getRegisteredCalls() {
        return calls;
    }
}
