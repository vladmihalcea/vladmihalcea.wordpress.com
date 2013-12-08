package org.vladmihalcea.service;

import org.vladmihalcea.hibernate.model.store.Product;

/**
 * ProductService - Product Service
 *
 * @author Vlad Mihalcea
 */
public interface ProductService {

    Product newProduct();

    Product updateName(Long id, String name);
}
