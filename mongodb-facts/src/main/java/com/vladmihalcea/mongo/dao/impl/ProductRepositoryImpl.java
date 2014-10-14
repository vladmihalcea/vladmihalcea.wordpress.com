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

package com.vladmihalcea.mongo.dao.impl;

import com.vladmihalcea.mongo.dao.ProductCustomRepository;
import com.vladmihalcea.mongo.model.Product;
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
        String ID = "_id";
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
