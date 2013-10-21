package vladmihalcea.hibernate.model.indexlist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagBranch - BagBranch
 *
 * @author Vlad Mihalcea
 */
@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="tree_fk", insertable=false, updatable=false)
    public Tree tree;

    private int index;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "branch_fk")
    @OrderColumn(name = "index")
    private List<Leaf> leaves = new ArrayList<Leaf>();

    public Long getId() {
        return id;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Leaf> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Leaf> leaves) {
        this.leaves = leaves;
    }

    public void addLeaf(Leaf leaf) {
        leaf.setBranch(this);
        getLeaves().add(leaf);
    }
}
