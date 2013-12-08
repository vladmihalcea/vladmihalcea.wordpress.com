package org.vladmihalcea.hibernate.model.eagerset;

import javax.persistence.*;

@Entity
public class SetChild {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    private SetParent parent;

    public SetChild() {
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

    public SetParent getParent() {
        return parent;
    }

    public void setParent(SetParent parent) {
        this.parent = parent;
    }
}
