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
		if ("".equals(dateString) || NULL_DATE.equals(dateString))
			return NULL_DATE_DB;
		try {
			java.util.Date tempDate = showFormat.parse(dateString);
			return toDBFormat(tempDate);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String toDBFormat(Date fromDate) throws Exception {
		if (fromDate == null)
			return null;
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return DBFormat.format(fromDate);
		} catch (Exception e) {
			System.out.println("Invalid DB Date format in DateBean.toDBformat(): failed converting " + fromDate);
			return null;
		}
	}
	

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

	/**
	 * Get the end of time date 1/1/4000 This is used for expiration dates
	 * 
	 * @return
	 */
	public static Date getEndOfTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(4000, 1, 1);
		return cal.getTime();
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

	/**
	 * SecondDate - FirstDate <br/> Example: 1/1/08 and 2/1/08 = 31 <br/> 1/1/08
	 * and 12/31/07 = -1
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return days between the two dates
	 */
	public static int getDateDifference(Date firstDate, Date secondDate) {
		long msApart = secondDate.getTime() - firstDate.getTime();
		return (int) (msApart / (24 * 60 * 60 * 1000));
	}

	/**
	 * Calculate the number of days until the date Positive numbers are in the
	 * future. Negative numbers are in the past.
	 * 
	 * @param firstDate
	 * @return
	 */
	public static int getDateDifference(Date firstDate) {
		Calendar cal = Calendar.getInstance();
		return DateBean.getDateDifference(cal.getTime(), firstDate);
	}

	public static Date parseDate(String dateString) {
		//System.out.println("Attempting to parse " + dateString);
		SimpleDateFormat df = new SimpleDateFormat();
		df.setLenient(false);
		
		List<String> patterns = new ArrayList<String>();
		patterns.add("MM-dd-yy");
		patterns.add("MM/dd/yy");
		patterns.add("yyyy-MM-dd");
		patterns.add("yyyy/MM/dd");
		patterns.add("MM-dd-yyyy");
		patterns.add("MM/dd/yyyy");
		Date d = null;
		for(String pattern : patterns) {
			try {
				df.applyPattern(pattern);
				d = df.parse(dateString);
//				System.out.println("parseDate (SUCCESS): from " + dateString + " into " + DateBean.format(d, "yyyy-MM-dd"));
				break;
			} catch (ParseException e) {
				//System.out.println(e.getMessage() + " using pattern: " + pattern);
			}
		}
		if (d == null)
			System.out.println("parseDate (FAILED): " + dateString);
		return d;
	}
	
	public static Date parseDateTime(String dateString) {
		//System.out.println("Attempting to parse " + dateString);
		SimpleDateFormat df = new SimpleDateFormat();
		df.setLenient(false);
		
		List<String> datePatterns = new ArrayList<String>();
		datePatterns.add("MM-dd-yy");
		datePatterns.add("MM/dd/yy");
		datePatterns.add("yyyy-MM-dd");
		datePatterns.add("yyyy/MM/dd");
		datePatterns.add("MM-dd-yyyy");
		datePatterns.add("MM/dd/yyyy");

		
		List<String> timePatterns = new ArrayList<String>();
		timePatterns.add("hh:mm a z");
		timePatterns.add("HH:mm z");
		
		Date d = null;
outerLoop:		
		for(String pattern : datePatterns) {
			for( String timePattern : timePatterns ) {
				try {
					df.applyPattern(pattern + " " + timePattern);
					d = df.parse(dateString);
//					System.out.println("parseDate (SUCCESS): from " + dateString + " into " + DateBean.format(d, "yyyy-MM-dd"));
					break outerLoop;
				} catch (ParseException e) {
					//System.out.println(e.getMessage() + " using pattern: " + pattern);
				}
			}
		}
		if (d == null)
			System.out.println("parseDate (FAILED): " + dateString);
		return d;
	}
	
	public static Date addMonths(Date startDate, int months) {
		if (startDate == null || months == 0)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	
	public static Date addDays(Date startDate, int days) {
		if (startDate == null || days == 0)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}	

	/**
	 * Get the first date of a month before
	 * @param startDate
	 * @param months
	 * @return
	 */
	public static Date getFirstofMonth(Date startDate, int months) {
		if (startDate == null || months == 0)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, months);
		cal.set(Calendar.DATE, 1);
		return cal.getTime();
	}

	/**
	 * this will not roll to the next march.  it will increment by a year
	 * and then go to march.  Jan 1 2009 will return March 1 2010
	 */ 
	
	public static Date getMarchOfNextYear(Date startDate) {
		if (startDate == null)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.YEAR, 1);
		
		cal.set(Calendar.MONTH, Calendar.MARCH);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static String getBrainTreeDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
		sdf.setTimeZone( TimeZone.getTimeZone("GMT"));
		return sdf.format(d);
	}
	
	public static void main(String[] args) {
		System.out.println(parseDateTime("3/10/08 6:56 AM PDT"));
	}
	
	public static boolean isBeforeAWeek(Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -5);
		return calendar.getTime().before(startDate);
	}
	
	public static String getCurrentMonthName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
		return dateFormat.format(new Date());
	}
}
