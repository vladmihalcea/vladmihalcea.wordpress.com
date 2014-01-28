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

package com.vladmihalcea.hibernate.model.indexlist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagTree - BagTree
 *
 * @author Vlad Mihalcea
 */
@Entity
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "forest_fk", insertable = false, updatable = false)
    public Forest forest;

    private int index;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tree_fk")
    @OrderColumn(name = "index")
    private List<Branch> branches = new ArrayList<Branch>();

    public Long getId() {
        return id;
    }

    public Forest getForest() {
        return forest;
    }

    public void setForest(Forest forest) {
        this.forest = forest;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public void addBranch(Branch branch) {
        branch.setTree(this);
        getBranches().add(branch);
    }
}
