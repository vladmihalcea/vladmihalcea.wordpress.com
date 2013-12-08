package org.vladmihalcea.hibernate.model.indexlist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagTree - BagTree
 *
 * @author Vlad Mihalcea
 */
@Entity
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="forest_fk", insertable=false, updatable=false)
    public Forest forest;

    private int index;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tree_fk")
    @OrderColumn(name = "index")
    private List<Branch> branches = new ArrayList<Branch>();

    public Long getId() {
        return id;
    }

    public Forest getForest() {
        return forest;
    }

    public void setForest(Forest forest) {
        this.forest = forest;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public void addBranch(Branch branch) {
        branch.setTree(this);
        getBranches().add(branch);
    }
}
