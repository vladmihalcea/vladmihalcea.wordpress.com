package com.vladmihalcea.hibernate.model.fetch;

import javax.persistence.*;

/**
* FetchChild - Fetch Child
*
* @author Vlad Mihalcea
*/
@Entity
public class FetchChild {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    public FetchChild() {
    }

    public FetchChild(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private FetchParent parent;

    public FetchParent getParent() {
        return parent;
    }

    public void setParent(FetchParent parent) {
        this.parent = parent;
    }
}
