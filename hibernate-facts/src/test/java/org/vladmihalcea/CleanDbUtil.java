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

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

/**
 * CleanDbUtil - CleanDbUtil
 *
 * @author Vlad Mihalcea
 */
public class CleanDbUtil {

    public static void cleanStore(final TransactionTemplate transactionTemplate, final EntityManager entityManager) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from SubVersion where id > 0").executeUpdate();
                entityManager.createQuery("delete from Version where id > 0").executeUpdate();
                entityManager.createQuery("delete from Image where id > 0").executeUpdate();
                entityManager.createQuery("delete from WarehouseProductInfo where id > 0").executeUpdate();
                entityManager.createQuery("delete from Product where id > 0").executeUpdate();
                entityManager.createQuery("delete from Company where id > 0").executeUpdate();
                entityManager.flush();
                return null;
            }
        });
    }
}
