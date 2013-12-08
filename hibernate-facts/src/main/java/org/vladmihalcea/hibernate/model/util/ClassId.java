package org.vladmihalcea.hibernate.model.util;

/**
* ClassId - Class and id Structure
*
* @author Vlad Mihalcea
*/
public class ClassId<T> {

    private final Class<T> clazz;
    private final Long id;

    public ClassId(Class<T> clazz, Long id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassId classId = (ClassId) o;

        if (!clazz.equals(classId.clazz)) return false;
        if (!id.equals(classId.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
