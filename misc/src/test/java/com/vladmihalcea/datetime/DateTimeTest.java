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
        jodaParse("1970-01-01T00:00:00.200Z", 200L);
        jodaParse("1970-01-01T00:00:00.200+0000", 200L);
        jodaParse("1970-01-01T00:00:00.200+0100", 200L - 1000 * 60 * 60);
    }

    private void dateFormatParse(String pattern, String stringValue, long expectedEpoch) throws ParseException {
        try {
            Date utcDate = new SimpleDateFormat(pattern).parse(stringValue);
            if(expectedEpoch != utcDate.getTime()) {
                LOGGER.warn("pattern: {}, date: {} actual epoch {} while expected epoch: {}", new Object[]{pattern, stringValue, utcDate.getTime(), expectedEpoch});
            }
        } catch (ParseException e) {
            LOGGER.warn("pattern: {}, date: {} threw {}", new Object[]{pattern, stringValue, e.getClass().getSimpleName()});
        }
    }

    private void jodaParse(String stringValue, long expectedEpoch) {
        Date utcDate = DateTime.parse(stringValue).toDate();
        if(expectedEpoch != utcDate.getTime()) {
            LOGGER.warn("date: {} actual epoch {} while expected epoch: {}", new Object[]{stringValue, utcDate.getTime(), expectedEpoch});
        }
    }
}
