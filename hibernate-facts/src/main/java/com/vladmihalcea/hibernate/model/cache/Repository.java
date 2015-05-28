package com.vladmihalcea.hibernate.model.cache;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository - Repository
 *
 * @author Vlad Mihalcea
 */
@Entity(name = "repository")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    private List<Commit> commits = new ArrayList<>();

    public Repository() {
    }

    public Repository(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void addCommit(Commit commit) {
        commits.add(commit);
        commit.setRepository(this);
    }
}