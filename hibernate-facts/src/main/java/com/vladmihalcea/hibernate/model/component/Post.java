package com.vladmihalcea.hibernate.model.component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    @JoinTable(name = "post_comments", joinColumns = @JoinColumn(name = "post_id"))
    @OrderColumn(name = "comment_index")
    private List<Comment> comments = new ArrayList<Comment>();

    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public final int getVersion() {
        return version;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
