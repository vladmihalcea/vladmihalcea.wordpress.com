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
import org.vladmihalcea.hibernate.model.store.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class HibernateSetWithMultiLevelFetchTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateSetWithMultiLevelFetchTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        CleanDbUtil.cleanStore(transactionTemplate, entityManager);
    }

    @Test
    public void test() {

        final Long productId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product = new Product("tvCode");
                product.setName("TV");
                product.setCompany(company);

                WarehouseProductInfo warehouseProductInfo = new WarehouseProductInfo();
                warehouseProductInfo.setQuantity(101);
                product.addWarehouse(warehouseProductInfo);

                Image image1 = new Image();
                image1.setName("image1");
                image1.setIndex(0);

                Version version11 = new Version();
                version11.setType("type11");

                SubVersion subVersion111 = new SubVersion();
                subVersion111.setCode("code111");
                SubVersion subVersion112 = new SubVersion();
                subVersion111.setCode("code112");
                SubVersion subVersion113 = new SubVersion();
                subVersion111.setCode("code113");
                SubVersion subVersion114 = new SubVersion();
                subVersion111.setCode("code114");
                version11.addSubVersion(subVersion111);
                version11.addSubVersion(subVersion112);
                version11.addSubVersion(subVersion113);
                version11.addSubVersion(subVersion114);

                Version version12 = new Version();
                version12.setType("type12");

                SubVersion subVersion121 = new SubVersion();
                subVersion121.setCode("code121");
                SubVersion subVersion122 = new SubVersion();
                subVersion122.setCode("code122");
                SubVersion subVersion123 = new SubVersion();
                subVersion123.setCode("code123");
                SubVersion subVersion124 = new SubVersion();
                subVersion124.setCode("code124");
                version12.addSubVersion(subVersion121);
                version12.addSubVersion(subVersion122);
                version12.addSubVersion(subVersion123);
                version12.addSubVersion(subVersion124);

                image1.addVersion(version11);
                image1.addVersion(version12);

                Image image2 = new Image();
                image2.setName("image2");
                image2.setIndex(1);

                Version version21 = new Version();
                version21.setType("type21");

                SubVersion subVersion211 = new SubVersion();
                subVersion211.setCode("code211");
                SubVersion subVersion212 = new SubVersion();
                subVersion212.setCode("code212");
                SubVersion subVersion213 = new SubVersion();
                subVersion213.setCode("code213");
                SubVersion subVersion214 = new SubVersion();
                subVersion214.setCode("code214");
                version21.addSubVersion(subVersion211);
                version21.addSubVersion(subVersion212);
                version21.addSubVersion(subVersion213);
                version21.addSubVersion(subVersion214);

                Version version22 = new Version();
                version22.setType("type22");

                SubVersion subVersion221 = new SubVersion();
                subVersion221.setCode("code221");
                SubVersion subVersion222 = new SubVersion();
                subVersion222.setCode("code222");
                SubVersion subVersion223 = new SubVersion();
                subVersion223.setCode("code223");
                version22.addSubVersion(subVersion221);
                version22.addSubVersion(subVersion222);
                version22.addSubVersion(subVersion223);

                image2.addVersion(version21);
                image2.addVersion(version22);

                product.addImage(image1);
                product.addImage(image2);

                entityManager.persist(product);
                return product.getId();
            }
        });

        Product product = transactionTemplate.execute(new TransactionCallback<Product>() {
            @Override
            public Product doInTransaction(TransactionStatus transactionStatus) {
                return entityManager.find(Product.class, productId);
            }
        });
        try {
            navigateProduct(product);
            fail("Should have thrown LazyInitializationException!");
        } catch (LazyInitializationException expected) {

        }

        product = transactionTemplate.execute(new TransactionCallback<Product>() {
            @Override
            public Product doInTransaction(TransactionStatus transactionStatus) {
                Product p = entityManager.createQuery(
                        "select p " +
                        "from Product p " +
                        "left join fetch p.images i " +
                        "left join fetch i.versions v " +
                        "left join fetch v.subVersions sv ",
                        Product.class).getSingleResult();
                return p;
            }
        });
        navigateProduct(product);

    }

    protected void navigateProduct(Product product) {
        for(Image image : product.getImages()) {
            for(Version version : image.getVersions()) {
                for(SubVersion subVersion : version.getSubVersions()) {
                    assertNotNull(subVersion);
                }
            }
        }
    }
}
