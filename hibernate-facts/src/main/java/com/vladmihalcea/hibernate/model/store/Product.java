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

package com.vladmihalcea.hibernate.model.store;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(updatable = false)
    private String code;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product", optional = false)
    private WarehouseProductInfo warehouseProductInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importer_id")
    private Importer importer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @OrderBy("index")
    private Set<Image> images = new LinkedHashSet<Image>();

    @Version
    private int version;

    public Product() {
    }

    public Product(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Image> getImages() {
        return images;
    }

    public WarehouseProductInfo getWarehouseProductInfo() {
        return warehouseProductInfo;
    }

    public void setWarehouseProductInfo(WarehouseProductInfo warehouseProductInfo) {
        this.warehouseProductInfo = warehouseProductInfo;
    }

    public Importer getImporter() {
        return importer;
    }

    public void setImporter(Importer importer) {
        this.importer = importer;
    }

    public final int getVersion() {
        return version;
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setProduct(null);
    }

    public void addWarehouse(WarehouseProductInfo warehouseProductInfo) {
        warehouseProductInfo.setProduct(this);
        this.setWarehouseProductInfo(warehouseProductInfo);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(name);
        hcb.append(company);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        Product that = (Product) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(name, that.getName());
        eb.append(company, that.getCompany());
        return eb.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("id", id);
        tsb.append("name", name);
        tsb.append("version", version);
        return tsb.toString();
    }
}
