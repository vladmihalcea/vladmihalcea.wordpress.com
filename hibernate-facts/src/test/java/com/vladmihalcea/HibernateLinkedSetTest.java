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

import com.vladmihalcea.hibernate.model.linkedset.LinkedChild;
import com.vladmihalcea.hibernate.model.linkedset.LinkedParent;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class HibernateLinkedSetTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateLinkedSetTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void test() {

        final Long parentId = cleanAndSaveParent();

        LinkedParent parent = transactionTemplate.execute(new TransactionCallback<LinkedParent>() {
            @Override
            public LinkedParent doInTransaction(TransactionStatus transactionStatus) {
                LinkedParent parent = loadParent(parentId);
                LinkedChild child1 = new LinkedChild();
                child1.setName("child1");
                LinkedChild child2 = new LinkedChild();
                child2.setName("child2");
                LinkedChild child3 = new LinkedChild();
                child3.setName("child3");
                LinkedChild child4 = new LinkedChild();
                child4.setName("child4");
                LinkedChild child5 = new LinkedChild();
                child5.setName("child5");
                parent.addChild(child1);
                parent.addChild(child2);
                parent.addChild(child3);
                parent.addChild(child4);
                parent.addChild(child5);
                entityManager.merge(parent);
                return parent;
            }
        });
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Set<LinkedChild> children = loadParent(parentId).getChildren();
                assertEquals(5, children.size());
                Iterator<LinkedChild> childIterator = children.iterator();
                assertEquals("child1", childIterator.next().getName());
                assertEquals("child2", childIterator.next().getName());
                assertEquals("child3", childIterator.next().getName());
                assertEquals("child4", childIterator.next().getName());
                assertEquals("child5", childIterator.next().getName());
                return null;
            }
        });
    }

    protected Long cleanAndSaveParent() {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from LinkedChild where id > 0").executeUpdate();
                entityManager.createQuery("delete from LinkedParent where id > 0").executeUpdate();
                assertTrue(entityManager.createQuery("from LinkedParent").getResultList().isEmpty());
                LinkedParent parent = new LinkedParent();
                entityManager.persist(parent);
                entityManager.flush();
                return parent.getId();
            }
        });
    }

    protected LinkedParent loadParent(Long parentId) {
        return entityManager.find(LinkedParent.class, parentId);
    }

}
