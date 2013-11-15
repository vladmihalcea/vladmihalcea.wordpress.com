package vladmihalcea;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import vladmihalcea.hibernate.model.bag.Child;
import vladmihalcea.hibernate.model.bag.Parent;
import vladmihalcea.service.ItemService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class OptimisticLockingTest {

    private static final Logger LOG = LoggerFactory.getLogger(OptimisticLockingTest.class);

    @Autowired
    private ItemService itemService;

    @Test(expected = IllegalTransactionStateException.class)
    public void testRetryFailsOnTransaction() {
        itemService.saveItem();
    }

    @Test
    public void testRetryRunsOnTransaction() {
        assertEquals(0, itemService.getRegisteredCalls());
        try {
            itemService.saveItems();
        } catch (OptimisticLockException expected) {
        }
        assertEquals(3, itemService.getRegisteredCalls());
    }

}
