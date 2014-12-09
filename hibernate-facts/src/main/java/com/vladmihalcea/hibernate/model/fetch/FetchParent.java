package com.vladmihalcea.hibernate.model.fetch;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
* Parent - Parent
*
* @author Vlad Mihalcea
*/
@Entity
public class FetchParent {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<FetchChild> children = new ArrayList<FetchChild>();

    public Long getId() {
        return id;
    }

    public List<FetchChild> getChildren() {
        return children;
    }

    public void addChild(FetchChild child) {
        child.setParent(this);
        children.add(child);
    }
}
