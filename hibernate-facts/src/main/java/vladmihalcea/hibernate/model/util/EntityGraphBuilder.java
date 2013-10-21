package vladmihalcea.hibernate.model.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntityGraphBuilder - EntityGraphBuilder
 *
 * @author Vlad Mihalcea
 */
public class EntityGraphBuilder {

    private final Map<Class, EntityVisitor> visitorsMap;

    private Map<ClassId, Object> visitedMap = new HashMap<ClassId, Object>();

    public EntityGraphBuilder(EntityVisitor[] entityVisitors) {
        visitorsMap = new HashMap<Class, EntityVisitor>();
        for (EntityVisitor entityVisitor : entityVisitors) {
            visitorsMap.put(entityVisitor.getTargetClazz(), entityVisitor);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(ClassId<T> classId) {
        return (T) visitedMap.get(classId);
    }

    public void build(List<? extends Identifiable> objects) {
        for (Identifiable object : objects) {
            visit(object);
        }
    }

    private <T extends Identifiable, P extends Identifiable> void visit(T object) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked")
        EntityVisitor<T, P> entityVisitor = visitorsMap.get(clazz);
        if (entityVisitor == null) {
            throw new IllegalArgumentException("Class " + clazz + " has no entityVisitor!");
        }
        entityVisitor.visit(object, visitedMap);
        P parent = entityVisitor.getParent(object);
        if (parent != null) {
            visit(parent);
        }
    }
}
