package com.vladmihalcea.datetime;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateTimeTest - Date and Time Test
 *
 * @author Vlad Mihalcea
 */
public class DateTimeTest {

    private Logger LOGGER = LoggerFactory.getLogger(DateTimeTest.class);

    // ISO 8601
    // http://www.w3.org/TR/NOTE-datetime
    @Test
    public void testSimpleDateFormatterWithUTCStrings() throws ParseException {
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "1970-01-01T00:00:00.200Z", 200L);
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "1970-01-01T00:00:00.200Z", 200L);
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "1970-01-01T00:00:00.200+0000", 200L);
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "1970-01-01T00:00:00.200Z", 200L);
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "1970-01-01T00:00:00.200+0000", 200L);
        dateFormatParse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "1970-01-01T00:00:00.200+0100", 200L - 1000 * 60 * 60);
    }

    @Test
    public void testJodaDateTimeWithUTCStrings() throws ParseException {
        jodaTimeParse("1970-01-01T00:00:00.200Z", 200L);
        jodaTimeParse("1970-01-01T00:00:00.200+0000", 200L);
        jodaTimeParse("1970-01-01T00:00:00.200+0100", 200L - 1000 * 60 * 60);
    }

    /**
     * DateFormat parsing utility
     * @param pattern date/time pattern
     * @param dateTimeString date/time string value
     * @param expectedNumericTimestamp expected millis since epoch
     */
    private void dateFormatParse(String pattern, String dateTimeString, long expectedNumericTimestamp) {
        try {
            Date utcDate = new SimpleDateFormat(pattern).parse(dateTimeString);
            if(expectedNumericTimestamp != utcDate.getTime()) {
                LOGGER.warn("Pattern: {}, date: {} actual epoch {} while expected epoch: {}", new Object[]{pattern, dateTimeString, utcDate.getTime(), expectedNumericTimestamp});
            }
        } catch (ParseException e) {
            LOGGER.warn("Pattern: {}, date: {} threw {}", new Object[]{pattern, dateTimeString, e.getClass().getSimpleName()});
        }
    }

    /**
     * Joda-Time parsing utility
     * @param dateTimeString date/time string value
     * @param expectedNumericTimestamp expected millis since epoch
     */
    private void jodaTimeParse(String dateTimeString, long expectedNumericTimestamp) {
        Date utcDate = DateTime.parse(dateTimeString).toDate();
        if(expectedNumericTimestamp != utcDate.getTime()) {
            LOGGER.warn("date: {} actual epoch {} while expected epoch: {}", new Object[]{dateTimeString, utcDate.getTime(), expectedNumericTimestamp});
        }
    }
}
