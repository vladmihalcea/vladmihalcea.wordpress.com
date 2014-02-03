package com.vladmihalcea;

import com.vladmihalcea.service.TimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;

/**
 * SqlFunctionTest - SqlFunctionTest
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class SqlFunctionTest {

    @Resource
    private TimeService springSqlFunctionTimeService;

    @Resource
    private TimeService jooqSqlFunctionTimeService;

    @Test
    public void testSpringSqlFunctionTimeService() {
        String timestamp = springSqlFunctionTimeService.formatTimestamp();
        assertNotNull(timestamp);
    }

    @Test
    public void testJooqSqlFunctionTimeService() {
        String timestamp = jooqSqlFunctionTimeService.formatTimestamp();
        assertNotNull(timestamp);
    }

}
