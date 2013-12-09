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

package org.vladmihalcea.mongo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vladmihalcea.concurrent.Retry;
import org.vladmihalcea.mongo.dao.ProductRepository;
import org.vladmihalcea.mongo.model.Product;
import org.vladmihalcea.mongo.service.ProductService;

/**
 * ProductServiceImpl - Product Service Implementation
 *
 * @author Vlad Mihalcea
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Retry(times = 10, on = org.springframework.dao.OptimisticLockingFailureException.class)
    public Product updateName(Long id, String name) {
        Product product = productRepository.findOne(id);
        product.setName(name);
        LOGGER.info("Updating product {} name to {}", product, name);
        return productRepository.save(product);
    }
}
