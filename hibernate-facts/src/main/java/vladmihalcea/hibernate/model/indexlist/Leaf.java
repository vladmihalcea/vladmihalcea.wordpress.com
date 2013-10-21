package vladmihalcea.hibernate.model.indexlist;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BagLeaf - BagLeaf
 *
 * @author Vlad Mihalcea
 */
@Entity
public class Leaf {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="branch_fk", insertable=false, updatable=false)
    public Branch branch;

    private int index;

    public Long getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
