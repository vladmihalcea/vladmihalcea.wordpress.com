package vladmihalcea.hibernate.model.store;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private int index;

    @ManyToOne
    private Product product;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "image", orphanRemoval = true)
    @OrderBy("type")
    private Set<Version> versions = new LinkedHashSet<Version>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set<Version> getVersions() {
        return versions;
    }

    public void addVersion(Version version) {
        versions.add(version);
        version.setImage(this);
    }

    public void removeVersion(Version version) {
        versions.remove(version);
        version.setImage(null);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(name);
        //hcb.append(product);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Image)) {
            return false;
        }
        Image that = (Image) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(name, that.getName());
        //eb.append(product, that.getProduct());
        return eb.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("id", id);
        tsb.append("name", name);
        tsb.append("index", index);
        tsb.append("product", product);
        return tsb.toString();
    }
}
