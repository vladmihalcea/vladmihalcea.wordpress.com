package vladmihalcea.concurrent.aop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import vladmihalcea.concurrent.exception.OptimisticLockingException;
import vladmihalcea.concurrent.service.CustomerService;
import vladmihalcea.concurrent.service.ItemService;
import vladmihalcea.concurrent.service.ProductService;

import static junit.framework.Assert.assertEquals;

/**
 * OptimisticConcurrencyControlAspectTest - OptimisticConcurrencyControlAspect Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OptimisticConcurrencyControlAspectTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ItemService itemService;

    @Test
    public void testRetryOnInterface() {
        assertEquals(0, productService.getRegisteredCalls());
        try {
            productService.saveProduct();
        } catch (OptimisticLockingException expected) {
        }
        //assertEquals(3, productService.getRegisteredCalls());
        //http://stackoverflow.com/questions/2847640/spring-aop-pointcut-that-matches-annotation-on-interface
        assertEquals(1, productService.getRegisteredCalls());
    }

    @Test
    public void testRetryOnImplementation() {
        assertEquals(0, customerService.getRegisteredCalls());
        try {
            customerService.saveCustomer();
        } catch (OptimisticLockingException expected) {
        }
        assertEquals(3, customerService.getRegisteredCalls());
    }

    @Test(expected = IllegalTransactionStateException.class)
    public void testRetryFailsOnTransaction() {
        try {
            TransactionSynchronizationManager.setActualTransactionActive(true);
            itemService.saveItem();
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test
    public void testRetryRunsOnTransaction() {
        try {
            TransactionSynchronizationManager.setActualTransactionActive(true);
            assertEquals(0, itemService.getRegisteredCalls());
            try {
                itemService.saveItems();
            } catch (OptimisticLockingException expected) {
            }
            assertEquals(3, itemService.getRegisteredCalls());
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }
}
