package vladmihalcea;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
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
import vladmihalcea.hibernate.model.store.Company;
import vladmihalcea.hibernate.model.store.Image;
import vladmihalcea.hibernate.model.store.Product;
import vladmihalcea.hibernate.model.store.WarehouseProductInfo;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateOperationsOrderTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateOperationsOrderTest.class);

    @PersistenceContext(unitName = "testPersistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Before
    public void beforeTest() {
        clean();
    }

    @Test
    public void test() {

        final Long productId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {

                Company company = new Company();
                company.setName("TV Company");
                entityManager.persist(company);

                Product product = new Product("tvCode");
                product.setName("TV");
                product.setCompany(company);

                Image frontImage = new Image();
                frontImage.setName("front image");
                frontImage.setIndex(0);

                Image sideImage = new Image();
                sideImage.setName("side image");
                sideImage.setIndex(1);

                product.addImage(frontImage);
                product.addImage(sideImage);

                WarehouseProductInfo warehouseProductInfo = new WarehouseProductInfo();
                warehouseProductInfo.setQuantity(101);
                product.addWarehouse(warehouseProductInfo);

                entityManager.persist(product);
                return product.getId();
            }
        });
        try {
            transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus transactionStatus) {


                    Product product = entityManager.find(Product.class, productId);
                    assertEquals(2, product.getImages().size());
                    Iterator<Image> imageIterator = product.getImages().iterator();

                    Image frontImage = imageIterator.next();
                    assertEquals("front image", frontImage.getName());
                    assertEquals(0, frontImage.getIndex());
                    Image sideImage = imageIterator.next();
                    assertEquals("side image", sideImage.getName());
                    assertEquals(1, sideImage.getIndex());

                    Image backImage = new Image();
                    sideImage.setName("back image");
                    sideImage.setIndex(1);

                    product.removeImage(sideImage);
                    product.addImage(backImage);
                    product.setName("tv set");

                    entityManager.flush();
                    return null;
                }
            });
            fail("Expected ConstraintViolationException");
        } catch (PersistenceException expected) {
            assertEquals(ConstraintViolationException.class, expected.getCause().getClass());
        }

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Product product = entityManager.find(Product.class, productId);
                assertEquals(2, product.getImages().size());
                Iterator<Image> imageIterator = product.getImages().iterator();

                Image frontImage = imageIterator.next();
                assertEquals("front image", frontImage.getName());
                Image sideImage = imageIterator.next();
                assertEquals("side image", sideImage.getName());

                Image backImage = new Image();
                backImage.setName("back image");
                backImage.setIndex(1);

                //http://docs.jboss.org/hibernate/orm/4.2/javadocs/org/hibernate/event/internal/AbstractFlushingEventListener.html#performExecutions%28org.hibernate.event.spi.EventSource%29
                product.removeImage(sideImage);
                entityManager.flush();

                product.addImage(backImage);
                product.setName("tv set");

                entityManager.flush();
                return null;
            }
        });
    }

    protected void clean() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                entityManager.createQuery("delete from Version where id > 0").executeUpdate();
                entityManager.createQuery("delete from Image where id > 0").executeUpdate();
                entityManager.createQuery("delete from Product where id > 0").executeUpdate();
                entityManager.createQuery("delete from Company where id > 0").executeUpdate();
                return null;
            }
        });
    }
}
