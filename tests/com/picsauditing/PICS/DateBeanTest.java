package com.picsauditing.PICS;

import java.text.SimpleDateFormat;
import java.util.Date;

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
		String formatted = DateBean.format(new Date(), "MMM yyyy");
		assertEquals("3/15/07", formatted);
	}
}
