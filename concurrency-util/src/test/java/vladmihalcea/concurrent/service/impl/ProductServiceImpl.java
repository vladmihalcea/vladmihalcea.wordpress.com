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
public class ProductServiceImpl extends BaseServiceImpl implements ProductService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public void saveProduct() {
        incrementCalls();
        LOGGER.info("Save Product!");
        throw new OptimisticLockingException("Save Product!");
    }
}
