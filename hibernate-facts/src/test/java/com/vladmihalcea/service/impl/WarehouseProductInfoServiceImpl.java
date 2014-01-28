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

package com.vladmihalcea.service.impl;

import com.vladmihalcea.hibernate.model.store.Company;
import com.vladmihalcea.hibernate.model.store.Product;
import com.vladmihalcea.hibernate.model.store.WarehouseProductInfo;
import com.vladmihalcea.service.WarehouseProductInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * WarehouseProductInfoServiceImpl - WarehouseProductInfoService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class WarehouseProductInfoServiceImpl implements WarehouseProductInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseProductInfoServiceImpl.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Override
    @Transactional
    public WarehouseProductInfo newWarehouseProductInfo() {

        LOGGER.info("newWarehouseProductInfo");

        Company company = entityManager.createQuery("from Company", Company.class).getResultList().get(0);

        Product product3 = new Product("phoneCode");
        product3.setName("Phone");
        product3.setCompany(company);

        WarehouseProductInfo warehouseProductInfo3 = new WarehouseProductInfo();
        warehouseProductInfo3.setQuantity(19);
        product3.addWarehouse(warehouseProductInfo3);

        entityManager.persist(product3);
        return warehouseProductInfo3;
    }

    @Override
    @Transactional
    public List<WarehouseProductInfo> findAllWithNPlusOne() {
        List<WarehouseProductInfo> warehouseProductInfos = entityManager.createQuery(
                "from WarehouseProductInfo", WarehouseProductInfo.class).getResultList();
        navigateWarehouseProductInfos(warehouseProductInfos);
        return warehouseProductInfos;
    }

    @Override
    @Transactional
    public List<WarehouseProductInfo> findAllWithFetch() {
        List<WarehouseProductInfo> warehouseProductInfos = entityManager.createQuery(
                "from WarehouseProductInfo wpi " +
                        "join fetch wpi.product p " +
                        "join fetch p.company", WarehouseProductInfo.class).getResultList();
        navigateWarehouseProductInfos(warehouseProductInfos);
        return warehouseProductInfos;
    }

    private void navigateWarehouseProductInfos(List<WarehouseProductInfo> warehouseProductInfos) {
        for (WarehouseProductInfo warehouseProductInfo : warehouseProductInfos) {
            warehouseProductInfo.getProduct();
        }
    }
}
