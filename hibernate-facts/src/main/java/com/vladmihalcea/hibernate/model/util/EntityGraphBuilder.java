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

    private final EntityContext entityContext;

    public EntityGraphBuilder(EntityVisitor[] entityVisitors) {
        visitorsMap = new HashMap<Class, EntityVisitor>();
        for (EntityVisitor entityVisitor : entityVisitors) {
            visitorsMap.put(entityVisitor.getTargetClazz(), entityVisitor);
        }
        entityContext = new EntityContext(new HashMap<ClassId, Object>());
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public EntityGraphBuilder build(List<? extends Identifiable> objects) {
        for (Identifiable object : objects) {
            visit(object);
        }
        return this;
    }

    private <T extends Identifiable, P extends Identifiable> void visit(T object) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        @SuppressWarnings("unchecked")
        EntityVisitor<T, P> entityVisitor = visitorsMap.get(clazz);
        if (entityVisitor == null) {
            throw new IllegalArgumentException("Class " + clazz + " has no entityVisitor!");
        }
        entityVisitor.visit(object, entityContext);
        P parent = entityVisitor.getParent(object);
        if (parent != null) {
            visit(parent);
        }
    }
}
