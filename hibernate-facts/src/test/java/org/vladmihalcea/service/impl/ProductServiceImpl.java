package org.vladmihalcea.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.vladmihalcea.concurrent.Retry;
import org.vladmihalcea.hibernate.model.store.Company;
import org.vladmihalcea.hibernate.model.store.Product;
import org.vladmihalcea.hibernate.model.store.WarehouseProductInfo;
import org.vladmihalcea.service.ProductService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

/**
 * ProductServiceImpl - ProductService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Override
    public Product newProduct() {
        return transactionTemplate.execute(new TransactionCallback<Product>() {
            @Override
            public Product doInTransaction(TransactionStatus status) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product = new Product();
                product.setName("TV");
                product.setCompany(company);

                WarehouseProductInfo warehouseProductInfo = new WarehouseProductInfo();
                warehouseProductInfo.setQuantity(101);
                product.addWarehouse(warehouseProductInfo);

                entityManager.persist(product);
                entityManager.flush();
                return product;
            }
        });
    }

    @Override
    @Retry(times = 10, on = OptimisticLockException.class)
    public Product updateName(final Long id, final String name) {
        return transactionTemplate.execute(new TransactionCallback<Product>() {
            @Override
            public Product doInTransaction(TransactionStatus status) {
                Product product = entityManager.find(Product.class, id);
                product.setName(name);
                LOGGER.info("Updating product {} name to {}", product, name);
                product = entityManager.merge(product);
                entityManager.flush();
                return product;
            }
        });
    }
}
