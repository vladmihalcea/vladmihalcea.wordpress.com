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
import org.vladmihalcea.hibernate.model.indexlist.Branch;
import org.vladmihalcea.hibernate.model.indexlist.Forest;
import org.vladmihalcea.hibernate.model.indexlist.Leaf;
import org.vladmihalcea.hibernate.model.indexlist.Tree;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateListMultiLevelFetchTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateListMultiLevelFetchTest.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        clean();
    }

    @Test
    public void test() {

        final Long forestId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                Forest forest = new Forest();

                Tree tree1 = new Tree();
                tree1.setIndex(0);

                Branch branch11 = new Branch();
                branch11.setIndex(0);

                Leaf leaf111 = new Leaf();
                leaf111.setIndex(0);
                Leaf leaf112 = new Leaf();
                leaf111.setIndex(1);
                Leaf leaf113 = new Leaf();
                leaf111.setIndex(2);
                Leaf leaf114 = new Leaf();
                leaf111.setIndex(3);
                branch11.addLeaf(leaf111);
                branch11.addLeaf(leaf112);
                branch11.addLeaf(leaf113);
                branch11.addLeaf(leaf114);

                Branch branch12 = new Branch();
                branch12.setIndex(1);

                Leaf leaf121 = new Leaf();
                leaf121.setIndex(1);
                Leaf leaf122 = new Leaf();
                leaf122.setIndex(2);
                Leaf leaf123 = new Leaf();
                leaf123.setIndex(3);
                Leaf leaf124 = new Leaf();
                leaf124.setIndex(4);
                branch12.addLeaf(leaf121);
                branch12.addLeaf(leaf122);
                branch12.addLeaf(leaf123);
                branch12.addLeaf(leaf124);

                tree1.addBranch(branch11);
                tree1.addBranch(branch12);

                Tree tree2 = new Tree();
                tree2.setIndex(1);

                Branch branch21 = new Branch();
                branch21.setIndex(0);

                Leaf leaf211 = new Leaf();
                leaf211.setIndex(0);
                Leaf leaf212 = new Leaf();
                leaf111.setIndex(1);
                Leaf leaf213 = new Leaf();
                leaf111.setIndex(2);
                Leaf leaf214 = new Leaf();
                leaf111.setIndex(3);
                branch21.addLeaf(leaf211);
                branch21.addLeaf(leaf212);
                branch21.addLeaf(leaf213);
                branch21.addLeaf(leaf214);

                Branch branch22 = new Branch();
                branch22.setIndex(2);

                Leaf leaf221 = new Leaf();
                leaf121.setIndex(0);
                Leaf leaf222 = new Leaf();
                leaf121.setIndex(1);
                Leaf leaf223 = new Leaf();
                leaf121.setIndex(2);
                branch22.addLeaf(leaf221);
                branch22.addLeaf(leaf222);
                branch22.addLeaf(leaf223);

                tree2.addBranch(branch21);
                tree2.addBranch(branch22);

                forest.addTree(tree1);
                forest.addTree(tree2);

                entityManager.persist(forest);
                entityManager.flush();
                return forest.getId();
            }
        });

        Forest forest = transactionTemplate.execute(new TransactionCallback<Forest>() {
            @Override
            public Forest doInTransaction(TransactionStatus transactionStatus) {
                return entityManager.find(Forest.class, forestId);
            }
        });
        try {
            navigateForest(forest);
            fail("Should have thrown LazyInitializationException!");
        } catch (LazyInitializationException expected) {

        }

        forest = transactionTemplate.execute(new TransactionCallback<Forest>() {
            @Override
            public Forest doInTransaction(TransactionStatus transactionStatus) {
                Forest f = entityManager.createQuery(
                        "select f " +
                        "from Forest f " +
                        "join fetch f.trees t " +
                        "join fetch t.branches b " +
                        "join fetch b.leaves l ",
                        Forest.class).getSingleResult();
                return f;
            }
        });
        navigateForest(forest);

    }

    protected void navigateForest(Forest forest) {
        for(Tree tree : forest.getTrees()) {
            for(Branch branch : tree.getBranches()) {
                for(Leaf leaf : branch.getLeaves()) {
                    assertNotNull(leaf);
                }
            }
        }
    }

    protected void clean() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from Leaf where id > 0").executeUpdate();
                entityManager.createQuery("delete from Branch where id > 0").executeUpdate();
                entityManager.createQuery("delete from Tree where id > 0").executeUpdate();
                entityManager.createQuery("delete from Forest where id > 0").executeUpdate();
                return null;
            }
        });
    }
}
