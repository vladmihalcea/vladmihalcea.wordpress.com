package vladmihalcea.hibernate.model.linkedset;

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
