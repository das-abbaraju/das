package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class DateBeanTest  {
	
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
	
	@SuppressWarnings("deprecation")
	@Test
	public void testTimeZone() throws Exception {
		Date sourceDate = new Date(999999);
		Date destDate = DateBean.convertTime(sourceDate, TimeZone.getTimeZone("US/Eastern"), TimeZone.getTimeZone("US/Pacific"));
		assertEquals(sourceDate.getHours() - 3, destDate.getHours());
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
	
}
