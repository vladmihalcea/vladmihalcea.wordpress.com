package org.vladmihalcea.hibernate.model.indexlist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagForest - BagForest
 *
 * @author Vlad Mihalcea
 */
@Entity
public class Forest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "forest_fk")
    @OrderColumn(name = "index")
    private List<Tree> trees = new ArrayList<Tree>();

    public Long getId() {
        return id;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public void setTrees(List<Tree> trees) {
        this.trees = trees;
    }

    public void addTree(Tree tree) {
        tree.setForest(this);
        getTrees().add(tree);
    }
}
