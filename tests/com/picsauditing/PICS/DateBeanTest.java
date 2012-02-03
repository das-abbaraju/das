package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;

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
}
