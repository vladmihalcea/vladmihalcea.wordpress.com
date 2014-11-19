package com.vladmihalcea.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-fibonacci_memoizer.xml"})
public class FibonacciMemoizerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FibonacciMemoizerTest.class);

    @Autowired
    private FibonacciService fibonacciService;

    @Test
    public void test_fibonacci() {
        assertEquals(55, fibonacciService.compute(10));
    }
}