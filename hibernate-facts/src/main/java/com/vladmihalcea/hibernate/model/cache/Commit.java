package com.vladmihalcea.hibernate.model.cache;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Commit - Commit
 *
 * @author Vlad Mihalcea
 */
@Entity(name = "Commit")
@Table(name = "commit")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Immutable
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Repository repository;

    @ElementCollection
    @CollectionTable(
            name="commit_change",
            joinColumns=@JoinColumn(name="commit_id")
    )
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    private List<Change> changes = new ArrayList<>();

    public Commit() {
    }

    public Commit(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public List<Change> getChanges() {
        return changes;
    }
}
