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
import org.hibernate.loader.MultipleBagFetchException;
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
import org.vladmihalcea.hibernate.model.baglist.BagBranch;
import org.vladmihalcea.hibernate.model.baglist.BagForest;
import org.vladmihalcea.hibernate.model.baglist.BagLeaf;
import org.vladmihalcea.hibernate.model.baglist.BagTree;
import org.vladmihalcea.hibernate.model.util.ClassId;
import org.vladmihalcea.hibernate.model.util.EntityGraphBuilder;
import org.vladmihalcea.hibernate.model.util.EntityVisitor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateBagMultiLevelFetchTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateBagMultiLevelFetchTest.class);

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

                BagForest forest = new BagForest();

                BagTree tree1 = new BagTree();
                tree1.setIndex(0);

                BagBranch branch11 = new BagBranch();
                branch11.setIndex(0);

                BagLeaf leaf111 = new BagLeaf();
                leaf111.setIndex(0);
                BagLeaf leaf112 = new BagLeaf();
                leaf111.setIndex(1);
                BagLeaf leaf113 = new BagLeaf();
                leaf111.setIndex(2);
                BagLeaf leaf114 = new BagLeaf();
                leaf111.setIndex(3);
                branch11.addLeaf(leaf111);
                branch11.addLeaf(leaf112);
                branch11.addLeaf(leaf113);
                branch11.addLeaf(leaf114);

                BagBranch branch12 = new BagBranch();
                branch12.setIndex(1);

                BagLeaf leaf121 = new BagLeaf();
                leaf121.setIndex(1);
                BagLeaf leaf122 = new BagLeaf();
                leaf122.setIndex(2);
                BagLeaf leaf123 = new BagLeaf();
                leaf123.setIndex(3);
                BagLeaf leaf124 = new BagLeaf();
                leaf124.setIndex(4);
                branch12.addLeaf(leaf121);
                branch12.addLeaf(leaf122);
                branch12.addLeaf(leaf123);
                branch12.addLeaf(leaf124);

                tree1.addBranch(branch11);
                tree1.addBranch(branch12);

                BagTree tree2 = new BagTree();
                tree2.setIndex(1);

                BagBranch branch21 = new BagBranch();
                branch21.setIndex(0);

                BagLeaf leaf211 = new BagLeaf();
                leaf211.setIndex(0);
                BagLeaf leaf212 = new BagLeaf();
                leaf111.setIndex(1);
                BagLeaf leaf213 = new BagLeaf();
                leaf111.setIndex(2);
                BagLeaf leaf214 = new BagLeaf();
                leaf111.setIndex(3);
                branch21.addLeaf(leaf211);
                branch21.addLeaf(leaf212);
                branch21.addLeaf(leaf213);
                branch21.addLeaf(leaf214);

                BagBranch branch22 = new BagBranch();
                branch22.setIndex(2);

                BagLeaf leaf221 = new BagLeaf();
                leaf121.setIndex(0);
                BagLeaf leaf222 = new BagLeaf();
                leaf121.setIndex(1);
                BagLeaf leaf223 = new BagLeaf();
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

        BagForest forest = transactionTemplate.execute(new TransactionCallback<BagForest>() {
            @Override
            public BagForest doInTransaction(TransactionStatus transactionStatus) {
                return entityManager.find(BagForest.class, forestId);
            }
        });
        try {
            navigateForest(forest);
            fail("Should have thrown LazyInitializationException!");
        } catch (LazyInitializationException expected) {

        }

        forest = transactionTemplate.execute(new TransactionCallback<BagForest>() {
            @Override
            public BagForest doInTransaction(TransactionStatus transactionStatus) {
                BagForest forest = entityManager.find(BagForest.class, forestId);
                navigateForest(forest);
                return forest;
            }
        });

        try {
            forest = transactionTemplate.execute(new TransactionCallback<BagForest>() {
                @Override
                public BagForest doInTransaction(TransactionStatus transactionStatus) {
                    BagForest forest = entityManager.createQuery(
                            "select f " +
                                    "from BagForest f " +
                                    "join fetch f.trees t " +
                                    "join fetch t.branches b " +
                                    "join fetch b.leaves l ",
                            BagForest.class).getSingleResult();
                    return forest;
                }
            });
            fail("Should have thrown MultipleBagFetchException!");
        } catch (PersistenceException expected) {
            assertEquals(MultipleBagFetchException.class, expected.getCause().getClass());
        }

        List<BagLeaf> leaves = transactionTemplate.execute(new TransactionCallback<List<BagLeaf>>() {
            @Override
            public List<BagLeaf> doInTransaction(TransactionStatus transactionStatus) {
                List<BagLeaf> leaves = entityManager.createQuery(
                        "select l " +
                                "from BagLeaf l " +
                                "inner join fetch l.branch b " +
                                "inner join fetch b.tree t " +
                                "inner join fetch t.forest f " +
                                "where f.id = :forestId",
                        BagLeaf.class)
                        .setParameter("forestId", forestId)
                        .getResultList();
                return leaves;
            }
        });

        forest = reconstructForest(leaves, forestId);
        navigateForest(forest);

        final BagBranch firstBranch = forest.getTrees().get(0).getBranches().get(0);
        firstBranch.getLeaves().clear();

        final BagForest toMergeForest = forest;

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                BagForest savedForest = entityManager.merge(toMergeForest);
                if(!firstBranch.getLeaves().equals(savedForest.getTrees().get(0).getBranches().get(0).getLeaves())) {
                    LOG.error("Unsafe reusing the bag, changes haven't propagated!");
                }
                entityManager.flush();
                return null;
            }
        });

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                BagForest savedForest = entityManager.find(BagForest.class, forestId);
                if(!firstBranch.getLeaves().equals(savedForest.getTrees().get(0).getBranches().get(0).getLeaves())) {
                    LOG.error("Unsafe reusing the bag, changes haven't propagated!");
                }
                return null;
            }
        });
    }

    protected void navigateForest(BagForest forest) {
        for (BagTree tree : forest.getTrees()) {
            for (BagBranch branch : tree.getBranches()) {
                for (BagLeaf leaf : branch.getLeaves()) {
                    assertNotNull(leaf);
                }
            }
        }
    }

    protected BagForest reconstructForest(List<BagLeaf> leaves, Long forestId) {
        EntityGraphBuilder entityGraphBuilder = new EntityGraphBuilder(new EntityVisitor[] {
                BagLeaf.ENTITY_VISITOR, BagBranch.ENTITY_VISITOR, BagTree.ENTITY_VISITOR, BagForest.ENTITY_VISITOR
        }).build(leaves);
        ClassId<BagForest> forestClassId = new ClassId<BagForest>(BagForest.class, forestId);
        return entityGraphBuilder.getEntityContext().getObject(forestClassId);
    }

    protected void clean() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from BagLeaf where id > 0").executeUpdate();
                entityManager.createQuery("delete from BagBranch where id > 0").executeUpdate();
                entityManager.createQuery("delete from BagTree where id > 0").executeUpdate();
                entityManager.createQuery("delete from BagForest where id > 0").executeUpdate();
                entityManager.flush();
                return null;
            }
        });
    }
}
