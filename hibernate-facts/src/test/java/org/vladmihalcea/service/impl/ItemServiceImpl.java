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
