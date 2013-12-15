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

package org.vladmihalcea;

import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.vladmihalcea.hibernate.model.store.Company;
import org.vladmihalcea.hibernate.model.store.Image;
import org.vladmihalcea.hibernate.model.store.Product;
import org.vladmihalcea.hibernate.model.store.WarehouseProductInfo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
public class HibernateEqualsHashCodeTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateEqualsHashCodeTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        CleanDbUtil.cleanStore(transactionTemplate, entityManager);
    }

    @Test
    public void testRootObjects() {

        final Company newCompany = new Company();
        newCompany.setName("TV Company");

        final Long companyId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                entityManager.persist(newCompany);
                return newCompany.getId();
            }
        });
        Company detachedCompany = transactionTemplate.execute(new TransactionCallback<Company>() {
            @Override
            public Company doInTransaction(TransactionStatus transactionStatus) {
                Company attachedCompany = entityManager.find(Company.class, companyId);
                assertEquals(newCompany, attachedCompany);
                assertEquals(newCompany.hashCode(), attachedCompany.hashCode());
                return attachedCompany;
            }
        });

        assertEquals(newCompany, detachedCompany);
        assertEquals(newCompany.hashCode(), detachedCompany.hashCode());

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Company attachedCompany = entityManager.find(Company.class, companyId);
                attachedCompany.setName("New Company");
                entityManager.flush();
                return null;
            }
        });

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Company attachedCompany = entityManager.find(Company.class, companyId);
                assertEquals(newCompany, attachedCompany);
                assertEquals(newCompany.hashCode(), attachedCompany.hashCode());
                return null;
            }
        });
    }

    @Test
    public void testChildObjects() {

        final Long productId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product = new Product("tvCode");
                product.setName("TV");
                product.setCompany(company);

                Image frontImage = new Image();
                frontImage.setName("front image");
                frontImage.setIndex(0);

                Image sideImage = new Image();
                sideImage.setName("side image");
                sideImage.setIndex(1);

                product.addImage(frontImage);
                product.addImage(sideImage);

                WarehouseProductInfo warehouseProductInfo = new WarehouseProductInfo();
                warehouseProductInfo.setQuantity(101);
                product.addWarehouse(warehouseProductInfo);

                entityManager.persist(product);
                return product.getId();
            }
        });

        Product product = transactionTemplate.execute(new TransactionCallback<Product>() {
            @Override
            public Product doInTransaction(TransactionStatus transactionStatus) {
                return entityManager.createQuery(
                        "select p " +
                        "from Product p " +
                        "left join fetch p.images i " +
                        "where p.id = :productId", Product.class)
                        .setParameter("productId", productId)
                        .getSingleResult();
            }
        });
        Image frontImage = new Image();
        frontImage.setName("front image");
        frontImage.setProduct(product);
        frontImage.setIndex(0);
        assertTrue(product.getImages().contains(frontImage));

        List<Image> images = transactionTemplate.execute(new TransactionCallback<List<Image>>() {
            @Override
            public List<Image> doInTransaction(TransactionStatus transactionStatus) {
                return entityManager.createQuery(
                        "select i from Image i ", Image.class)
                        .getResultList();
            }
        });
        try {
            assertTrue(new HashSet<Image>(images).contains(frontImage));
            fail("Should have thrown LazyInitializationException!");
        } catch (LazyInitializationException expected) {

        }

    }
}
