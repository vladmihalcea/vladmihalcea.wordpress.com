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

package com.vladmihalcea;

import com.vladmihalcea.hibernate.model.component.Post;
import com.vladmihalcea.hibernate.model.eagerset.SetChild;
import com.vladmihalcea.hibernate.model.eagerset.SetParent;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PessimisticLockScope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HibernateCascadeLockComponentTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateCascadeLockComponentTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void test() {

        final Long parentId = cleanAndSaveParent();

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Post post = entityManager.find(Post.class, parentId);
                entityManager.lock(post, LockModeType.PESSIMISTIC_WRITE, Collections.singletonMap("javax.persistence.lock.scope", PessimisticLockScope.EXTENDED));
                return null;
            }
        });
    }

    protected Long cleanAndSaveParent() {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from Post where id > 0").executeUpdate();
                assertTrue(entityManager.createQuery("from Post").getResultList().isEmpty());
                Post post = new Post();
                entityManager.persist(post);
                entityManager.flush();
                return post.getId();
            }
        });
    }

}
