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

import com.vladmihalcea.hibernate.model.cache.Change;
import com.vladmihalcea.hibernate.model.cache.Commit;
import com.vladmihalcea.hibernate.model.cache.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test-pg.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HibernateCacheTest extends AbstractTest {

    private Repository repositoryReference;

    @Before
    public void init() {
        repositoryReference = doInTransaction((entityManager) -> {
            LOGGER.info("Transactional entities are write-through on persisting");

            Repository repository = new Repository("Hibernate-Master-Class");
            Commit commit = new Commit(repository);
            commit.getChanges().add(
                    new Change("README.txt", "0a1,5...")
            );
            commit.getChanges().add(
                    new Change("web.xml", "17c17...")
            );
            repository.addCommit(commit);
            entityManager.persist(repository);
            return repository;
        });
    }

    @Test
    public void testRepositoryEntityUpdate() {
        LOGGER.info("Transactional entities are write-through on updating");
        doInTransaction((entityManager) -> {
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            repository.setName("High-Performance Hibernate");
            for (Commit commit : repository.getCommits()) {
                for (Change change : commit.getChanges()) {
                    assertNotNull(change.getDiff());
                }
            }
        });
        doInTransaction((entityManager) -> {
            LOGGER.info("Reload entity after updating");
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            assertEquals("High-Performance Hibernate", repository.getName());
        });
    }

    private final CountDownLatch aliceLatch = new CountDownLatch(1);
    private final CountDownLatch bobLatch = new CountDownLatch(1);

    @Test
    public void testRepositoryEntityConcurrentUpdate() throws ExecutionException, InterruptedException {
        LOGGER.info("Transactional entity concurrent update");
        final AtomicReference<Future<?>> bobTransactionOutcomeHolder = new AtomicReference<>();
        doInTransaction((entityManager) -> {
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            repository.setName("High-Performance Hibernate");
            entityManager.flush();
            Future<?> bobTransactionOutcome = executeAsync(() -> {
                doInTransaction((_entityManager) -> {
                    Repository _repository = entityManager.find(Repository.class, repositoryReference.getId());
                    _repository.setName("High-Performance Hibernate Book");
                    aliceLatch.countDown();
                    awaitOnLatch(bobLatch);
                });
            });
            bobTransactionOutcomeHolder.set(bobTransactionOutcome);
            sleep(500);
            awaitOnLatch(aliceLatch);
        });
        doInTransaction((entityManager) -> {
            LOGGER.info("Reload entity after Alice update");
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            LOGGER.info("Repository name is {}", repository.getName());
        });
        bobLatch.countDown();
        bobTransactionOutcomeHolder.get().get();
        doInTransaction((entityManager) -> {
            LOGGER.info("Reload entity after Bob update");
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            LOGGER.info("Repository name is {}", repository.getName());
        });
    }

    @Test
    public void testRepositoryEntityDelete() {
        LOGGER.info("Transactional entities are deletable");
        doInTransaction((entityManager) -> {
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            entityManager.remove(repository);
        });
        doInTransaction((entityManager) -> {
            assertNull(entityManager.find(Repository.class, repositoryReference.getId()));
        });
    }

    @Test
    public void testIsolationLevel() {
        LOGGER.info("Transactional entities are deletable");
        doInTransaction((entityManager) -> {
            Repository repository = entityManager.find(Repository.class, repositoryReference.getId());
            executeSync(() -> {
                doInTransaction(_entityManager -> {
                    Repository _repository = entityManager.find(Repository.class, repositoryReference.getId());
                    _repository.setName("High-Performance Hibernate");
                    LOGGER.info("Updating repository name to {}", _repository.getName());
                });
            });

            repository = entityManager.find(Repository.class, repositoryReference.getId());
            assertEquals("Hibernate-Master-Class", repository.getName());

            LOGGER.info("Detaching repository ");
            entityManager.detach(repository);
            assertFalse(entityManager.contains(repository));

            repository = entityManager.find(Repository.class, repositoryReference.getId());

            assertEquals("High-Performance Hibernate", repository.getName());
        });
    }

}
