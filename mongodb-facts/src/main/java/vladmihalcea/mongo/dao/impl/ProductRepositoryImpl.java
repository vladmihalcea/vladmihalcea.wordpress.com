package vladmihalcea.mongo.dao.impl;

import vladmihalcea.mongo.dao.ProductCustomRepository;
import vladmihalcea.mongo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * ProductRepository custom implementation.
 *
 * @author Vlad Mihalcea
 */
public class ProductRepositoryImpl implements ProductCustomRepository {

    static interface Properties {
        String ID = "id";
        String NAME = "name";
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Product findAndInsert(Long id) {
        return mongoTemplate.findAndModify(
                new Query(where(Properties.ID).is(id)),
                Update.update(Properties.ID, id),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Product.class
        );
    }
}
