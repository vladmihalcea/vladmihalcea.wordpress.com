package vladmihalcea.hibernate.model.baglist;

import vladmihalcea.hibernate.model.util.EntityVisitor;
import vladmihalcea.hibernate.model.util.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagLeaf - BagLeaf
 *
 * @author Vlad Mihalcea
 */
@Entity
public class BagLeaf implements Identifiable {

    public static EntityVisitor<BagLeaf, BagBranch> ENTITY_VISITOR = new EntityVisitor<BagLeaf, BagBranch>(BagLeaf.class) {

        @Override
        public BagBranch getParent(BagLeaf visitingObject) {
            return visitingObject.getBranch();
        }

        @Override
        public List<BagLeaf> getChildren(BagBranch parent) {
            return parent.getLeaves();
        }

        @Override
        public void setChildren(BagBranch parent) {
            parent.setLeaves(new ArrayList<BagLeaf>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    public BagBranch branch;

    private int index;

    public Long getId() {
        return id;
    }

    public BagBranch getBranch() {
        return branch;
    }

    public void setBranch(BagBranch branch) {
        this.branch = branch;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
