package vladmihalcea;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import vladmihalcea.hibernate.model.eagerset.SetChild;
import vladmihalcea.hibernate.model.eagerset.SetParent;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateEagetSetTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateEagetSetTest.class);

    @PersistenceContext(unitName = "testPersistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Test
    public void test() {

        final Long parentId = cleanAndSaveParent();

        SetParent parent = transactionTemplate.execute(new TransactionCallback<SetParent>() {
            @Override
            public SetParent doInTransaction(TransactionStatus transactionStatus) {
                SetParent parent = loadParent(parentId);
                SetChild child1 = new SetChild();
                child1.setName("child1");
                SetChild child2 = new SetChild();
                child2.setName("child2");
                parent.addChild(child1);
                parent.addChild(child2);
                entityManager.merge(parent);
                return parent;
            }
        });
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                SetParent parent = loadParent(parentId);
                assertEquals(2, loadParent(parentId).getChildren().size());
                assertTrue(parent.getChildren().contains(parent.getChildren().iterator().next()));
                return null;
            }
        });
    }

    @Test
    public void testFixByPersistingChild() {
        final Long parentId = cleanAndSaveParent();

        SetParent parent = transactionTemplate.execute(new TransactionCallback<SetParent>() {
            @Override
            public SetParent doInTransaction(TransactionStatus transactionStatus) {
                SetParent parent = loadParent(parentId);
                SetChild child1 = new SetChild();
                child1.setName("child1");
                SetChild child2 = new SetChild();
                child2.setName("child2");
                entityManager.persist(child1);
                entityManager.persist(child2);
                parent.addChild(child1);
                parent.addChild(child2);
                entityManager.merge(parent);
                parent.getChildren().size();
                return parent;
            }
        });
        assertEquals(2, parent.getChildren().size());
    }

    protected Long cleanAndSaveParent() {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from SetChild where id > 0").executeUpdate();
                entityManager.createQuery("delete from SetParent where id > 0").executeUpdate();
                assertTrue(entityManager.createQuery("from SetParent").getResultList().isEmpty());
                SetParent parent = new SetParent();
                entityManager.persist(parent);
                return parent.getId();
            }
        });
    }

    protected SetParent loadParent(Long parentId) {
        //return entityManager.createQuery("from SetParent where id =:parentId", SetParent.class).setParameter("parentId", parentId).getSingleResult();
        return entityManager.find(SetParent.class, parentId);
    }

}
