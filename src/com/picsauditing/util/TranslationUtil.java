package com.picsauditing.util;

public class TranslationUtil {

	public static final String LONE_DOUBLE_QUOTE = "\"";
	public static final String PAIR_DOUBLE_QUOTE = "\"\"";
	
	public static final String DOUBLE_QUOTE_LEFT_ANGLE_BRACKET = "\"<";
	public static final String RIGHT_ANGLE_BRACKET_DOUBLE_QUOTE = ">\"";

	public static String scrubValue(String value) {
		if (value == null) {
			return null;
		}

		value = value.trim();

		if (!value.contains(PAIR_DOUBLE_QUOTE)) {
			return value;
		}

		value = value.replaceAll(PAIR_DOUBLE_QUOTE, LONE_DOUBLE_QUOTE);
		
		if (value.startsWith(DOUBLE_QUOTE_LEFT_ANGLE_BRACKET)) {
			value = value.replaceFirst(DOUBLE_QUOTE_LEFT_ANGLE_BRACKET, "<");
		}
		
		if (value.endsWith(RIGHT_ANGLE_BRACKET_DOUBLE_QUOTE)) {
			value = value.substring(0, value.length() - 1);
		}

		return value;
	}
}
