package vladmihalcea.hibernatebagduplicaterows.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private List<Child> children = new ArrayList<Child>();

    public Parent() {
    }

    public Long getId() {
        return id;
    }

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }

    public List<Child> getChildren() {
        return children;
    }
}
