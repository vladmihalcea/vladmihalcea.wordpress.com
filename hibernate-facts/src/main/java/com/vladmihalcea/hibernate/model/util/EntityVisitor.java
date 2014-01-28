/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vladmihalcea.hibernate.model.util;

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
