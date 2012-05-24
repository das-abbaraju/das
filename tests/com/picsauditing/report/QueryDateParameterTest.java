package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertEqualsToTheSecond;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.fields.QueryDateParameter;

public class QueryDateParameterTest {
	private Calendar cal;

	@Before
	public void setUp() {
		cal = Calendar.getInstance();
	}

	@Test
	public void testConvertDateParameterNull() {
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("").getTime());
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("    ").getTime());
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("\t").getTime());
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
	public void testConvertDateParameterYears() {
		cal.add(Calendar.YEAR, 1);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("1y").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 2);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("2Y").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -2);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-2y").getTime());
	}

	@Test
	public void testConvertDateParameterMonths() {
		cal.add(Calendar.MONTH, 23);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("23m").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-2M").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -12);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-12m").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -13);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-13m").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -22);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-22m").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -24);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-24m").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -23);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-23m").getTime());
	}

	@Test
	public void testConvertDateParameterDays() {
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("4d").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-4D").getTime());
	}

	@Test
	public void testConvertDateParameterHours() {
		cal.add(Calendar.HOUR_OF_DAY, 4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("4h").getTime());

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -4);
		assertEqualsToTheSecond(cal.getTime(), new QueryDateParameter("-4H").getTime());
	}

}
