package com.picsauditing.report;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.picsauditing.report.fields.QueryDateParameter;

import junit.framework.TestCase;

public class QueryDateParameterTest extends TestCase {
	private Calendar cal = Calendar.getInstance();

	public void testConvertDateParameterNull() {
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("").getTime());
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter(null).getTime());
	}

	public void testConvertDateParameterUnixtime() {
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.set(2009, Calendar.FEBRUARY, 13, 23, 31, 30);
		cal.set(Calendar.MILLISECOND, 0);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("1234567890").getTime());
	}

	public void testConvertDateParameterOneYear() {
		cal.add(Calendar.YEAR, 1);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("1y").getTime());
	}

	public void testConvertDateParameterMinus23Months() {
		cal.add(Calendar.MONTH, -23);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-23m").getTime());
	}

	public void testConvertDateParameter456Days() {
		cal.add(Calendar.DAY_OF_YEAR, 4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("4d").getTime());
	}

	public void testConvertDateParameterMinus4Hours() {
		cal.add(Calendar.HOUR_OF_DAY, -4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-4h").getTime());
	}
	
	private void assertEqualsToTheSecond(Date expected, Date actual) {
		if (expected.getTime() == actual.getTime())
			return;
		long difference = toSeconds(expected.getTime()) - toSeconds(actual.getTime());
		if (difference == 0)
			return;
		failNotEquals("Dates to not match and are " + difference + " seconds apart", expected, actual);
	}

	static private long toSeconds(long milliseconds) {
		return Math.round(milliseconds / 1000);
	}
}
