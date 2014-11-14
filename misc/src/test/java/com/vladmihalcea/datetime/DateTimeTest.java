package com.vladmihalcea.datetime;

import org.joda.time.*;
import org.joda.time.chrono.ISOChronology;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * DateTimeTest - Date and Time Test
 *
 * @author Vlad Mihalcea
 */
public class DateTimeTest {

    private Logger LOGGER = LoggerFactory.getLogger(DateTimeTest.class);

    @Test
    public void testTimeZonesWithCalendar() throws ParseException {
        assertEquals(0L, newCalendarInstanceMillis("GMT").getTimeInMillis());
        assertEquals(TimeUnit.HOURS.toMillis(-9), newCalendarInstanceMillis("Japan").getTimeInMillis());
        assertEquals(TimeUnit.HOURS.toMillis(10), newCalendarInstanceMillis("Pacific/Honolulu").getTimeInMillis());
        Calendar epoch = newCalendarInstanceMillis("GMT");
        epoch.setTimeZone(TimeZone.getTimeZone("Japan"));
        assertEquals(TimeUnit.HOURS.toMillis(-9), epoch.getTimeInMillis());
    }

    private Calendar newCalendarInstanceMillis(String timeZoneId) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return calendar;
    }

    @Test
    public void testTimeZonesWithDateTime() throws ParseException {
        assertEquals(0L, newDateTimeMillis("GMT").toDate().getTime());
        assertEquals(TimeUnit.HOURS.toMillis(-9), newDateTimeMillis("Japan").toDate().getTime());
        assertEquals(TimeUnit.HOURS.toMillis(10), newDateTimeMillis("Pacific/Honolulu").toDate().getTime());
        DateTime epoch = newDateTimeMillis("GMT");
        assertEquals("1970-01-01T00:00:00.000Z", epoch.toString());
        epoch = epoch.toDateTime(DateTimeZone.forID("Japan"));
        assertEquals(0, epoch.toDate().getTime());
        assertEquals("1970-01-01T09:00:00.000+09:00", epoch.toString());
        MutableDateTime mutableDateTime = epoch.toMutableDateTime();
        mutableDateTime.setChronology(ISOChronology.getInstance().withZone(DateTimeZone.forID("Japan")));
        assertEquals("1970-01-01T09:00:00.000+09:00", epoch.toString());
    }

    private DateTime newDateTimeMillis(String timeZoneId) {
        return new DateTime(DateTimeZone.forID(timeZoneId))
                .withYear(1970)
                .withMonthOfYear(1)
                .withDayOfMonth(1)
                .withTimeAtStartOfDay();
    }

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
