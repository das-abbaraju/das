package com.picsauditing.auditbuilder.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateBean {
	public static String NULL_DATE_DB = "0000-00-00";

	private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

	public static final int ADD_OPERATION = 1;

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

	public static String getWCBYear(Date date) {
		Calendar wcbYearStart = Calendar.getInstance();
		if (date != null) {
			wcbYearStart.setTime(date);
		}

		wcbYearStart.set(Calendar.MONTH, Calendar.NOVEMBER);
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
		return getEffectiveWCBYear(today());
	}

	private static int getEffectiveWCBYear(Date now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		if (calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DATE) <= 31) {
			return calendar.get(Calendar.YEAR) - 1;
		}

		return calendar.get(Calendar.YEAR);
	}

	public static boolean isGracePeriodForWCB() {
		return isGracePeriodForWCB(today());
	}

	private static boolean isGracePeriodForWCB(Date now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);

		int month = calendar.get(Calendar.MONTH);
		if (month >= Calendar.NOVEMBER && month <= Calendar.DECEMBER) {
			return true;
		}

		if (month == Calendar.JANUARY && calendar.get(Calendar.DATE) <= 31) {
			return true;
		}

		return false;
	}

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

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

	public static int daysBetween(Date startDate, Date endDate) {
        Days d = Days.daysBetween(new DateTime(startDate), new DateTime(endDate));
        return d.getDays();
	}

	public static int getDateDifference(Date firstDate) {
		Calendar cal = Calendar.getInstance();

		return DateBean.daysBetween(cal.getTime(), firstDate);
	}

	public static Date parseDate(String dateString) {
		if (Strings.isEmpty(dateString)) {
			return null;
		}

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

	public static Date addMonths(Date startDate, int months) {
		return adjustDate(startDate,months,Calendar.MONTH,ADD_OPERATION);
	}

	public static Date adjustDate(Date startDate, int qty, int typeOfAdjustment, int operation) {
		if (startDate == null || qty == 0) {
			return null;
		}

		Calendar cal = initializeCalendarWithOffset(startDate, typeOfAdjustment, qty*operation);

		return cal.getTime();
	}

	protected static Calendar initializeCalendarWithOffset(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);

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

	public static Date today() {
		return new Date();
	}
}