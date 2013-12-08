package org.vladmihalcea.hibernate.model.util;

import java.util.Map;

/**
 * EntityContext - EntityContext
 *
 * @author Vlad Mihalcea
 */
public class EntityContext {

    private final Map<ClassId, Object> visitedMap;

    public EntityContext(Map<ClassId, Object> visitedMap) {
        this.visitedMap = visitedMap;
    }

    public boolean isVisited(ClassId<?> classId) {
        return visitedMap.containsKey(classId);
    }

    public <T> void visit(ClassId<T> classId, T object) {
        visitedMap.put(classId, object);
    }

    public <T> T getObject(ClassId<T> classId) {
        Object object = visitedMap.get(classId);
        return classId.getClazz().cast(object);
    }
}
