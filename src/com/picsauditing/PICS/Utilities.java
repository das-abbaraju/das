package com.picsauditing.PICS;

/**
 * A set of generic Utilities. We should consider moving this into the Strings
 * class
 */
public class Utilities {

	public static String escapeHTML(String value) {
		if (value == null)
			return "";
		StringBuffer strval = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			switch (ch) {
			case '\'':
				strval.append("\\'");
				break;
			case '"':
				strval.append("\"");
				break;
			case '&':
				strval.append("&");
				break;
			case '%':
				strval.append(" ");
				break;
			case '<':
				strval.append("<");
				break;
			case '>':
				strval.append(">");
				break;
			case '\n':
				strval.append("<br>");
				break;
			default:
				if (ch > 126)
					strval.append("&#" + String.valueOf(ch) + ";");
				else
					strval.append(ch);
				break;
			}// switch
		}// for
		// BJ 2-21-05 java can not reccognize ms apostrpohes, so must work
		// backwards
		// loop through to find where ms apostrpohes were converted to question
		// marks, change to standard apostrophes
		// int substart = strval.lastIndexOf("&#?;");
		// strval.replace(substart,substart+3,"'");

		return strval.toString();
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
}
