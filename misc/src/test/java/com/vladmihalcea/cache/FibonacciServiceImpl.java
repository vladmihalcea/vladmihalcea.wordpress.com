package com.vladmihalcea.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ComputeService - Compute Service
 *
 * @author Vlad Mihalcea
 */
@Component
public class FibonacciServiceImpl implements FibonacciService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FibonacciServiceImpl.class);

    @Autowired
    private ApplicationContext applicationContext;

    private FibonacciService fibonacciService;

    @PostConstruct
    private void init() {
        fibonacciService = applicationContext.getBean(FibonacciService.class);
    }

    @Memoize
    public int compute(int i) {
        LOGGER.info("Calculate fibonacci for number {}", i);
        if (i == 0 || i == 1)
            return i;
        return fibonacciService.compute(i - 2) + fibonacciService.compute(i - 1);
    }
}
