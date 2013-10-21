package vladmihalcea.hibernate.model.store;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
public class SubVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    private Version version;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(getCode());
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubVersion)) {
            return false;
        }
        SubVersion that = (SubVersion) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getCode(), that.getCode());
        return eb.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("id", id);
        tsb.append("code", getCode());
        return tsb.toString();
    }
}
