package vladmihalcea.hibernate.model.util;

import java.util.List;

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

    public void visit(T object, EntityContext entityContext) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        ClassId<T> objectClassId = new ClassId<T>(clazz, object.getId());
        boolean objectVisited = entityContext.isVisited(objectClassId);
        if (!objectVisited) {
            entityContext.visit(objectClassId, object);
        }
        P parent = getParent(object);
        if (parent != null) {
            @SuppressWarnings("unchecked")
            Class<P> parentClass = (Class<P>) parent.getClass();
            ClassId<P> parentClassId = new ClassId<P>(parentClass, parent.getId());
            if (!entityContext.isVisited(parentClassId)) {
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
