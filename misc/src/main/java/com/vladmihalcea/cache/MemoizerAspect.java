package com.vladmihalcea.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MemoizerAspect - Function call memoization utility.
 *
 * @author Vlad Mihalcea
 */
@Aspect
public class MemoizerAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoizerAspect.class);

    @Autowired
    private RequestScopeCache requestScopeCache;

    /**
     * Memoize current call
     * @param pjp joint point
     * @return result
     * @throws Throwable call failure
     */
    @Around("@annotation(com.vladmihalcea.cache.Memoize)")
    public Object memoize(ProceedingJoinPoint pjp) throws Throwable {
        InvocationContext invocationContext = new InvocationContext(
                pjp.getSignature().getDeclaringType(),
                pjp.getSignature().getName(),
                pjp.getArgs()
        );
        Object result = requestScopeCache.get(invocationContext);
        if (RequestScopeCache.NONE == result) {
            result = pjp.proceed();
            LOGGER.info("Memoizing result {}, for method invocation: {}", result, invocationContext);
            requestScopeCache.put(invocationContext, result);
        } else {
            LOGGER.info("Using memoized result: {}, for method invocation: {}", result, invocationContext);
        }
        return result;
    }
}
