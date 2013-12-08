package org.vladmihalcea.mongo.dao;

import org.vladmihalcea.mongo.model.Product;
import org.springframework.data.repository.Repository;

/**
 * ProductRepository - Product Repository
 *
 * @author Vlad Mihalcea
 */
public interface ProductRepository extends Repository<Product, Long>, ProductCustomRepository {

    Product save(Product entity);

    Product findOne(Long id);
}
