package com.picsauditing.PICS;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.util.PicsDateFormat;

public class DateBeanTest {

	TimeZone easternTimeZone = TimeZone.getTimeZone("US/Eastern");
	TimeZone pacificTimeZone = TimeZone.getTimeZone("US/Pacific");

	private static List<String> february03_2001 = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("2/3/2001");
			add("2-3-01");
			add("2/3/01");
			add("02-03-2001");
			add("2001/02/03");
			add("2001/2/3");
			add("02/03/2001");
			add("02/03/01");
			add("February 03, 01");
			add("February 03 01");
			add("February 03, 2001");
			add("February 03 2001");
			add("02 03 2001");
			add("03 February 2001");
			add("02/03.2001");
			add("02.03.2001");
			add("02/03.01");
			add("02.03.01");
			add("Feb 03, 01");
			add("Feb 03 01");
			add("Feb 03, 2001");
			add("Feb 03 2001");
			add("03 Feb 2001");
		}
	};

	private static List<String> february03_2001_WithTimes = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("2/3/2001 08:32 AM PDT");
			add("2/3/2001 08:32 AM");
			add("2/3/2001 8:32 AM");
			add("February 03, 2001 08:32 AM PDT");
			add("February 03, 2001 08:32 AM");
			add("February 03, 2001 8:32 AM");
		}
	};

	private static List<String> separators = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("/");
			add("-");
			add(".");
			add(" ");
		}
	};

	private static List<String> dateTemplates = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("ONE_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY{SEPARATOR}FOUR_DIGIT_YEAR");
			add("TWO_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY{SEPARATOR}FOUR_DIGIT_YEAR");
			add("ONE_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY{SEPARATOR}FOUR_DIGIT_YEAR");
			add("TWO_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY{SEPARATOR}FOUR_DIGIT_YEAR");

			add("ONE_DIGIT_DAY{SEPARATOR}ONE_DIGIT_MONTH{SEPARATOR}FOUR_DIGIT_YEAR");
			add("ONE_DIGIT_DAY{SEPARATOR}TWO_DIGIT_MONTH{SEPARATOR}FOUR_DIGIT_YEAR");
			add("TWO_DIGIT_DAY{SEPARATOR}ONE_DIGIT_MONTH{SEPARATOR}FOUR_DIGIT_YEAR");
			add("TWO_DIGIT_DAY{SEPARATOR}TWO_DIGIT_MONTH{SEPARATOR}FOUR_DIGIT_YEAR");

			add("ONE_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY{SEPARATOR}TWO_DIGIT_YEAR");
			add("TWO_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY{SEPARATOR}TWO_DIGIT_YEAR");
			add("ONE_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY{SEPARATOR}TWO_DIGIT_YEAR");
			add("TWO_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY{SEPARATOR}TWO_DIGIT_YEAR");

			add("FOUR_DIGIT_YEAR{SEPARATOR}TWO_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY");
			add("FOUR_DIGIT_YEAR{SEPARATOR}TWO_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY");
			add("FOUR_DIGIT_YEAR{SEPARATOR}ONE_DIGIT_MONTH{SEPARATOR}TWO_DIGIT_DAY");
			add("FOUR_DIGIT_YEAR{SEPARATOR}ONE_DIGIT_MONTH{SEPARATOR}ONE_DIGIT_DAY");

			add("LONG_MONTH_NAME ONE_DIGIT_DAY, TWO_DIGIT_YEAR");
			add("SHORT_MONTH_NAME ONE_DIGIT_DAY, TWO_DIGIT_YEAR");
			add("LONG_MONTH_NAME ONE_DIGIT_DAY TWO_DIGIT_YEAR");
			add("SHORT_MONTH_NAME ONE_DIGIT_DAY TWO_DIGIT_YEAR");

			add("LONG_MONTH_NAME ONE_DIGIT_DAY, FOUR_DIGIT_YEAR");
			add("SHORT_MONTH_NAME ONE_DIGIT_DAY, FOUR_DIGIT_YEAR");
			add("LONG_MONTH_NAME ONE_DIGIT_DAY FOUR_DIGIT_YEAR");
			add("SHORT_MONTH_NAME ONE_DIGIT_DAY FOUR_DIGIT_YEAR");

		}
	};

	@Test
	public void testShowFormat() throws Exception {
		String formatted = DateBean.toShowFormat("2007-03-15");
		assertEquals("3/15/07", formatted);
	}

	@Ignore("too slow for every test run")
	@Test
	public void testParseDate_EveryDayForNextYearStartingToday() {
		int DAYS_IN_A_YEAR = 365;
		testParseDate_EveryDayForNextXDaysStartingToday(DAYS_IN_A_YEAR);
	}

	@Test
	public void testParseDate_EveryDayForNextTwoWeeksStartingToday() {
		testParseDate_EveryDayForNextXDaysStartingToday(14);
	}

	public void testParseDate_EveryDayForNextXDaysStartingToday(int daysToTest) {
		Calendar testDate = startDateZeroTime();

		for (int days = 0; days < daysToTest; days++) {
			parseDateAllPatternsAndSeparators(separators, testDate);
			testDate.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	@Test
	public void testParseDate_TodayEveryMonthFor1Years() {
		testParseDate_TodayEveryMonthForXYears(1);
	}

	private void testParseDate_TodayEveryMonthForXYears(int yearsToTest) {
		int MONTHS_TO_TEST = 12 * yearsToTest;
		Calendar testDate = startDateZeroTime();

		for (int count = 0; count < MONTHS_TO_TEST; count++) {
			parseDateAllPatternsAndSeparators(separators, testDate);
			testDate.add(Calendar.MONTH, 1);
		}
	}

	private Calendar startDateZeroTime() {
		Calendar testDate = Calendar.getInstance();
		testDate.set(Calendar.HOUR_OF_DAY, 0);
		testDate.set(Calendar.MINUTE, 0);
		testDate.set(Calendar.SECOND, 0);
		testDate.set(Calendar.MILLISECOND, 0);
		return testDate;
	}

	private void parseDateAllPatternsAndSeparators(List<String> separators, Calendar testDate) {
		for (String template : dateTemplates) {
			if (template.contains("{SEPARATOR}")) {
				for (String separator : separators) {
					String pattern = template.replaceAll("\\{SEPARATOR\\}", separator);
					runParseDateTest(testDate, pattern);
				}
			} else {
				runParseDateTest(testDate, template);
			}
		}
	}

	private void runParseDateTest(Calendar testDate, String pattern) {
		pattern = replaceTemplatePatternsWithTestDate(pattern, testDate);
		Date actual = DateBean.parseDate(pattern);
		try {
			assertEquals(pattern, testDate.getTime(), actual);
		} catch (AssertionError e) {
			// because we're allowing day month year and month day year this
			// will sometimes cause
			// a misparse such as:
			// 1/8/2012 expected:<Wed Aug 01 00:00:00 MDT 2012> but was:<Sun Jan
			// 08 00:00:00 MST 2012>
			int day = testDate.get(Calendar.DAY_OF_MONTH);
			if (day > 12) {
				// this is only an error if the day cannot be a month
				throw e;
			}
		}
	}

	private String replaceTemplatePatternsWithTestDate(String template, Calendar testDate) {
		SimpleDateFormat formatter = new SimpleDateFormat();
		int day = testDate.get(Calendar.DAY_OF_MONTH);
		int month = testDate.get(Calendar.MONTH) + 1;
		int year = testDate.get(Calendar.YEAR);

		String pattern = template.replaceAll("ONE_DIGIT_DAY", day + "");
		String twoDigitDay = (day < 9) ? "0" + day : day + "";
		pattern = pattern.replaceAll("TWO_DIGIT_DAY", twoDigitDay);

		pattern = pattern.replaceAll("ONE_DIGIT_MONTH", month + "");
		String twoDigitMonth = (month < 9) ? "0" + month : month + "";
		pattern = pattern.replaceAll("TWO_DIGIT_MONTH", twoDigitMonth);

		pattern = pattern.replaceAll("FOUR_DIGIT_YEAR", year + "");
		String twoDigitYear = (year > 2000) ? (year - 2000) + "" : (year - 1900) + "";
		pattern = pattern.replaceAll("TWO_DIGIT_YEAR", twoDigitYear);

		formatter.applyPattern("MMMMM");
		String longMonthName = formatter.format(testDate.getTime());
		pattern = pattern.replaceAll("LONG_MONTH_NAME", longMonthName);

		formatter.applyPattern("MMMM");
		String shortMonthName = formatter.format(testDate.getTime());
		pattern = pattern.replaceAll("SHORT_MONTH_NAME", shortMonthName);

		return pattern;
	}

	@Test
	public void testParseDate_Feb_03_2001() {
		Date expected = DateBean.parseDate("2001-02-03");

		for (String pattern : february03_2001) {
			Date actual = DateBean.parseDate(pattern);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testParseDateTime() throws Exception {
		Date expected = DateBean.parseDate("2001-02-03 08:32 AM PDT");

		for (String pattern : february03_2001_WithTimes) {
			Date actual = DateBean.parseDate(pattern);
			assertEquals(expected, actual);
		}

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
	public void testConvertTime_UsEasternToEuropeLondon() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");

		Date destination = DateBean.convertTime(source, easternTimeZone, TimeZone.getTimeZone("Europe/London"));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 13:00:00")));
	}

	@Test
	public void testConvertTime_EuropeLondonToUsPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");

		Date destination = DateBean.convertTime(source, TimeZone.getTimeZone("Europe/London"), pacificTimeZone);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 00:00:00")));
	}

	@Test
	public void testConvertTime_EasternToPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");

		Date destination = DateBean.convertTime(source, easternTimeZone, pacificTimeZone);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 05:00:00")));
	}

	@Test
	public void testConvertTime_PacificToPacific() throws Exception {
		Date source = DateBean.parseDateTime("09-05-2012 8:00 am");

		Date destination = DateBean.convertTime(source, pacificTimeZone, pacificTimeZone);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
		assertThat(sdf.format(destination.getTime()), is(equalTo("2012-09-05 08:00:00 PDT")));
	}

	@Test
	public void testGetNextDayMidnight_NullReturnsNullAndDoesntCauseException() {
		Date nextDayMidnight = DateBean.getNextDayMidnight(null);

		assertNull(nextDayMidnight);
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

	@Test
	public void testGetYearFromDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DATE, 10);
		calendar.set(Calendar.MONTH, 3);
		calendar.set(Calendar.YEAR, 2011);

		assertEquals(2011, DateBean.getYearFromDate(calendar.getTime()));
		assertEquals(0, DateBean.getYearFromDate(null));
	}

	@Test
	public void testGetWCBDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.MONTH, 7);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.YEAR, 2011);

		assertEquals("2011", DateBean.getWCBYear(calendar.getTime()));

		calendar.set(Calendar.MONTH, 8);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2011);

		assertEquals("2012", DateBean.getWCBYear(calendar.getTime()));
	}

	@Test
	public void testGetMarchOfNextYearWithNull() {
		Date result = DateBean.getMarchOfNextYear(null);
		assertNull(result);
	}

	@Test
	public void testGetMarchOfThatYearWithNull() {
		Date result = DateBean.getMarchOfThatYear(null);
		assertNull(result);
	}

	@Test
	public void testGetFirstOfNextYearWithNull() {
		Date result = DateBean.getFirstOfNextYear(null);
		assertNull(result);
	}

	@Test
	public void testAddFieldWithNull() {
		Date result = DateBean.addField(null, Calendar.DATE, 1);
		assertNull(result);
	}

	@Test
	public void testAddFieldWithZeroAmount() {
		Date result = DateBean.addField(new Date(), Calendar.MONTH, 0);
		assertNull(result);
	}

	@Test
	public void testGetFirstofMonthWithNull() {
		Date result = DateBean.getFirstofMonth(null, 2);
		assertNull(result);
	}

	@Test
	public void testAddDaysWithNullDate() {
		Date result = DateBean.addDays(null, 6);
		assertNull(result);
	}

	@Test
	public void testAddDaysWithZeroDays() {
		Date result = DateBean.addDays(new Date(), 0);
		assertNull(result);
	}

	@Test
	public void testAddMonthsNullDate() {
		Date result = DateBean.addMonths(null, 2);
		assertNull(result);
	}

	@Test
	public void testAddMonthsWithZeroMonths() {
		Date result = DateBean.addMonths(new Date(), 0);
		assertNull(result);
	}

	@Test
	public void testGetMarchOfNextYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date marchOfThatYear = DateBean.getMarchOfNextYear(calendar.getTime());

		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
	}

	@Test
	public void testGetMarchOfNextYearEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date marchOfThatYear = DateBean.setToEndOfDay(DateBean.getMarchOfNextYear(calendar.getTime()));

		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
		assertEquals(result.get(Calendar.HOUR_OF_DAY), 23);
		assertEquals(result.get(Calendar.MINUTE), 59);
		assertEquals(result.get(Calendar.SECOND), 59);
	}

	@Test
	public void testGetMarchOfThatYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date marchOfThatYear = DateBean.getMarchOfThatYear(calendar.getTime());

		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.MARCH, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR), result.get(Calendar.YEAR));
	}

	@Test
	public void getFirstOfNextYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date marchOfThatYear = DateBean.getFirstOfNextYear(calendar.getTime());

		Calendar result = Calendar.getInstance();
		result.setTime(marchOfThatYear);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JANUARY, result.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.YEAR) + 1, result.get(Calendar.YEAR));
	}

	@Test
	public void addField() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 5);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date date = DateBean.addField(calendar.getTime(), Calendar.MONTH, 1);

		Calendar result = Calendar.getInstance();
		result.setTime(date);

		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
	}

	@Test
	public void testGetFirstofMonthSameYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 30);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date date = DateBean.getFirstofMonth(calendar.getTime(), 1);

		Calendar result = Calendar.getInstance();
		result.setTime(date);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
		assertEquals(2011, result.get(Calendar.YEAR));
	}

	@Test
	public void testGetFirstofMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 30);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2011);

		Date date = DateBean.getFirstofMonth(calendar.getTime(), 1);

		Calendar result = Calendar.getInstance();
		result.setTime(date);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(Calendar.JULY, result.get(Calendar.MONTH));
		assertEquals(2011, result.get(Calendar.YEAR));
	}

	@Test
	public void testGetFirstofMonth_ThisMonth() {
		Calendar now = Calendar.getInstance();
		Calendar result = Calendar.getInstance();

		Date date = DateBean.getFirstofMonth(now.getTime(), 0);
		result.setTime(date);

		assertEquals(1, result.get(Calendar.DATE));
		assertEquals(now.get(Calendar.MONTH), result.get(Calendar.MONTH));
		assertEquals(now.get(Calendar.YEAR), result.get(Calendar.YEAR));
	}

	@Test
	public void testAddDays() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 15);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2010);

		Date date = DateBean.addDays(calendar.getTime(), 5);

		Calendar result = Calendar.getInstance();
		result.setTime(date);

		assertEquals(20, result.get(Calendar.DATE));
		assertEquals(Calendar.FEBRUARY, result.get(Calendar.MONTH));
		assertEquals(2010, result.get(Calendar.YEAR));
	}

	@Test
	public void testAddMonths() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 31);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2012);

		Date date = DateBean.addMonths(calendar.getTime(), 4);

		Calendar result = Calendar.getInstance();
		result.setTime(date);

		assertEquals(31, result.get(Calendar.DATE));
		assertEquals(Calendar.DECEMBER, result.get(Calendar.MONTH));
		assertEquals(2012, result.get(Calendar.YEAR));
	}

	@Test
	public void testFormatWithNullValue() {
		assertEquals("", DateBean.format(null, PicsDateFormat.Iso));
	}

	@Test
	public void testGetWCBExpirationDate() {
		assertNull(DateBean.getWCBExpirationDate(null));
		assertNull(DateBean.getWCBExpirationDate(""));
		assertNull(DateBean.getWCBExpirationDate("garbage"));

		Date date = DateBean.getWCBExpirationDate("2012");
		assertNotNull(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
		assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testBusinessDaysBetween() {
		assertEquals(0, DateBean.BusinessDaysBetween(new LocalDate(2012, 8, 25), new LocalDate(2012, 8, 24)));
		assertEquals(1, DateBean.BusinessDaysBetween(new LocalDate(2012, 8, 23), new LocalDate(2012, 8, 24)));
		assertEquals(4, DateBean.BusinessDaysBetween(new LocalDate(2012, 8, 20), new LocalDate(2012, 8, 24)));
		assertEquals(5, DateBean.BusinessDaysBetween(new LocalDate(2012, 8, 14), new LocalDate(2012, 8, 21)));
	}

	@Test
	public void testIsMoreThanXMonthsAgo() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		Date threeMonthsAgo = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		cal.add(Calendar.DATE, 1);
		Date justUnderThreeMonthsAgo = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		cal.add(Calendar.DATE, -1);
		Date justOverThreeMonthsAgo = cal.getTime();

		assertTrue(DateBean.isMoreThanXMonthsAgo(threeMonthsAgo, 2));
		assertTrue(DateBean.isMoreThanXMonthsAgo(justOverThreeMonthsAgo, 3));

		assertFalse(DateBean.isMoreThanXMonthsAgo(justUnderThreeMonthsAgo, 3));
		assertFalse(DateBean.isMoreThanXMonthsAgo(threeMonthsAgo, 4));
		assertFalse(DateBean.isMoreThanXMonthsAgo(threeMonthsAgo, 5));

	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testIsGracePeriodForWCB() throws Exception {
		// Before grace period
		Date now = new Date("8/31/2012"); 
		Boolean result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertFalse(result);
		
		// start of grace period
		now = new Date("9/1/2012"); 
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// end of year
		now = new Date("12/31/2012"); 
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);

		// start of year
		now = new Date("1/1/2013"); 
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);
		
		// end of grace period
		now = new Date("1/31/2013"); 
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertTrue(result);
		
		// after grace period
		now = new Date("2/1/2013"); 
		result = Whitebox.invokeMethod(DateBean.class, "isGracePeriodForWCB", now);
		assertFalse(result);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetEffectiveWCBYear() throws Exception {
		Date now = new Date("8/31/2012");
		Integer result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());
		
		now = new Date("9/1/2012");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());
		
		now = new Date("12/31/2012");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());
		
		now = new Date("1/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());
		
		now = new Date("1/31/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2012, result.intValue());
		
		now = new Date("2/1/2013");
		result = Whitebox.invokeMethod(DateBean.class, "getEffectiveWCBYear", now);
		assertEquals(2013, result.intValue());
	}
}
