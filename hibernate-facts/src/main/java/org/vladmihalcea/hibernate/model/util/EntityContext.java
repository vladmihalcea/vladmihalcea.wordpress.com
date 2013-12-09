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
