package vladmihalcea.hibernate.model.baglist;

import vladmihalcea.hibernate.model.util.EntityVisitor;
import vladmihalcea.hibernate.model.util.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagForest - BagForest
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagForest implements Identifiable {

    public static EntityVisitor<BagForest, Identifiable> ENTITY_VISITOR = new EntityVisitor<BagForest, Identifiable>(BagForest.class) {
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forest")
    private List<BagTree> trees = new ArrayList<BagTree>();

    public Long getId() {
        return id;
    }

    public List<BagTree> getTrees() {
        return trees;
    }

    public void setTrees(List<BagTree> trees) {
        this.trees = trees;
    }

    public void addTree(BagTree tree) {
        tree.setForest(this);
        getTrees().add(tree);
    }
}
