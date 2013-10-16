package vladmihalcea.hibernate.model.linkedset;

import javax.persistence.*;

@Entity
public class LinkedChild {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    private LinkedParent parent;

    public LinkedChild() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedParent getParent() {
        return parent;
    }

    public void setParent(LinkedParent parent) {
        this.parent = parent;
    }
}
