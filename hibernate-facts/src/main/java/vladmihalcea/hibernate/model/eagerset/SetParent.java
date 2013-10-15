package vladmihalcea.hibernate.model.eagerset;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class SetParent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private Set<SetChild> children = new HashSet<SetChild>();

    public SetParent() {
    }

    public Long getId() {
        return id;
    }

    public Set<SetChild> getChildren() {
        return children;
    }

    public void addChild(SetChild child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(SetChild child) {
        children.remove(child);
        child.setParent(null);
    }
}
