package vladmihalcea.hibernate.model.store;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import vladmihalcea.hibernate.model.bag.Child;

import javax.persistence.*;
import java.util.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(updatable = false)
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false, updatable = false)
    private Company company;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product", optional = false)
    private WarehouseProductInfo warehouseProductInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importer_id")
    private Importer importer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @OrderBy("index")
    private Set<Image> images = new LinkedHashSet<Image>();

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
        return tsb.toString();
    }
}
