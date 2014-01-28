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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagLeaf - BagLeaf
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagLeaf implements Identifiable {

    public static EntityVisitor<BagLeaf, BagBranch> ENTITY_VISITOR = new EntityVisitor<BagLeaf, BagBranch>(BagLeaf.class) {

        @Override
        public BagBranch getParent(BagLeaf visitingObject) {
            return visitingObject.getBranch();
        }

        @Override
        public List<BagLeaf> getChildren(BagBranch parent) {
            return parent.getLeaves();
        }

        @Override
        public void setChildren(BagBranch parent) {
            parent.setLeaves(new ArrayList<BagLeaf>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    public BagBranch branch;

    private int index;

    public Long getId() {
        return id;
    }

    public BagBranch getBranch() {
        return branch;
    }

    public void setBranch(BagBranch branch) {
        this.branch = branch;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(index);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BagLeaf)) {
            return false;
        }
        BagLeaf that = (BagLeaf) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(index, that.index);
        return eb.isEquals();
    }
}
