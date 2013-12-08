package org.vladmihalcea.mongo.service;

import org.vladmihalcea.mongo.model.Product;

/**
 * ProductService - Product Service
 *
 * @author Vlad Mihalcea
 */
public interface ProductService {

    Product updateName(Long id, String name);
}
