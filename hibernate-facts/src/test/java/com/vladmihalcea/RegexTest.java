package com.vladmihalcea;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class RegexTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexTest.class);

    private String TEST_VALUE = "ABS, traction control, front and side airbags, Isofix child seat anchor points, no air conditioning, electric windows, \r\nelectrically operated door mirrors";
    private String TEST_VALUE2 = "ABS, traction control, front and side airbags, Isofix child seat anchor points;no air conditioning, electric windows, electrically operated door mirrors";
    private String TEST_VALUE3 = "no air conditioning, electric windows, electrically operated door mirrors";
    private String TEST_VALUE4 = "ABS, traction control, front and side airbags, Isofix child seat anchor points no air conditioning";
    private String NO_TEST_VALUE = "ABS, traction control, front and side airbags, Isofix child seat anchor points mono air conditioning a";

    @Test
    @Ignore
    public void testOverload() {
        double start = System.nanoTime();
        Pattern pattern = Pattern.compile("^(?:.*?(?:\\s|,)+)*no\\s+air\\s+conditioning.*$");
        assertTrue(pattern.matcher(TEST_VALUE).matches());
        double end = System.nanoTime();
        LOGGER.info("Took {} micros", (end - start) / (1000 ));
    }

    @Test
    public void testSlowest() {
        Pattern pattern = Pattern.compile("(?:.*?(?:\\s|,)+)*no\\s+air\\s+conditioning.*?", Pattern.MULTILINE);
        double start = System.nanoTime();
        assertTrue(pattern.matcher(TEST_VALUE).find());
        double end = System.nanoTime();
        LOGGER.info("testSlowest took {} micros", (end - start) / 1000);
    }

    @Test
    public void testSlower() {
        Pattern pattern = Pattern.compile("(?:.*?(?:\\s|,)+)?no\\s+air\\s+conditioning.*?", Pattern.MULTILINE);
        double start = System.nanoTime();
        assertTrue(pattern.matcher(TEST_VALUE).find());
        double end = System.nanoTime();
        LOGGER.info("testSlower took {} micros", (end - start) / 1000);
    }

    @Test
    public void testFine() {
        Pattern pattern = Pattern.compile("(?:.*?\\b)?no\\s+air\\s+conditioning.*?", Pattern.MULTILINE);
        double start = System.nanoTime();
        assertTrue(pattern.matcher(TEST_VALUE).find());
        double end = System.nanoTime();
        LOGGER.info("testFine took {} micros", (end - start) / 1000);
    }

    @Test
    public void testFind() {
        Pattern pattern = Pattern.compile("\\bno\\s+air\\s+conditioning");
        double start = System.nanoTime();
        assertTrue(pattern.matcher(TEST_VALUE).find());
        //assertTrue(pattern.matcher(TEST_VALUE2).find());
        //assertTrue(pattern.matcher(TEST_VALUE3).find());
        //assertTrue(pattern.matcher(TEST_VALUE4).find());
        //assertFalse(pattern.matcher(NO_TEST_VALUE).find());
        double end = System.nanoTime();
        LOGGER.info("testFind took {} micros", (end - start) / 1000);
    }

    @Test
    public void testIndexOd() {
        double start = System.nanoTime();
        assertTrue(TEST_VALUE.indexOf("no air conditioning") > 0);
        //assertTrue(pattern.matcher(TEST_VALUE2).find());
        //assertTrue(pattern.matcher(TEST_VALUE3).find());
        //assertTrue(pattern.matcher(TEST_VALUE4).find());
        //assertFalse(pattern.matcher(NO_TEST_VALUE).find());
        double end = System.nanoTime();
        LOGGER.info("testFind took {} micros", (end - start) / 1000);
    }
}

