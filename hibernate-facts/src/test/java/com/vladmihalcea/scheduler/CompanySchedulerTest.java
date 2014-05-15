package com.vladmihalcea.scheduler;

import com.vladmihalcea.hibernate.model.store.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * UserRatingManagerSchedulerTest -
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CompanySchedulerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CompanyScheduler companyScheduler;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void test() throws InterruptedException {

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Company tvCompany = new Company();
                tvCompany.setName("TV Company");

                Company radioCompany = new Company();
                radioCompany.setName("Radio Company");

                entityManager.persist(tvCompany);
                entityManager.persist(radioCompany);

                return null;
            }
        });

        Thread.sleep(500);
    }
}
