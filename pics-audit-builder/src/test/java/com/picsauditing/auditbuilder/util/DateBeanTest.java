package com.picsauditing.auditbuilder.util;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DateBeanTest {

	@Test
	public void testGetWCBDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.MONTH, 7);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.YEAR, 2011);

		assertEquals("2011", DateBean.getWCBYear(calendar.getTime()));

		calendar.set(Calendar.MONTH, 10);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2011);

		assertEquals("2012", DateBean.getWCBYear(calendar.getTime()));
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
	public void testGetWCBExpirationDate() {
		assertNull(DateBean.getWCBExpirationDate(null));
		assertNull(DateBean.getWCBExpirationDate(""));
		assertNull(DateBean.getWCBExpirationDate("garbage"));

		Date date = DateBean.getWCBExpirationDate("2012");
		assertNotNull(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
		assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIsGracePeriodForWCB() throws Exception {
		// Before grace period
		Date now = new Date("8/31/2012");
		Boolean result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertFalse(result);

		// start of grace period
		now = new Date("11/1/2012");
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// end of year
		now = new Date("12/31/2012");
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// start of year
		now = new Date("1/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// end of grace period
		now = new Date("1/31/2013");
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// after grace period
		now = new Date("2/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertFalse(result);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetEffectiveWCBYear() throws Exception {
		Date now = new Date("8/31/2012");
		Integer result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());

		now = new Date("9/1/2012");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());

		now = new Date("12/31/2012");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());

		now = new Date("1/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());

		now = new Date("1/31/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());

		now = new Date("2/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2013, result.intValue());
	}

    @Test
    public void testDaysBetween_EndOfYearToNewYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, 2009);
        Date startDate = cal.getTime();

        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.YEAR, 2008);
        Date endDate = cal.getTime();

        int daysBetween = DateBean.daysBetween(startDate, endDate);

        assertEquals(-1, daysBetween);
    }

    @Test
    public void testDaysBetween_StartOfMonthToStartOfNextMonth() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.MONTH, 0);
        startDate.set(Calendar.YEAR, 2008);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        endDate.set(Calendar.MONTH, 1);
        endDate.set(Calendar.YEAR, 2008);
        int daysBetween = DateBean.daysBetween(startDate.getTime(), endDate.getTime());
        assertEquals(31, daysBetween);
    }

	@Test
	public void testAddNineMonths() throws ParseException {
		Date original = new GregorianCalendar(2003,GregorianCalendar.MAY,31).getTime();
		Date expected = new GregorianCalendar(2004,GregorianCalendar.FEBRUARY,29).getTime();
		Date actual = DateBean.addMonths(original,9);
		assertEquals(expected, actual);
	}

	@Test
	public void testAddOneMonth() throws ParseException {
		Date original = new GregorianCalendar(2004,GregorianCalendar.JANUARY,31).getTime();
		Date expected = new GregorianCalendar(2004,GregorianCalendar.FEBRUARY,29).getTime();
		Date actual = DateBean.addMonths(original,1);
		assertEquals(expected, actual);
	}
}
