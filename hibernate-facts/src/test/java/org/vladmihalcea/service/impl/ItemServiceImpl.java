package org.vladmihalcea.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vladmihalcea.concurrent.Retry;
import org.vladmihalcea.service.ItemService;

import javax.persistence.OptimisticLockException;

/**
 * ItemServiceImpl - ItemService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Override
    @Retry(times = 2, on = OptimisticLockException.class)
    @Transactional
    public void saveItem() {
        incrementCalls();
        LOGGER.info("Save Item!");
        throw new OptimisticLockException("Save Item!");
    }

    @Override
    @Retry(times = 2, on = OptimisticLockException.class, failInTransaction = false)
    @Transactional
    public void saveItems() {
        incrementCalls();
        LOGGER.info("Save Items!");
        throw new OptimisticLockException("Save Items!");
    }
}
