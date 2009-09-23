package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

public class DateBeanTest extends TestCase {
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
	public void testTimeZone() throws Exception {
		Date sourceDate = new Date(999999);
		Date destDate = DateBean.convertTime(sourceDate, TimeZone.getTimeZone("US/Eastern"), TimeZone.getTimeZone("US/Pacific"));
		assertEquals(sourceDate.getHours() - 3, destDate.getHours());
	}
	
	@Test
	public void testPrettyDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		assertEquals("", DateBean.prettyDate(cal.getTime()));
	}
}
