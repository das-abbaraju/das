package com.picsauditing.util;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.TimeZone;

public class TimeZoneUtilTest {

	@Test
	public void testGetFormattedTimeStringWithNewTimeZone_WhenUserIsInCentral_ThenTimeIsntChanged() {
		TimeZone timezone = TimeZoneUtil.SERVER_TIME_ZONE;
		DateTime dateTime = new DateTime(2012, 3, 14, 12, 44);

		String isoFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.Iso, dateTime);
		String fullFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.DateAndTimeNoTimezone, dateTime);

		assertEquals("2012-03-14", isoFormattedDate);
		assertEquals("2012-03-14 @ 12:44", fullFormattedDate);
	}

	@Test
	public void testGetFormattedTimeStringWithNewTimeZone_WhenUserIsInPacific_ThenTimeIsTwoHoursEarlier() {
		TimeZone timezone = TimeZone.getTimeZone("PST");
		DateTime dateTime = new DateTime(2012, 3, 14, 15, 5);

		String isoFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.Iso, dateTime);
		String fullFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.DateAndTimeNoTimezone, dateTime);

		assertEquals("2012-03-14", isoFormattedDate);
		assertEquals("2012-03-14 @ 13:05", fullFormattedDate);
	}

	@Test
	public void testGetFormattedTimeStringWithNewTimeZone_WhenUserIsInPacific_ThenTimeIsTwoHoursEarlierAndDayCanChange() {
		TimeZone timezone = TimeZone.getTimeZone("PST");
		DateTime dateTime = new DateTime(2012, 3, 14, 1, 30);

		String isoFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.Iso, dateTime);
		String fullFormattedDate = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.DateAndTimeNoTimezone, dateTime);

		assertEquals("2012-03-13", isoFormattedDate);
		assertEquals("2012-03-13 @ 23:30", fullFormattedDate);
	}
}
