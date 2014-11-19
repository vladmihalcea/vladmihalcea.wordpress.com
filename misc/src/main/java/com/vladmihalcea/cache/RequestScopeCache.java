package com.vladmihalcea.cache;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RequestScopeCache {

    public static final Object NONE = new Object();

    private final Map<InvocationContext, Object> cache = new HashMap<InvocationContext, Object>();

    @SuppressWarnings("unchecked")
    public Object get(InvocationContext invocationContext) {
        return cache.containsKey(invocationContext) ? cache.get(invocationContext) : NONE;
    }

    @SuppressWarnings("unchecked")
    public void put(InvocationContext methodInvocation, Object result) {
        cache.put(methodInvocation, result);
    }
}
