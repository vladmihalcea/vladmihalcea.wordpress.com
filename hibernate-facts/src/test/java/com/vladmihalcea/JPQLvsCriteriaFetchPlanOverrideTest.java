package com.vladmihalcea;

import com.vladmihalcea.hibernate.model.fetch.FetchChild;
import com.vladmihalcea.hibernate.model.fetch.FetchParent;
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
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JPQLvsCriteriaFetchPlanOverrideTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testFetchPlan() {
        final Long parentId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                FetchParent parent = new FetchParent();
                FetchChild son = new FetchChild("Bob");
                FetchChild daughter = new FetchChild("Alice");
                parent.addChild(son);
                parent.addChild(daughter);
                entityManager.persist(parent);
                entityManager.flush();
                return parent.getId();
            }
        });

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                LOGGER.info("HQL override default fetch plan");

                List<FetchParent> parents = entityManager.createQuery("select p from FetchParent p where p.id = :id", FetchParent.class)
                        .setParameter("id", parentId)
                        .getResultList();
                assertEquals(1, parents.size());
                return null;
            }
        });

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                LOGGER.info("Criteria doesn't override default fetch plan");

                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<FetchParent> cq = cb.createQuery(FetchParent.class);
                Root<FetchParent> parent = cq.from(FetchParent.class);
                cq.where(cb.equal(parent.get("id"), parentId));

                List<FetchParent> parents = entityManager.createQuery(cq).getResultList();
                assertEquals(1, parents.size());
                return null;
            }
        });

    }

}
