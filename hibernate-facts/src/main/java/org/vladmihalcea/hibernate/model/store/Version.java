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

package org.vladmihalcea.hibernate.model.store;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    private Image image;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "version", orphanRemoval = true)
    private Set<SubVersion> subVersions = new LinkedHashSet<SubVersion>();

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Set<SubVersion> getSubVersions() {
        return subVersions;
    }

    public void setSubVersions(Set<SubVersion> subVersions) {
        this.subVersions = subVersions;
    }

    public void addSubVersion(SubVersion subVersion) {
        subVersions.add(subVersion);
        subVersion.setVersion(this);
    }

    public void removeSubVersion(SubVersion subVersion) {
        subVersions.remove(subVersion);
        subVersion.setVersion(null);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(type);
        hcb.append(image);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version that = (Version) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(type, that.getType());
        eb.append(image, that.getImage());
        return eb.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("id", id);
        tsb.append("type", type);
        tsb.append("image", image);
        return tsb.toString();
    }
}
