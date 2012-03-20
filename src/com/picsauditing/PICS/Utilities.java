package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.Date;

import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.SpringUtils;

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
		return escapeHTML(value,Integer.MAX_VALUE);
	}

	/**
	 * This variation of escapeHTML(text) accepts a second argument of
	 * maxLength. It truncates the text to the given length, before escaping it.
	 * This means that we can safely truncate the text without worrying about
	 * truncating in the middle of an escape sequence.
	 * 
	 * Additionally, if the text is truncated, then "..." is appended in place
	 * of the truncted text.
	 */
	public static String escapeHTML(String value,int maxLength) {
		int maxIndex = Math.min(maxLength,value.length());
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < maxIndex; i++) {
			char c = value.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '\'') {
				out.append("&#" + (int) c + ";");
			} else if (c == '\n') {
				out.append("<br/>");
			} else {
				out.append(c);
			}
		}
		if (maxLength < value.length()) {
			out.append("...");
		}
		return out.toString();
	}

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
	
	public static float getIndustryAverage(boolean lwcr, Naics naics){
		NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
		return naicsDAO.getIndustryAverage(lwcr, naics);
	}
	
	public static float getDartIndustryAverage(Naics naics) {
		NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
		return naicsDAO.getDartIndustryAverage(naics);
		}
}
