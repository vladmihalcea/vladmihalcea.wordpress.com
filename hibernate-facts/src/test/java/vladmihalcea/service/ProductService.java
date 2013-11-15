package vladmihalcea.service;

import vladmihalcea.hibernate.model.store.Product;

/**
 * ProductService - Product Service
 *
 * @author Vlad Mihalcea
 */
public interface ProductService {

    Product newProduct();

    Product updateName(Long id, String name);
}
