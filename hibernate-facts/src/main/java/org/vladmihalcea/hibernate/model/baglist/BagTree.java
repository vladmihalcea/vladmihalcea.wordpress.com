package org.vladmihalcea.hibernate.model.baglist;

import org.vladmihalcea.hibernate.model.util.EntityVisitor;
import org.vladmihalcea.hibernate.model.util.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagTree - BagTree
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagTree implements Identifiable {

    public static EntityVisitor<BagTree, BagForest> ENTITY_VISITOR = new EntityVisitor<BagTree, BagForest>(BagTree.class) {

        @Override
        public BagForest getParent(BagTree visitingObject) {
            return visitingObject.getForest();
        }

        @Override
        public List<BagTree> getChildren(BagForest parent) {
            return parent.getTrees();
        }

        @Override
        public void setChildren(BagForest parent) {
            parent.setTrees(new ArrayList<BagTree>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    public BagForest forest;

    private int index;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tree", orphanRemoval = true)
    private List<BagBranch> branches = new ArrayList<BagBranch>();

    public Long getId() {
        return id;
    }

    public BagForest getForest() {
        return forest;
    }

    public void setForest(BagForest forest) {
        this.forest = forest;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<BagBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<BagBranch> branches) {
        this.branches = branches;
    }

    public void addBranch(BagBranch branch) {
        branch.setTree(this);
        getBranches().add(branch);
    }
}
