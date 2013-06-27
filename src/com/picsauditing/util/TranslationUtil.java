package com.picsauditing.util;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Translatable;

public class TranslationUtil {

	public static final String LONE_DOUBLE_QUOTE = "\"";
	public static final String PAIR_DOUBLE_QUOTE = "\"\"";

	public static final String DOUBLE_QUOTE_LEFT_ANGLE_BRACKET = "\"<";
	public static final String RIGHT_ANGLE_BRACKET_DOUBLE_QUOTE = ">\"";

	private static final Logger logger = LoggerFactory.getLogger(TranslationUtil.class);

	public final static Comparator<Locale> LOCALE_DISPLAY_NAME_COMPARATOR = new Comparator<Locale>() {
		public int compare(Locale l1, Locale l2) {
			return l1.getDisplayName().compareTo(l2.getDisplayName());
		}
	};

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

	public static boolean isTranslation(Translatable translatable, String property, String translation) {
		String key = translatable.getI18nKey(property);
		if (key == null || translation == null) {
			return false;
		}

		return !(key.equals(translation));
	}

	public static Map<Locale, String> sortTranslationsByLocaleDisplayNames(Map<Locale, String> newTranslationMap) {
		Map<Locale, String> sortedTranslationMap = new TreeMap<Locale, String>(LOCALE_DISPLAY_NAME_COMPARATOR);
		sortedTranslationMap.putAll(newTranslationMap);
		return sortedTranslationMap;
	}

	public static Locale convertStringToLocale(String keyStr) {
		Locale locale = null;
		String[] lanCountry = keyStr.split("_");

		// e.g. en_GB
		if (lanCountry.length > 1) {
			locale = new Locale(lanCountry[0], lanCountry[1]);
		} else {
			locale = new Locale(keyStr);
		}
		return locale;
	}

	public static String getStrippedHref(String text) {
		if (text.contains("href") && text.contains("www.picsorganizer.com")) {
			text = text.replaceAll("(href\\s*=\\s*[\"']*)(https?:)?//www.picsorganizer.com/?", "$1");
		}

		return text;
	}

	public static String prepareKeyForCache(String key) {
		String keyUppercase = Strings.EMPTY_STRING;

		try {
			keyUppercase = key.toUpperCase();
		} catch (Exception e) {
			logger.warn("Null key passed to be cleaned before being put into I18nCache.");
		}

		return keyUppercase;
	}

	public static void validateTranslationKey(String key) {
		if (key == null) {
			throw new RuntimeException("i18n key cannot be NULL");
		}

		if (key.length() == 0) {
			throw new RuntimeException("i18n key cannot be empty");
		}

		if (key.contains("'") || key.contains("\"")) {
			throw new RuntimeException("i18n key cannot contain quotes");
		}

		if (key.contains(Strings.SINGLE_SPACE)) {
			throw new RuntimeException("i18n key cannot contain spaces");
		}
	}

}
