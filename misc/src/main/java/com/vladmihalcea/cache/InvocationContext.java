package com.vladmihalcea.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

/**
 * InvocationContext - Method invocation context
 *
 * @author Vlad Mihalcea
 */
public class InvocationContext {

    public static final String TEMPLATE = "%s.%s(%s)";

    private final Class targetClass;
    private final String targetMethod;
    private final Object[] args;

    public InvocationContext(Class targetClass, String targetMethod, Object[] args) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.args = args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(TEMPLATE, targetClass.getName(), targetMethod, Arrays.toString(args));
    }
}
