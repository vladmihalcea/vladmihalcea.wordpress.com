package com.vladmihalcea.hibernate.model.cache;

import javax.persistence.Embeddable;

/**
 * Change - Change
 *
 * @author Vlad Mihalcea
 */
@Embeddable
public class Change {

    private String path;

    private String diff;

    public Change() {
    }

    public Change(String path, String diff) {
        this.path = path;
        this.diff = diff;
    }

    public String getPath() {
        return path;
    }

    public String getDiff() {
        return diff;
    }
}
