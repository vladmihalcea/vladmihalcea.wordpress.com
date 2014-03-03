package com.vladmihalcea.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FibonacciGuavaCacheTest - Test for fibonnaci guava caching
 *
 * @author Vlad Mihalcea
 */
public class FibonacciGuavaCacheTest {

    private Logger LOGGER = LoggerFactory.getLogger(FibonacciGuavaCacheTest.class);

    private LoadingCache<Integer, Integer> fibonacciCache = CacheBuilder.newBuilder()
            .maximumSize(3)
            .build(new CacheLoader<Integer, Integer>() {
                public Integer load(Integer i) {
                    if (i == 0)
                        return i;

                    if (i == 1)
                        return 1;

                    LOGGER.info("Calculating f(" + i + ")");
                    return fibonacciCache.getUnchecked(i - 2) + fibonacciCache.getUnchecked(i - 1);
                }
            });

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            LOGGER.info("f(" + i + ") = " + fibonacciCache.getUnchecked(i));
        }
    }
}
