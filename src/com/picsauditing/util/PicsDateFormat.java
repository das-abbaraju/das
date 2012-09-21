package com.picsauditing.util;

/**
 * This class is prefaced with 'Pics' due to the naming collision with java.text.DateFormat.
 * 
 * The only non-intuitive aspect of date formats is months. 'M' or 'MM' will display as a
 * number, 'MMM' will display as an abbreviation of a month (e.g. Feb), and 'MMMM' or more
 * will display the full month name (e.g. November).
 */
public abstract class PicsDateFormat {

	/** You should use this format for 99% of use cases. */
	public static final String Iso = "yyyy-MM-dd";

	public static final String IsoWeekday = "EEE, yyyy-MM-dd";
	public static final String IsoLongMonth = "yyyy-MMM-dd";
	public static final String MonthAndDay = "MMM dd";
	public static final String MonthAndYear = "MMM yyyy";
	public static final String TwoDigitYear = "yy";
	public static final String Datetime = "yyyy-MM-dd HH:mm z";
	public static final String Datetime12Hour = "yyyy-MM-dd hh:mm a z";
	public static final String Time = "HH:mm z";
	public static final String Time12Hour = "hh:mm a z";
	public static final String ScheduleAudit = "yyyyMMddHHmm";

	// These exist for backwards compatability for input forms and the parsing thereof
	@Deprecated
	public static final String American = "MM/dd/yyyy";
	@Deprecated
	public static final String AmericanShort = "M/d/yyyy";

	private PicsDateFormat() { }
}
