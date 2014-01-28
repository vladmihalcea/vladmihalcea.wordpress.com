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
