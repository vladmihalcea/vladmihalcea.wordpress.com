package vladmihalcea.mongo.dao;

import org.springframework.dao.OptimisticLockingFailureException;
import vladmihalcea.mongo.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * ProductRepositoryIT - ProductRepository IT
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-mongo-test.xml")
public class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        mongoTemplate.dropCollection(Product.class);
    }

    @Test
    public void testSave() {
        Product product = new Product();
        product.setId(123L);
        product.setName("Tv");
        productRepository.save(product);
        Product savedProduct = productRepository.findOne(123L);
        assertEquals(savedProduct, product);
        assertEquals(savedProduct.hashCode(), product.hashCode());
        assertEquals(Long.valueOf(0), product.getVersion());
        assertEquals("Tv", product.getName());
        savedProduct.setName("Dvd");
        savedProduct = productRepository.save(savedProduct);
        assertEquals(Long.valueOf(1), savedProduct.getVersion());
        savedProduct.setVersion(0L);
        try {
            productRepository.save(savedProduct);
            fail("Expected OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }
    }

    @Test
    public void testFindAndInsert() {
        Long randomId = new Random().nextLong();
        assertNull(productRepository.findOne(randomId));
        Product product = productRepository.findAndInsert(randomId);
        assertEquals(product.getId(), productRepository.findOne(randomId).getId());
    }

}
