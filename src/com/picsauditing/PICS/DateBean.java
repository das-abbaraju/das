package com.picsauditing.PICS;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;

public class DateBean {
	public static String NULL_DATE = "0/0/00";
	public static String NULL_DATE_DB = "0000-00-00";
	public static SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
	public static SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<Integer> nextMonths;

	public static String PQF_EXPIRED_CUTOFF = "2008-01-01";
	public static String OLD_OFFICE_CUTOFF = "2006-08-27";
	public static String MonthNames[] = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };

	protected static Map<Integer, String> times = new TreeMap<Integer, String>();

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

	public static String toDBFormat(String month, String day, String year) throws Exception {
		return toDBFormat(year + "-" + month + "-" + day);
	}

	public static String toDBFormat(String dateString) throws Exception {
		if (dateString == null)
			return null;
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
		String temp = "";
		if ("".equals(dateString) || NULL_DATE.equals(dateString))
			return NULL_DATE_DB;
		try {
			java.util.Date tempDate = showFormat.parse(dateString);
			temp = DBFormat.format(tempDate);
		} catch (Exception e) {
			System.out.println("Invalid DB Date format in DateBean.toDBformat(): failed converting " + dateString
					+ " to " + temp);
			temp = NULL_DATE_DB;
		}// catch
		// System.out.println("Valid DB Date format in DateBean.toDBformat():
		// "+dateString+" to "+temp);
		return temp;
	}// toDBFormat

	public static String toShowFormat(Object date) throws Exception {
		if (date == null)
			return "";
		if (date instanceof Date)
			return toShowFormat(date.toString());
		if (date instanceof String) {
			return toShowFormat(date.toString());
		}
		return "";
	}

	public static String toShowFormat(String dateString) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (null == dateString || "0000-00-00".equals(dateString) || "".equals(dateString))
			return "";
		String temp = "";
		try {
			java.util.Date tempDate = DBFormat.parse(dateString);
			temp = showFormat.format(tempDate);
		} catch (Exception e) {
			System.out.println("Invalid DB Date format in DateBean.toShowFormat(): failed converting " + dateString
					+ " to " + temp);
			temp = "";
		}// catch
		// System.out.println("Valid DB Date format in DateBean.toShowFormat():
		// "+dateString+" to "+temp);
		return temp;
	}// toDBFormat

	public static String format(Date date, String format) {
		if (format == null || format.equals(""))
			format = "M/d/yy";
		if (date == null)
			return "";

		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static String getTodaysDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("M/d/yy");
		String temp = format.format(cal.getTime());
		// System.out.println("Todays date: "+temp);
		return temp;
	}// getTodaysDate

	public static String getThreeYearsAheadDate(String fromDate) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		java.util.Date tempDate = showFormat.parse(fromDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(tempDate);
		cal.add(Calendar.YEAR, 3);
		return showFormat.format(cal.getTime());
	}// getThreeYearsAheadDate

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
	}// getCurrentYear

	/**
	 * This allows setting the new year rollover before jan 1. To set the
	 * rollover date, update currentYearStart in web.xml.
	 * 
	 * @param strCurrentYearStart
	 * @return
	 * @throws Exception
	 * 
	 */
	public static int getCurrentYear(ServletContext context) throws Exception {
		String strCurrentYearStart = context.getInitParameter("currentYearStart");
		return getCurrentYear(strCurrentYearStart);
	}// getCur

	public static int getCurrentYear(String strCurrentYearStart) throws Exception {
		String curYearStart = strCurrentYearStart + "/" + String.valueOf(getCurrentYear());
		if (isAfterToday(curYearStart))
			return getCurrentYear();
		else
			return getCurrentYear() + 1;

	}// getCur

	/**
	 * This allows old data to remain valid during the grace period of the new
	 * year. To set the grace period, update currentYearGrace in web.xml.
	 * 
	 * @param strCurrentYearGrace
	 * @return
	 * @throws Exception
	 */
	public static int getCurrentYearGrace(ServletContext context) throws Exception {
		String strCurrentYearGrace = context.getInitParameter("currentYearGrace");
		if (isDuringGrace(strCurrentYearGrace))
			return getCurrentYear() - 1;
		else
			return getCurrentYear();

	}// getCur

	public static boolean isDuringGrace(String strCurrentYearGrace) throws Exception {
		String curYearGrace = strCurrentYearGrace + "/" + String.valueOf(getCurrentYear());
		return isAfterToday(curYearGrace);
	}// getCur

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

	public static String getMonthName(int monthInt) {
		if (monthInt < 12)
			return MonthNames[monthInt];
		else
			return "";
	}// getMonthName

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
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		java.util.Date yearAgo = cal.getTime();
		java.util.Date testDate = showFormat.parse(dateString);
		return yearAgo.before(testDate);
	}// isLessThanOneYearAgo

	public static boolean isAuditExpired(String auditDate) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		if ("".equals(auditDate))
			auditDate = NULL_DATE;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -3);
		java.util.Date threeYearsAgo = cal.getTime();
		java.util.Date testAuditDate = showFormat.parse(auditDate);
		return testAuditDate.before(threeYearsAgo);
	}// isAuditExpired

	// public static boolean isAuditClosed(String auditClosedDate) throws
	// Exception {
	// if ("".equals(auditClosedDate) || NULL_DATE.equals(auditClosedDate))
	// return false;
	// Calendar cal = Calendar.getInstance();
	// java.util.Date today = cal.getTime();
	// java.util.Date testDate = showFormat.parse(auditClosedDate);
	// return testDate.before(today);
	// }//isAuditClosed

	public static boolean isPrequalExpired(String testDateString) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
		if ("".equals(testDateString))
			testDateString = NULL_DATE;
		java.util.Date pqfCutOffDate = DBFormat.parse(PQF_EXPIRED_CUTOFF);
		java.util.Date testPqfSubmittedDate = showFormat.parse(testDateString);
		return testPqfSubmittedDate.before(pqfCutOffDate);
	}// isPrequalExpired

	public static boolean isAfterToday(String testDateString) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		if ("".equals(testDateString))
			testDateString = NULL_DATE;
		Calendar cal = Calendar.getInstance();
		java.util.Date today = cal.getTime();
		java.util.Date testDate = showFormat.parse(testDateString);
		return today.before(testDate);
	}// isAfterToday

	public static boolean isBeforeToday(String testDateString) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		if ("".equals(testDateString))
			testDateString = NULL_DATE;
		Calendar cal = Calendar.getInstance();
		java.util.Date today = cal.getTime();
		java.util.Date testDate = showFormat.parse(testDateString);
		return today.after(testDate);
	}// isAfterToday

	public static boolean isFirstBeforeSecond(String dateString1, String dateString2) throws Exception {
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		java.util.Date date1 = showFormat.parse(dateString1);
		java.util.Date date2 = showFormat.parse(dateString2);
		return date1.before(date2);
	}// isFirstBeforeSecond

	public static boolean isNullDate(Date dt) {
		if (dt == null || dt.toString().equals(""))
			return true;
		else
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

	public static int getDateDifference(Date firstDate, Date secondDate) {
		long msApart = secondDate.getTime() - firstDate.getTime();
		return (int)(msApart / (24 * 60 * 60 * 1000));
	}
	
	/**
	 * Calculate the number of days until the date
	 * Positive numbers are in the future. Negative numbers are in the past.
	 * @param firstDate
	 * @return
	 */
	public static int getDateDifference(Date firstDate) {
		Calendar cal = Calendar.getInstance();
		return DateBean.getDateDifference(cal.getTime(), firstDate);
	}
}
