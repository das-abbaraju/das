package com.picsauditing.jpa.entities;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

public class AuditorAvailabilityTest {
	private AuditorAvailability auditorAvailability;

	@Before
	public void setUp() {
		auditorAvailability = new AuditorAvailability();
	}

	@Test
	public void testGetEndDate() throws Exception {
		// Jan 1st 2000 12PM
		DateTime dateTimeStart = new DateTime(2000, 1, 1, 12, 0, 0);
		// Jan 1st 2000 12:45PM
		DateTime dateTimeEnd = new DateTime(2000, 1, 1, 12, 45, 0);

		Whitebox.setInternalState(auditorAvailability, "startDate", dateTimeStart.toDate());

		Whitebox.setInternalState(auditorAvailability, "duration", 45);
		assertEquals(dateTimeEnd.toDate(), auditorAvailability.getEndDate());

		Whitebox.setInternalState(auditorAvailability, "duration", 1);
		dateTimeEnd = dateTimeStart.plusMinutes(1);
		assertEquals(dateTimeEnd.toDate(), auditorAvailability.getEndDate());

		// Zeroed out duration should give us the start date
		Whitebox.setInternalState(auditorAvailability, "duration", 0);
		assertEquals(dateTimeStart.toDate(), auditorAvailability.getEndDate());
	}

	@Test
	public void testGetTimeZoneStartDate() throws Exception {
		// Jan 1st 2000 12PM
		// AuditorAvailability interprets this as from CST
		DateTime dateTime = new DateTime(2000, 1, 1, 12, 0, 0);

		Whitebox.setInternalState(auditorAvailability, "startDate", dateTime.toDate());

		assertEquals("10:00 AM", auditorAvailability.getTimeZoneStartDate("PST"));
		assertEquals("11:00 AM", auditorAvailability.getTimeZoneStartDate("MST"));
		assertEquals("12:00 PM", auditorAvailability.getTimeZoneStartDate("CST"));
		assertEquals("01:00 PM", auditorAvailability.getTimeZoneStartDate("EST"));
	}

	@Test
	public void testGetTimeZoneEndDate() throws Exception {
		// Jan 1st 2000 12PM CST
		// AuditorAvailability interprets this as from CST
		DateTime dateTime = new DateTime(2000, 1, 1, 12, 0, 0);

		Whitebox.setInternalState(auditorAvailability, "startDate", dateTime.toDate());
		// Ending at 1:30PM CST
		Whitebox.setInternalState(auditorAvailability, "duration", 90);

		assertEquals("11:30 AM PST", auditorAvailability.getTimeZoneEndDate("PST"));
		assertEquals("12:30 PM MST", auditorAvailability.getTimeZoneEndDate("MST"));
		assertEquals("01:30 PM CST", auditorAvailability.getTimeZoneEndDate("CST"));
		assertEquals("02:30 PM EST", auditorAvailability.getTimeZoneEndDate("EST"));
	}
}
