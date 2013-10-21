package vladmihalcea.hibernate.model.util;

import java.util.List;
import java.util.Map;

/**
* EntityVisitor - EntityVisitor
*
* @author Vlad Mihalcea
*/
public abstract class EntityVisitor<T extends Identifiable, P extends Identifiable> {

    private final Class<T> targetClass;

    public EntityVisitor(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<T> getTargetClazz() {
        return targetClass;
    }

    public void visit(T object, Map<ClassId, Object> visitedMap) {
        @SuppressWarnings("unchecked")
        ClassId objectClassId = new ClassId(object.getClass(), object.getId());
        boolean objectVisited = visitedMap.containsKey(objectClassId);
        if (!objectVisited) {
            visitedMap.put(objectClassId, object);
        }
        P parent = getParent(object);
        if (parent != null) {
            @SuppressWarnings("unchecked")
            ClassId parentClassId = new ClassId(parent.getClass(), parent.getId());
            if (!visitedMap.containsKey(parentClassId)) {
                setChildren(parent);
            }
            List<T> children = getChildren(parent);
            if (!objectVisited) {
                children.add(object);
            }
        }

    }

    public P getParent(T visitingObject) {
        return null;
    }

    public List<T> getChildren(P parent) {
        throw new UnsupportedOperationException();
    }

    public void setChildren(P parent) {
        throw new UnsupportedOperationException();
    }
}
