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
