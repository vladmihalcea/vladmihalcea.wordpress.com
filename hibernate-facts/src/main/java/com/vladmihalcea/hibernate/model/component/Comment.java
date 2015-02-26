package com.vladmihalcea.hibernate.model.component;

import javax.persistence.Embeddable;

@Embeddable
public class Comment {

    private String review;

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}