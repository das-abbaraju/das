package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertEqualsToTheSecond;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import com.picsauditing.report.fields.QueryDateParameter;

public class QueryDateParameterTest {
	private Calendar cal = Calendar.getInstance();

	@Test
	public void testConvertDateParameterNull() {
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("").getTime());
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter(null).getTime());
	}

	@Test
	public void testConvertDateParameterUnixtime() {
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.set(2009, Calendar.FEBRUARY, 13, 23, 31, 30);
		cal.set(Calendar.MILLISECOND, 0);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("1234567890").getTime());
	}

	@Test
	public void testConvertDateParameterOneYear() {
		cal.add(Calendar.YEAR, 1);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("1y").getTime());
	}

	@Test
	public void testConvertDateParameterMinus23Months() {
		cal.add(Calendar.MONTH, -23);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-23m").getTime());
	}

	@Test
	public void testConvertDateParameter456Days() {
		cal.add(Calendar.DAY_OF_YEAR, 4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("4d").getTime());
	}

	@Test
	public void testConvertDateParameterMinus4Hours() {
		cal.add(Calendar.HOUR_OF_DAY, -4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-4h").getTime());
	}
	
}
