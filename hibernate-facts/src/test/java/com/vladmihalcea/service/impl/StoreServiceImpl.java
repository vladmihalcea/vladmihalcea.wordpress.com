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

package com.vladmihalcea.service.impl;

import com.vladmihalcea.hibernate.model.store.Product;
import com.vladmihalcea.service.StoreService;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * StoreServiceImpl - StoreService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class StoreServiceImpl implements StoreService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void purchase(Long productId) {
        Product product = entityManager.find(Product.class, 1L);
        Session session = (Session) entityManager.getDelegate();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                LOGGER.debug("Transaction isolation level is {}", Environment.isolationLevelToString(connection.getTransactionIsolation()));
            }
        });
        product.setQuantity(product.getQuantity() - 1);
    }
}
