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

package com.vladmihalcea.hibernate.model.linkedset;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class LinkedParent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    @OrderBy("id")
    private Set<LinkedChild> children = new LinkedHashSet<LinkedChild>();

    public LinkedParent() {
    }

    public Long getId() {
        return id;
    }

    public Set<LinkedChild> getChildren() {
        return children;
    }

    public void addChild(LinkedChild child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(LinkedChild child) {
        children.remove(child);
        child.setParent(null);
    }
}
