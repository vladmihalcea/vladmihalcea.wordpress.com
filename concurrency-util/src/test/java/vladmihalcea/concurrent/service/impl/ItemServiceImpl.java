package vladmihalcea.concurrent.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vladmihalcea.concurrent.Retry;
import vladmihalcea.concurrent.exception.OptimisticLockingException;
import vladmihalcea.concurrent.service.ItemService;

/**
 * ItemServiceImpl - ItemService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Override
    @Retry(times = 2, on = OptimisticLockingException.class)
    @Transactional
    public void saveItem() {
        incrementCalls();
        LOGGER.info("Save Item!");
        throw new OptimisticLockingException("Save Item!");
    }

    @Override
    @Retry(times = 2, on = OptimisticLockingException.class, failInTransaction = false)
    @Transactional
    public void saveItems() {
        incrementCalls();
        LOGGER.info("Save Items!");
        throw new OptimisticLockingException("Save Itesm!");
    }
}
