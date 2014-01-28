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

package com.vladmihalcea.hibernate.model.baglist;

import com.vladmihalcea.hibernate.model.util.EntityVisitor;
import com.vladmihalcea.hibernate.model.util.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagForest - BagForest
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagForest implements Identifiable {

    public static EntityVisitor<BagForest, Identifiable> ENTITY_VISITOR = new EntityVisitor<BagForest, Identifiable>(BagForest.class) {
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forest", orphanRemoval = true)
    private List<BagTree> trees = new ArrayList<BagTree>();

    public Long getId() {
        return id;
    }

    public List<BagTree> getTrees() {
        return trees;
    }

    public void setTrees(List<BagTree> trees) {
        this.trees = trees;
    }

    public void addTree(BagTree tree) {
        tree.setForest(this);
        getTrees().add(tree);
    }
}
