package com.picsauditing.PICS;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DateBeanTest  {
	TimeZone easternTimeZone = TimeZone.getTimeZone("US/Eastern");
	TimeZone pacificTimeZone = TimeZone.getTimeZone("US/Pacific");
	
	@Test
	public void testShowFormat() throws Exception {
		String formatted = DateBean.toShowFormat("2007-03-15");
		assertEquals("3/15/07", formatted);
	}

	@Test
	public void testParseDate() {
		Date expected = DateBean.parseDate("2001-02-03");

		Date actual;
		actual = DateBean.parseDate("2/3/2001");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("2-3-01");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("2/3/01");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("02-03-2001");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("2001/02/03");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("2001/2/3");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("02/03/2001");
		assertEquals(expected, actual);
		actual = DateBean.parseDate("02/03/01");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDBFormat() {
		Calendar cal = Calendar.getInstance();
		cal.set(2001, Calendar.JANUARY, 1);
		assertEquals("2001-01-01", DateBean.toDBFormat(cal.getTime()));
		
		cal.set(1, Calendar.JANUARY, 1);
		assertEquals("0001-01-01", DateBean.toDBFormat(cal.getTime()));
	}
	
	@Test
	public void testDatesAreEqualInTimezones_PacificEasternEqual() throws Exception {
		Calendar eastern = Calendar.getInstance(easternTimeZone);
		eastern.set(2012, 8, 5, 8, 0, 0);
		Calendar pacific = Calendar.getInstance(pacificTimeZone);
		pacific.set(2012, 8, 5, 5, 0, 0);

		boolean datesAreEqual = DateBean.datesAreEqualInTimeZones(pacific.getTime(), pacificTimeZone, eastern.getTime(), easternTimeZone);
		
		assertTrue(datesAreEqual);
	}
	
	@Test
	public void testDatesAreEqualInTimezones_EasternToPacific() throws Exception {
		Calendar eastern = Calendar.getInstance(easternTimeZone);
		eastern.set(2012, 8, 5, 8, 0, 0);
		Calendar pacific = Calendar.getInstance(pacificTimeZone);
		pacific.set(2012, 8, 5, 5, 0, 0);

		boolean datesAreEqual = DateBean.datesAreEqualInTimeZones(eastern.getTime(), easternTimeZone, pacific.getTime(), pacificTimeZone);
		
		assertTrue(datesAreEqual);
	}
	
	@Test
	public void testDatesAreEqualInTimezones_EasternToPacificNotEqual() throws Exception {
		Calendar eastern = Calendar.getInstance(easternTimeZone);
		eastern.set(2012, 8, 5, 8, 0, 0);
		Calendar pacific = Calendar.getInstance(pacificTimeZone);
		pacific.set(2012, 8, 5, 3, 0, 0);

		boolean datesAreEqual = DateBean.datesAreEqualInTimeZones(eastern.getTime(), easternTimeZone, pacific.getTime(), pacificTimeZone);
		
		assertFalse(datesAreEqual);
	}
	
	@Test
	public void testConvertTime_UsEasternToEuropeLondon() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");
		
		Date destination = DateBean.convertTime(source, easternTimeZone, TimeZone.getTimeZone("Europe/London"));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 13:00:00")));
	}
	
	@Test
	public void testConvertTime_EuropeLondonToUsPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");
		
		Date destination = DateBean.convertTime(source, TimeZone.getTimeZone("Europe/London"), pacificTimeZone);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 00:00:00")));
	}
	
	@Test
	public void testConvertTime_EasternToPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");
		
		Date destination = DateBean.convertTime(source, easternTimeZone, pacificTimeZone);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 05:00:00")));
	}

	@Test
	public void testConvertTime_PacificToPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");
		
		Date destination = DateBean.convertTime(source, pacificTimeZone, pacificTimeZone);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 08:00:00 PDT")));
	}
	
	@Test
	public void testGetNextDayMidnight() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2011, Calendar.JANUARY, 1, 1, 1, 1);
		Date d1 = cal.getTime();
		cal.set(2011, Calendar.JANUARY, 2, 0, 0, 0);
		Date d2 = cal.getTime();
		
		assertEquals(d2, DateBean.getNextDayMidnight(d1));
	}
	
	@Test
	public void testGetYearFromDate() {
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.DATE, 10);
		calendar.set(Calendar.MONTH, 3);
		calendar.set(Calendar.YEAR, 2011);
		
		assertEquals(2011, DateBean.getYearFromDate(calendar.getTime()));
		assertEquals(0, DateBean.getYearFromDate(null));
	}
	
	@Test
	public void testGetMarchOfNextYearWithNull() {
		Date result = DateBean.getMarchOfNextYear(null);
		assertNull(result);
	}
	
	@Test
	public void testGetMarchOfThatYearWithNull() {
		Date result = DateBean.getMarchOfThatYear(null);
		assertNull(result);
	}
	
	@Test
	public void testGetFirstOfNextYearWithNull() {
		Date result = DateBean.getFirstOfNextYear(null);
		assertNull(result);
	}
	
	@Test
	public void testAddFieldWithNull() {
		Date result = DateBean.addField(null, Calendar.DATE, 1);
		assertNull(result);
	}
	
	@Test
	public void testAddFieldWithZeroAmount() {
		Date result = DateBean.addField(new Date(), Calendar.MONTH, 0);
		assertNull(result);
	}
	
	@Test
	public void testGetFirstofMonthWithNull() {
		Date result = DateBean.getFirstofMonth(null, 2);
		assertNull(result);
	}
	
	@Test
	public void testAddDaysWithNullDate() {
		Date result = DateBean.addDays(null, 6);
		assertNull(result);
	}
	
	@Test
	public void testAddDaysWithZeroDays() {
		Date result = DateBean.addDays(new Date(), 0);
		assertNull(result);
	}
	
	@Test
	public void testAddMonthsNullDate() {
		Date result = DateBean.addMonths(null, 2);
		assertNull(result);
	}
	
	@Test
	public void testAddMonthsWithZeroMonths() {
		Date result = DateBean.addMonths(new Date(), 0);
		assertNull(result);
	}
	
	@Test
	public void testGetMarchOfNextYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date marchOfThatYear = DateBean.getMarchOfNextYear(calendar.getTime());
		
		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
	}
	
	@Test
	public void testGetMarchOfNextYearEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date marchOfThatYear = DateBean.setToEndOfDay(DateBean.getMarchOfNextYear(calendar.getTime()));
		
		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
		assertEquals(result.get(Calendar.HOUR_OF_DAY), 23);
		assertEquals(result.get(Calendar.MINUTE), 59);
		assertEquals(result.get(Calendar.SECOND), 59);
	}
	
	@Test
	public void testGetMarchOfThatYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date marchOfThatYear = DateBean.getMarchOfThatYear(calendar.getTime());
		
		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR), result.get(Calendar.YEAR));
	}
	
	@Test
	public void getFirstOfNextYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date marchOfThatYear = DateBean.getFirstOfNextYear(calendar.getTime());
		
		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JANUARY, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
	}
	
	@Test
	public void addField() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date date = DateBean.addField(calendar.getTime(), Calendar.MONTH, 1);
		
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		
		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
	}
	
	@Test
	public void testGetFirstofMonthSameYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 30);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date date = DateBean.getFirstofMonth(calendar.getTime(), 1);
		
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
		assertEquals(2011, result.get(Calendar.YEAR));
	}
	
	@Test
	public void testGetFirstofMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 30);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);
		
		Date date = DateBean.getFirstofMonth(calendar.getTime(), 1);
		
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
		assertEquals(2011, result.get(Calendar.YEAR));
	}
	
	@Test
	public void testGetFirstofMonth_ThisMonth() {
		Calendar now = Calendar.getInstance();
		Calendar result = Calendar.getInstance();
		
		Date date = DateBean.getFirstofMonth(now.getTime(), 0);
		result.setTime(date);
		
		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(now.get(Calendar.MONTH), result.get(Calendar.MONTH));
		assertEquals(now.get(Calendar.YEAR), result.get(Calendar.YEAR));
	}
	
	@Test
	public void testAddDays() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 15);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date date = DateBean.addDays(calendar.getTime(), 5);
		
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		
		assertEquals(20, result.get(Calendar.DATE));
		assertEquals(Calendar.FEBRUARY, result.get(Calendar.MONTH));
		assertEquals(2010, result.get(Calendar.YEAR));
	}
	
	@Test
	public void testAddMonths() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 31);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2012);
		
		Date date = DateBean.addMonths(calendar.getTime(), 4);
		
		Calendar result = Calendar.getInstance();
		result.setTime(date);
		
		assertEquals(31, result.get(Calendar.DATE));
		assertEquals(Calendar.DECEMBER, result.get(Calendar.MONTH));
		assertEquals(2012, result.get(Calendar.YEAR));
	}
	
	@Test
	public void testFormatWithNullValue() {
		assertEquals("", DateBean.format(null, "MM/dd/yyyy"));
	}
	
}
