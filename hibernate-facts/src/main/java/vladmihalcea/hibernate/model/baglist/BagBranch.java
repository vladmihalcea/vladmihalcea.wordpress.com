package vladmihalcea.hibernate.model.baglist;

import vladmihalcea.hibernate.model.util.EntityVisitor;
import vladmihalcea.hibernate.model.util.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagBranch - BagBranch
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagBranch implements Identifiable {

    public static EntityVisitor<BagBranch, BagTree> ENTITY_VISITOR = new EntityVisitor<BagBranch, BagTree>(BagBranch.class) {
        @Override
        public BagTree getParent(BagBranch visitingObject) {
            return visitingObject.getTree();
        }

        @Override
        public List<BagBranch> getChildren(BagTree parent) {
            return parent.getBranches();
        }

        @Override
        public void setChildren(BagTree parent) {
            parent.setBranches(new ArrayList<BagBranch>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    public BagTree tree;

    private int index;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "branch")
    private List<BagLeaf> leaves = new ArrayList<BagLeaf>();

    public Long getId() {
        return id;
    }

    public BagTree getTree() {
        return tree;
    }

    public void setTree(BagTree tree) {
        this.tree = tree;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<BagLeaf> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<BagLeaf> leaves) {
        this.leaves = leaves;
    }

    public void addLeaf(BagLeaf leaf) {
        leaf.setBranch(this);
        getLeaves().add(leaf);
    }

    public void removeLeaf(BagLeaf leaf) {
        leaf.setBranch(null);
        getLeaves().remove(leaf);
    }
}
