package com.picsauditing.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PicsDateFormatTest {

	private SimpleDateFormat dateFormat;
	private Calendar calendar;

	@Before
	public void setUp() throws Exception {
		dateFormat = new SimpleDateFormat("", Locale.ENGLISH);

		calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.DECEMBER, 4);
	}

	@Test
	public void testIso() {
		dateFormat.applyPattern(PicsDateFormat.Iso);

		String isoDate = dateFormat.format(calendar.getTime());

		assertEquals("2010-12-04", isoDate);
	}

	@Test
	public void testIsoWeekday() {
		dateFormat.applyPattern(PicsDateFormat.IsoWeekday);

		String isoDate = dateFormat.format(calendar.getTime());

		assertTrue(isoDate.matches("[A-Za-z]{3}, 2010-12-04"));
	}

	@Test
	public void testIsoLongMonth() {
		dateFormat.applyPattern(PicsDateFormat.IsoLongMonth);

		String isoLongDate = dateFormat.format(calendar.getTime());

		assertEquals("2010-Dec-04", isoLongDate);
	}

	@Test
	public void testMonthAndDay() {
		dateFormat.applyPattern(PicsDateFormat.MonthAndDay);

		String monthAndDay = dateFormat.format(calendar.getTime());

		assertEquals("Dec 04", monthAndDay);
	}

	@Test
	public void testMonthAndYear() {
		dateFormat.applyPattern(PicsDateFormat.MonthAndYear);

		String monthAndYear = dateFormat.format(calendar.getTime());

		assertEquals("Dec 2010", monthAndYear);
	}

	@Test
	public void testTwoDigitYear() {
		dateFormat.applyPattern(PicsDateFormat.TwoDigitYear);

		String twoDigitYear = dateFormat.format(calendar.getTime());

		assertEquals("10", twoDigitYear);
	}

	@Test
	public void testDatetime() {
		dateFormat.applyPattern(PicsDateFormat.Datetime);
		calendar.set(2010, Calendar.DECEMBER, 4, 15, 35);

		String datetime = dateFormat.format(calendar.getTime());

		assertTrue(datetime.matches("2010-12-04 [0-2][0-9]:[0-5][0-9] [A-Z]{3}"));
	}

	@Test
	public void testDatetime12Hour() {
		dateFormat.applyPattern(PicsDateFormat.Datetime12Hour);
		calendar.set(2010, Calendar.DECEMBER, 4, 15, 35);

		String datetime = dateFormat.format(calendar.getTime());

		assertTrue(datetime.matches("2010-12-04 [01][0-9]:[0-5][0-9] [AP]M [A-Z]{3}"));
	}

	@Test
	public void testScheduleAudit() {
		dateFormat.applyPattern(PicsDateFormat.ScheduleAudit);
		calendar.set(2010, Calendar.DECEMBER, 4, 15, 35);

		String scheduleAudit = dateFormat.format(calendar.getTime());

		assertEquals("201012041535", scheduleAudit);
	}












}
