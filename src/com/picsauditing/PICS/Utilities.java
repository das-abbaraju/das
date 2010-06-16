package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.Date;

/**
 * A set of generic Utilities. We should consider moving this into the Strings
 * class
 */
public class Utilities {

	public static boolean isEmptyArray(Object[] array) {
		if (array == null || array.length == 0)
			return true;

		for (Object object : array)
			if (object == null)
				return true;

		return false;
	}

	public static String escapeHTML(String value) {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '\'') {
				out.append("&#" + (int) c + ";");
			} else if (c == '\n') {
				out.append("<br/>");
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}// escapeHTML

	public static String escapeNewLines(String value) {
		if (value == null)
			return "";
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if ('\n' == value.charAt(i))
				temp.append("<br>");
			else
				temp.append(value.charAt(i));
		}
		return temp.toString();
	}

	/**
	 * Replaces single quotes in a string with two single quotes. This formats
	 * it properly for use in a SQL statement.
	 * 
	 * @param value
	 *            the string to format.
	 * @return the formatted string.
	 */
	public static String escapeQuotes(String value) {
		if (value == null)
			return "";
		StringBuffer strval = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			// Found a single quote; replace it with 2 for the SQL statement
			if ((ch == 146) || (ch == '\'') || (ch == '%'))
				strval.append("''");
			// if find double quote, change to single quote since html input
			// can't handle double quotes BJ 3-7-05
			else if (ch == 92)
				strval.append("\\\\");
			else if (ch == '"')
				strval.append("''");
			else
				// Just append the char to the strval for the complete string
				strval.append(ch);
		}
		return strval.toString();
	}

	public static String getBGColor(int count) {
		if ((count % 2) == 0)
			return " bgcolor=\"#FFFFFF\"";
		else
			return "";
	}

	public static float getAverageEMR(String year1, String year2, String year3, String year4) {
		Float rateFloat = 0.0f;
		int count = 0;
		if (convertToFloat(year1) > 0) {
			rateFloat += convertToFloat(year1);
			count++;
		}
		if (convertToFloat(year2) > 0) {
			rateFloat += convertToFloat(year2);
			count++;
		}
		if (convertToFloat(year3) > 0) {
			rateFloat += convertToFloat(year3);
			count++;
		}
		if (count < 3 && convertToFloat(year4) > 0) {
			rateFloat += convertToFloat(year4);
			count++;
		}

		Float avgRateFloat = rateFloat / count;
		return (float) Math.round(1000 * avgRateFloat) / 1000;
	}

	public static float convertToFloat(String year1) {
		if (year1 == null)
			return 0.0f;
		return Float.valueOf(year1).floatValue();
	}

	public static Date getYesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
}
