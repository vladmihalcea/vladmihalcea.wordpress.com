package vladmihalcea;

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
import vladmihalcea.hibernate.model.store.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicatonContext.xml"})
public class HibernateFetchStrategyTest {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateFetchStrategyTest.class);

    @PersistenceContext(unitName = "testPersistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

                Importer importer = new Importer();
                importer.setName("Importer");
                entityManager.persist(importer);
                product.setImporter(importer);

                entityManager.persist(product);
                return product.getId();
            }
        });
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                Product product = entityManager.find(Product.class, productId);
                assertNotNull(product);

                product = entityManager.createQuery(
                        "select p " +
                        "from Product p " +
                        "where p.id = :productId", Product.class)
                        .setParameter("productId", productId)
                        .getSingleResult();
                assertNotNull(product);

                product = entityManager.createQuery(
                        "select p " +
                        "from Product p " +
                        "inner join fetch p.warehouseProductInfo " +
                        "inner join fetch p.importer", Product.class).getSingleResult();
                assertEquals(productId, product.getId());
                Image image = entityManager.createQuery(
                    "select i " +
                    "from Image i " +
                    "inner join fetch i.product p " +
                    "where p.id = :productId", Image.class)
                .setParameter("productId", productId)
                .getResultList().get(0);
                assertNotNull(image);

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
