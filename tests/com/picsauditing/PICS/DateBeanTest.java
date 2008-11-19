package com.picsauditing.PICS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Test;

public class DateBeanTest extends TestCase {
	@Test
	public void testShowFormat() throws Exception {
		String formatted = DateBean.toShowFormat("2007-03-15");
		assertEquals("3/15/07", formatted);
	}

	@Test
	public void testDateFormat() throws Exception {
//		Calendar date = Calendar.getInstance();
//		date.add(Calendar.DAY_OF_YEAR, -1);
//		String outString = DateBean.format(date.getTime(), "M/d/yyyy");
//		System.out.println(outString);

		
		String formatted = DateBean.format(new Date(), "M/dd/yy");
		assertTrue(DateBean.isFirstBeforeSecond("7/01/08", formatted));
	}
	
	@Test
	public void testParseDate() {
		Date expected = DateBean.parseDate("2001-02-03");
		
		Date actual;
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
	

}
