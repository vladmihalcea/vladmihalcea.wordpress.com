package vladmihalcea.mongo.service;

import vladmihalcea.concurrent.Retry;
import vladmihalcea.mongo.model.Product;

/**
 * ProductService - Product Service
 *
 * @author Vlad Mihalcea
 */
public interface ProductService {

    Product updateName(Long id, String name);
}
