package com.picsauditing.PICS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;

public class DateBean {
	public static String NULL_DATE = "0/0/00";
	public static String NULL_DATE_DB = "0000-00-00";
	public static SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
	public static SimpleDateFormat DBFormat = new SimpleDateFormat(PicsDateFormat.Iso);
	ArrayList<Integer> nextMonths;

	public static String PQF_EXPIRED_CUTOFF = "2008-01-01";
	public static String OLD_OFFICE_CUTOFF = "2006-08-27";
	public static String MonthNames[] = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };

	protected static Map<Integer, String> times = new TreeMap<Integer, String>();

	private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

	static {
		times.put(0, "06:00 AM");
		times.put(1, "06:30 AM");
		times.put(2, "07:00 AM");
		times.put(3, "07:30 AM");
		times.put(4, "08:00 AM");
		times.put(5, "08:30 AM");
		times.put(6, "09:00 AM");
		times.put(7, "09:30 AM");
		times.put(8, "10:00 AM");
		times.put(9, "10:30 AM");
		times.put(10, "11:00 AM");
		times.put(11, "11:30 AM");
		times.put(12, "12:00 PM");
		times.put(13, "12:30 PM");
		times.put(14, "01:00 PM");
		times.put(15, "01:30 PM");
		times.put(16, "02:00 PM");
		times.put(17, "02:30 PM");
		times.put(18, "03:00 PM");
		times.put(19, "03:30 PM");
		times.put(20, "04:00 PM");
		times.put(21, "04:30 PM");
		times.put(22, "05:00 PM");
		times.put(23, "05:30 PM");
		times.put(24, "06:00 PM");
	}

	// some patterns come from logs of what users are actually entering
	// do two digit year date patterns first
	private static List<String> datePatterns = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("MM-dd-yy");
			add("MM/dd/yy");
			add("MM/dd.yy");
			add("MM.dd.yy");
			add("MM dd yy");
			add("MMMMM dd, yy");
			add("MMMMM dd yy");
			add("MMMM dd, yy");
			add("MMMM dd yy");

			add(PicsDateFormat.Iso);
			add("yyyy/MM/dd");
			add("yyyy.MM.dd");
			add("yyyy MM dd");
			add("MM-dd-yyyy");
			add("MM/dd/yyyy");
			add("dd/MM/yyyy");
			add("dd-MM-yyyy");
			add("dd.MM.yyyy");
			add("dd MM yyyy");
			add("dd MMMMM yyyy");
			add("dd MMMM yyyy");
			add("MMMMM dd, yyyy");
			add("MMMMM dd yyyy");
			add("MMMM dd, yyyy");
			add("MMMM dd yyyy");
			add("MM dd yyyy");
			add("MM/dd.yyyy");
			add("MM.dd.yyyy");
		}
	};

	public static String toDBFormat(String month, String day, String year) throws Exception {
		return toDBFormat(year + "-" + month + "-" + day);
	}

	public static String toDBFormat(String dateString) throws Exception {
		if (dateString == null) {
			return null;
		}

		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		if (Strings.EMPTY_STRING.equals(dateString) || NULL_DATE.equals(dateString)) {
			return NULL_DATE_DB;
		}

		try {
			java.util.Date tempDate = showFormat.parse(dateString);
			return toDBFormat(tempDate);
		} catch (Exception e) {
			return null;
		}
	}

	public static String toDBFormat(Date fromDate) {
		if (fromDate == null) {
			return null;
		}

		SimpleDateFormat DBFormat = new SimpleDateFormat(PicsDateFormat.Iso);
		return DBFormat.format(fromDate);
	}

	public static String toShowFormat(Object date) throws Exception {
		if (date == null) {
			return Strings.EMPTY_STRING;
		}

		if (date instanceof Date) {
			return toShowFormat(date.toString());
		}

		if (date instanceof String) {
			return toShowFormat(date.toString());
		}

		return Strings.EMPTY_STRING;
	}

	public static String toShowFormat(String dateString) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		SimpleDateFormat DBFormat = new SimpleDateFormat(PicsDateFormat.Iso);

		if (null == dateString || "0000-00-00".equals(dateString) || Strings.EMPTY_STRING.equals(dateString)) {
			return Strings.EMPTY_STRING;
		}

		String temp = "";
		try {
			Date tempDate = DBFormat.parse(dateString);
			temp = showFormat.format(tempDate);
		} catch (Exception e) {
			logger.error("Invalid DB Date format in DateBean.toShowFormat(): failed converting {} to {}", dateString,
					temp);
			temp = "";
		}

		return temp;
	}

	public static String format(Date date, String format) {
		if (Strings.isEmpty(format)) {
			format = "M/d/yy";
		}

		if (date == null) {
			return Strings.EMPTY_STRING;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static String getTodaysDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("M/d/yy");
		String temp = format.format(cal.getTime());

		return temp;
	}

	/**
	 * WCB year starts September 1 the next year e.g. Sep.1, 2012 would be 2013
	 * e.g. Aug 31, 2012 would be 2012
	 */
	public static String getWCBYear(Date date) {
		Calendar wcbYearStart = Calendar.getInstance();
		if (date != null) {
			wcbYearStart.setTime(date);
		}

		wcbYearStart.set(Calendar.MONTH, Calendar.SEPTEMBER);
		wcbYearStart.set(Calendar.DAY_OF_MONTH, 1);

		Calendar now = Calendar.getInstance();
		if (date != null) {
			now.setTime(date);
		}

		if (daysBetween(wcbYearStart.getTime(), now.getTime()) < 0) {
			return Strings.EMPTY_STRING + wcbYearStart.get(Calendar.YEAR);
		}

		return Strings.EMPTY_STRING + (wcbYearStart.get(Calendar.YEAR) + 1);
	}

	public static String getWCBYear() {
		return getWCBYear(null);
	}

	public static int getPreviousWCBYear() {
		return getEffectiveWCBYear(new Date());
	}

	/**
	 * Called when it's already been determined that the current year WCB is
	 * incomplete and we are trying to see if it's okay to look back to last
	 * year's WCB, or if they are stuck with this year's
	 */
	private static int getEffectiveWCBYear(Date now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		if (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DATE) <= 31) {
			return calendar.get(Calendar.YEAR) - 1;
		}

		return calendar.get(Calendar.YEAR);
	}

	/**
	 * The Grace Period is from September 1 - January 31 of next year.
	 *
	 * @return
	 */
	public static boolean isGracePeriodForWCB() {
		return isGracePeriodForWCB(new Date());
	}

	private static boolean isGracePeriodForWCB(Date now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		int month = calendar.get(Calendar.MONTH);
		if (month >= Calendar.SEPTEMBER && month <= Calendar.DECEMBER) {
			return true;
		}

		if (month == Calendar.JANUARY && calendar.get(Calendar.DATE) <= 31) {
			return true;
		}

		return false;
	}

	/**
	 * expiration date is January 31st.
	 */
	public static Date getWCBExpirationDate(String year) {
		Calendar expirationDate = Calendar.getInstance();

		try {
			expirationDate.set(Calendar.MONTH, Calendar.JANUARY);
			expirationDate.set(Calendar.DAY_OF_MONTH, 31);
			expirationDate.set(Calendar.YEAR, Integer.parseInt(year) + 1);
		} catch (Exception e) {
			return null;
		}

		return expirationDate.getTime();
	}

	public static String getThreeYearsAheadDate(String fromDate) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		Date tempDate = showFormat.parse(fromDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(tempDate);
		cal.add(Calendar.YEAR, 3);

		return showFormat.format(cal.getTime());
	}

	public static String getTodaysDateTime() {
		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
		SimpleDateFormat format = new SimpleDateFormat("M/d/yy h:mm a z");
		format.setTimeZone(tz);
		String temp = format.format(cal.getTime());

		return temp;
	}

	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	public static Date getNextDayMidnight(Date date) {
		if (date == null) {
			// There is no good value to return here other than null
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, 1);

		return cal.getTime();
	}

	public static Date getLatestDate(Date d1, Date d2) {
		if (d1 == null) {
			return d2;
		}

		if (d2 == null) {
			return d1;
		}

		if (d1.after(d2)) {
			return d1;
		}

		return d2;
	}

	/**
	 * This allows setting the new year rollover before jan 1. To set the
	 * rollover date, update currentYearStart in web.xml.
	 */
	public static int getCurrentYear(ServletContext context) throws Exception {
		String strCurrentYearStart = context.getInitParameter("currentYearStart");

		return getCurrentYear(strCurrentYearStart);
	}

	public static int getCurrentYear(String strCurrentYearStart) throws Exception {
		String curYearStart = strCurrentYearStart + "/" + String.valueOf(getCurrentYear());
		if (isAfterToday(curYearStart)) {
			return getCurrentYear();
		}

		return getCurrentYear() + 1;
	}

	/**
	 * @return the month (1-12) of today's date
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();

		return cal.get(Calendar.MONTH);
	}

	/**
	 * @return the current hour (0-23) right now
	 */
	public static int getCurrentHour() {
		Calendar cal = Calendar.getInstance();

		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Get the end of time date 1/1/4000 This is used for expiration dates
	 */
	public static Date getEndOfTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(4000, Calendar.JANUARY, 1, 23, 59, 59);

		return cal.getTime();
	}

	/**
	 * Get the start of time date 1/1/2000 This is used for query ranges
	 */
	public static Date getStartOfPicsTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);

		return cal.getTime();
	}

	public static String getMonthName(int monthInt) {
		if (monthInt < 12) {
			return MonthNames[monthInt];
		}

		return Strings.EMPTY_STRING;
	}

	static public ArrayList<Calendar> getNextMonths(int numMonths) {
		ArrayList<Calendar> monthList = new ArrayList<Calendar>();
		Calendar seed = Calendar.getInstance();

		for (int x = 0; x < numMonths; x += 1) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(seed.getTime());
			monthList.add(cal);
			seed.add(Calendar.MONTH, 1);
		}

		return monthList;
	}

	public static boolean isLessThanOneYearAgo(String dateString) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		Date yearAgo = cal.getTime();

		Date date = parseDate(dateString);

		return yearAgo.before(date);
	}

	public static boolean isLessThanTheeYearAgo(Date testDate) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -3);
		Date threeYearsAgo = cal.getTime();

		return threeYearsAgo.before(testDate);
	}

	public static boolean isMoreThanXMonthsAgo(Date testDate, int monthsAgo) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -monthsAgo);
		Date xMonthsAgo = cal.getTime();

		return xMonthsAgo.after(testDate);
	}

	public static boolean isAfterToday(String dateString) throws Exception {
		if (Strings.EMPTY_STRING.equals(dateString)) {
			dateString = NULL_DATE;
		}

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();

		Date date = parseDate(dateString);

		return today.before(date);
	}

	public static boolean isBeforeAWeek(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -5);

		return calendar.getTime().before(startDate);
	}

	public static boolean isNullDate(Date dt) {
		if (dt == null || Strings.EMPTY_STRING.equals(dt.toString())) {
			return true;
		}

		return false;
	}

	// This will return a map of business hours
	public static Map<Integer, String> getBusinessTimes() {
		return Collections.unmodifiableMap(times);
	}

	public static int getIndexForTime(String time) {
		for (Integer i : times.keySet()) {
			if (times.get(i).equals(time)) {
				return i;
			}
		}

		return 12;
	}

	public static boolean isToday(Date firstDate) {
		return (daysBetween(firstDate, new Date()) == 0);
	}

	public static boolean isSameDate(Date firstDate, Date secondDate) {
		return (daysBetween(firstDate, secondDate) == 0);
	}

	/**
	 * SecondDate - FirstDate <br/>
	 * Example: 1/1/08 and 2/1/08 = 31 <br/>
	 * 1/1/08 and 12/31/07 = -1
	 * @return days between the two dates
	 */
	public static int daysBetween(Date startDate, Date endDate) {
        Days d = Days.daysBetween(new DateTime(startDate), new DateTime(endDate));
        return d.getDays();
	}

	/**
	 * Calculate the number of days until the date Positive numbers are in the
	 * future. Negative numbers are in the past.
	 */
	public static int getDateDifference(Date firstDate) {
		Calendar cal = Calendar.getInstance();

		return DateBean.daysBetween(cal.getTime(), firstDate);
	}

	public static Date parseDate(String dateString) {
		if (Strings.isEmpty(dateString)) {
			return null;
		}

		// do not be tempted to move this to a static variable -
		// SimpleDateFormat is not thread-safe
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(false);

		Date date = null;
		for (String pattern : datePatterns) {
			try {
				dateFormat.applyPattern(pattern);
				date = dateFormat.parse(dateString);
				break;
			} catch (ParseException e) {
			}
		}

		if (date == null) {
			logger.warn("parseDate (FAILED): {}", dateString);
		}

		return date;
	}

	public static Date parseDateTime(String dateString) {
		// do not be tempted to move this to a static variable -
		// SimpleDateFormat is not thread-safe
		SimpleDateFormat df = new SimpleDateFormat();
		df.setLenient(false);

		List<String> timePatterns = new ArrayList<String>();
		timePatterns.add("hh:mm a z");
		timePatterns.add("hh:mm a");
		timePatterns.add("HH:mm a");

		Date d = null;
		outerLoop: for (String pattern : datePatterns) {
			for (String timePattern : timePatterns) {
				try {
					df.applyPattern(pattern + " " + timePattern);
					d = df.parse(dateString);
					break outerLoop;
				} catch (ParseException e) {
					logger.debug("Using pattern: {}", pattern, e.getCause());
				}
			}
		}
		if (d == null) {
			logger.error("parseDate (FAILED): {}", dateString);
		}
		return d;
	}

	public static Date addMonths(Date startDate, int months) {
		if (startDate == null || months == 0) {
			return null;
		}

		Calendar cal = initializeCalendarWithOffset(startDate, Calendar.MONTH, months);

		return cal.getTime();
	}

	public static Date addDays(Date startDate, int days) {
		if (startDate == null || days == 0) {
			return null;
		}

		Calendar cal = initializeCalendarWithOffset(startDate, Calendar.DATE, days);

		return cal.getTime();
	}

	public static Date addField(Date startDate, int field, int amount) {
		if (startDate == null || amount == 0) {
			return null;
		}

		Calendar cal = initializeCalendarWithOffset(startDate, field, amount);

		return cal.getTime();
	}

	/**
	 * Get the first date of a month before
	 */
	public static Date getFirstofMonth(Date startDate, int months) {
		if (startDate == null) {
			return null;
		}

		Calendar calendar = initializeCalendarWithOffset(startDate, Calendar.MONTH, months);
		calendar.set(Calendar.DATE, 1);

		return calendar.getTime();
	}

	/**
	 * this will not roll to the next march. it will increment by a year and
	 * then go to march. Jan 1 2009 will return March 1 2010
	 */

	public static Date getMarchOfNextYear(Date startDate) {
		if (startDate == null) {
			return null;
		}

		Calendar cal = initializeCalendarWithOffset(startDate, Calendar.YEAR, 1);
		cal = setDefaultsForMonth(cal, Calendar.MARCH, 15);

		return cal.getTime();
	}

	public static Date getMarchOfThatYear(Date startDate) {
		if (startDate == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal = setDefaultsForMonth(cal, Calendar.MARCH, 15);

		return cal.getTime();
	}

	public static Date getFirstOfNextYear(Date startDate) {
		if (startDate == null) {
			return null;
		}

		Calendar calendar = initializeCalendarWithOffset(startDate, Calendar.YEAR, 1);
		calendar = setDefaultsForMonth(calendar, Calendar.JANUARY, 1);

		return calendar.getTime();
	}

	protected static Calendar initializeCalendarWithOffset(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);

		return calendar;
	}

	protected static Calendar setDefaultsForMonth(Calendar calendar, int month, int dayOfMonth) {
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static Date setToEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static Date setToStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Returns the integer year of the date argument or zero if the date is
	 * null.
	 */
	public static int getYearFromDate(Date date) {
		if (date == null) {
			return 0;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar.get(Calendar.YEAR);
	}

	public static String getBrainTreeDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.Braintree);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(d);
	}

	public static String getBrainTreeDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.Braintree);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(d);
	}

	public static void main(String[] args) {
		logger.info("{}", parseDateTime("3/10/08 6:56 AM PDT"));
	}

	public static String getCurrentMonthName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");

		return dateFormat.format(new Date());
	}

	public static Date convertTime(Date sourceDate, TimeZone sourceTimeZone) {
		return convertTime(sourceDate, sourceTimeZone, TimeZone.getDefault());
	}

	public static Date convertTime(Date sourceDate, TimeZone sourceTimeZone, TimeZone destinationTimeZone) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(sourceDate.getTime());

		Calendar c2 = Calendar.getInstance(sourceTimeZone);
		c2.setTimeInMillis(sourceDate.getTime());
		c2.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
		// this must be some kind of bug in java. you MUST look at the hour for
		// the timezone conversion to work
		c2.get(Calendar.HOUR_OF_DAY);
		c2.setTimeZone(destinationTimeZone);

		return c2.getTime();
	}

	public static String prettyDate(Date dateValue) {
		long msToUnlock = dateValue.getTime() - new Date().getTime();

		String ago = (msToUnlock < 0) ? " ago" : Strings.EMPTY_STRING;
		msToUnlock = Math.abs(msToUnlock);

		String period = "";
		int value = Math.round(msToUnlock / 1000);
		if (value <= 70) {
			period = "second";
		} else {
			value = Math.round(value / 60);
			if (value <= 70) {
				period = "minute";
			} else {
				value = Math.round(value / 60);
				if (value <= 24) {
					period = "hour";
				} else {
					value = Math.round(value / 24);
					if (value <= 31) {
						period = "day";
					} else {
						value = Math.round(value * 12 / 365);
						period = "month";
					}
				}
			}
		}

		if (value != 1) {
			period += "s";
		}

		return value + " " + period + ago;
	}

	/**
	 * Get the first date of a month before or the closest sunday before
	 */
	public static Date getFirstofMonthOrClosestSunday(Date startDate) {
		Calendar month = Calendar.getInstance();
		month.setTime(startDate);
		month.set(Calendar.DATE, 1);

		Calendar sunday = Calendar.getInstance();
		sunday.setTime(startDate);
		sunday.set(Calendar.DAY_OF_WEEK, 1);

		int closestmonth = DateBean.daysBetween(startDate, month.getTime());
		int closestsunday = DateBean.daysBetween(startDate, sunday.getTime());

		if (closestmonth > closestsunday) {
			return month.getTime();
		}

		return sunday.getTime();
	}

	public static int BusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
		int calendarDaysBetween = Days.daysBetween(startDate, endDate).getDays();
		int workDays = 0;

		if (calendarDaysBetween < 0) {
			return 0;
		}

		do {
			startDate = startDate.plusDays(1);
			if (startDate.getDayOfWeek() != DateTimeConstants.SATURDAY
					&& startDate.getDayOfWeek() != DateTimeConstants.SUNDAY) {
				workDays++;
			}
		} while (startDate.isBefore(endDate));

		return workDays;
	}
}
