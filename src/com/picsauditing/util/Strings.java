package com.picsauditing.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.BaseTable;

public class Strings {
	
	public static final String EMPTY_STRING = ""; 

	private static final Logger logger = LoggerFactory.getLogger(Strings.class);
	
	public static boolean isEmpty(String value) {
		if (value == null)
			return true;
		value = value.trim();
		return value.length() == 0;
	}

	/**
	 * Are two strings equal to each other. One or both can be null. If both are
	 * null, then return true.
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean isEqualNullSafe(String value1, String value2) {
		if (value1 != null)
			return value1.equals(value2);

		if (value2 != null)
			return value2.equals(value1);
		return true;

	}

	public static String[] convertListToArray(List<String> list) {
		String[] array = new String[list.size()];
		int i = 0;
		for (String item : list) {
			array[i] = item;
			i++;
		}
		return array;
	}

	public static String insertSpaces(String value) {
		if (value == null)
			return null;
		StringBuilder newValue = new StringBuilder();

		for (int i = 0; i < value.length(); i++) {
			newValue.append(value.charAt(i));
			newValue.append(" ");
		}
		return newValue.toString().trim();
	}

	// TODO Put the TODOs about method names at the top of the method
	// TODO rename this method to escapeSingleQuote
	public static String escapeQuotes(String value) {
		if (value == null)
			return "";
		String singleQuote = "\'";
		String backSlash = "\\";

		return value.replace(singleQuote, backSlash + singleQuote);
	}

	public static String implode(int[] array) {
		return implode(array, ",");
	}

	public static String implode(int[] array, String delimiter) {
		if (array == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (int o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append(o);
		}
		return buffer.toString();
	}

	public static String implodeForDB(String[] array, String delimiter) {
		if (array == null)
			return EMPTY_STRING;
		StringBuffer buffer = new StringBuffer();
		for (String o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append("'");
			buffer.append(escapeQuotes(o));
			buffer.append("'");
		}
		return buffer.toString();
	}

	public static String implodeForDB(Enum[] array, String delimiter) {
		if (array == null)
			return EMPTY_STRING;
		StringBuffer buffer = new StringBuffer();
		for (Enum o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append("'");
			buffer.append(o);
			buffer.append("'");
		}
		return buffer.toString();
	}

	public static String implodeForDB(Collection<? extends Object> collection, String delimiter) {
		if (collection == null)
			return "";
		
		StringBuffer buffer = new StringBuffer();
		for (Object o : collection) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append("'");
			buffer.append(escapeQuotes(String.valueOf(o)));
			buffer.append("'");
		}
		return buffer.toString();
	}

	public static String implode(Collection<? extends Object> collection) {
		return implode(collection, ",");
	}

	public static String implodeIDs(Collection<? extends BaseTable> collection) {
		if (collection == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (BaseTable o : collection) {
			if (buffer.length() > 0)
				buffer.append(",");
			buffer.append(o.getId());
		}
		return buffer.toString();
	}

	public static String implode(Collection<? extends Object> collection, String delimiter) {
		if (collection == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (Object o : collection) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append(o);
		}
		return buffer.toString();
	}

	public static String implode(List<String> collection, String delimiter) {
		if (collection == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (Object o : collection) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append(o);
		}
		return buffer.toString();
	}

	public static String hash(String seed) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
		digest.update(seed.getBytes());
		byte[] hashed = digest.digest();
		BigInteger number = new BigInteger(1, hashed);
		// String value = Base64.encodeBytes(hashed);
		return number.toString(16);
	}

	public static String md5(String seed) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(seed.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			return number.toString(16);
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
	}

	/**
	 * @param seed
	 * @return
	 */
	public static String hashUrlSafe(String seed) {
		String value = Strings.hash(seed);
		value = value.replace("+", "_");
		return value;
	}

	/**
	 * Take an arbitrary string and return an integer if it could be an
	 * accountID. Other wise return a 0 Examples: <br />
	 * 11883 returns 11883<br />
	 * 11883.4 returns 11883<br />
	 * Foobar returns 0
	 * 
	 * @param query
	 * @return
	 */
	public static int extractAccountID(String query) {
		String expression = "^([1-9][0-9]*)(?:\\.([0-9]))?\\z";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(query);
		while (matcher.find()) {
			String idString = matcher.group(1);
			try {
				return Integer.parseInt(idString);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	public static float toFloat(String value) {
		float newValue = 0;
		try {
			newValue = Float.parseFloat(value);
		} catch (NumberFormatException e) {
		}
		return newValue;
	}

	public static String stripNonStandardCharacters(String input) {
		input = input.replace('ì', '"');
		input = input.replace('î', '"');
		input = input.replace("`", "'");
		return input;
	}

	public static String stripPhoneNumber(String phone) {
		if (phone == null)
			return null;
		Set<Character> valid = new HashSet<Character>();
		valid.add('2');
		valid.add('3');
		valid.add('4');
		valid.add('5');
		valid.add('6');
		valid.add('7');
		valid.add('8');
		valid.add('9');
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < phone.length(); i++) {
			char nextChar = phone.charAt(i);
			if (buffer.toString().length() < 10 && valid.contains(nextChar)) {
				buffer.append(nextChar);
				valid.add('1');
				valid.add('0');
			}
		}

		return buffer.toString();
	}

	public static Map<String, String> mapParams(String params) {
		Map<String, String> paramMap = new HashMap<String, String>();
		String expression = "(\\w*)=([^&]*)?";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(params);
		while (matcher.find()) {
			paramMap.put(matcher.group(1), matcher.group(2));
		}
		return paramMap;

	}

	public static String indexName(String name) {
		if (name == null)
			return null;
		name = name.toUpperCase();

		/*
		 * Old code for reference
		 * 
		 * String expression = "[A-Z0-9]+"; Pattern pattern =
		 * Pattern.compile(expression, Pattern.CANON_EQ); Matcher matcher =
		 * pattern.matcher(name);
		 * 
		 * StringBuffer buf = new StringBuffer(); boolean found = false; while
		 * ((found = matcher.find())) { System.out.println(matcher.group());
		 * buf.append(matcher.group()); }
		 * 
		 * // return name.toUpperCase().replaceAll("[^A-Z0-9]",""); return
		 * buf.toString();
		 */

		// Remove all quotes
		name = name.replaceAll("[\\'\\\"]", "");
		// Take out "the" at the beginning of the sentences, llc & inc (in
		// multiple variations) at the end of sentences
		name = name.replaceAll("^THE ", "");
		name = name.replaceAll(" L ?L ?C$", "");
		name = name.replaceAll(" I ?N ?C$", "");
		// Remove all non-alphanumeric characters
		name = name.replaceAll("\\W", "");
		name = name.replaceAll("_", "");
		// Change multiple spaces into nothing
		name = name.replaceAll(" +", "");
		name = name.trim();


		return name;
	}

	public static String htmlStrip(String input) {
		if (Strings.isEmpty(input))
			return null;

		return input.replaceAll("<", "").replaceAll(">", "");
	}
	@Deprecated
	public static Set<String> findUniqueEmailAddresses(String emailAddresses) {
		Set<String> validEmail = new HashSet<String>();

		if (!Strings.isEmpty(emailAddresses)) {
			String[] list1 = emailAddresses.split(",");
			for (String email : list1) {
				if (Strings.isValidEmail(email))
					validEmail.add(email);
			}
		}
		return validEmail;
	}

	public static String trim(String input, int maxlength) {
		if (isEmpty(input))
			return "";
		if (input.length() <= maxlength)
			return input;
		return input.substring(0, maxlength - 3) + "...";
	}
	@Deprecated
	public static boolean isValidEmail(String email) {
		boolean result = false;
		if (Strings.isEmpty(email) || email.trim().contains(" "))
			return false;
		int index = email.indexOf("@");
		if (index > 0) {
			int pindex = email.indexOf(".", index);
			if ((pindex > index + 1) && (email.length() > pindex + 1))
				result = true;
		}// if
		return result;
	}// isValidEmail

	public static String formatShort(float value) {
		if (value < 0)
			return "-" + formatShort(value * -1);
		if (value < 0.001) {
			return "0";
		}
		if (value < 1) {
			return trimTrailingZeros(String.format("%.3f", value));
		}
		if (value < 10) {
			return trimTrailingZeros(String.format("%.2f", value));
		}
		if (value < 100) {
			return trimTrailingZeros(String.format("%.1f", value));
		}
		if (value < 1000) {
			return trimTrailingZeros(String.format("%.0f", value));
		}
		if (value < 1000000) {
			return formatShort(value / 1000) + "K";
		}
		if (value < 1000000000) {
			return formatShort(value / 1000000) + "M";
		}
		return formatShort(value / 1000000000) + "B";
	}

	public static String formatDecimalComma(String decimalNumber) {
		try {
			Double value = Double.parseDouble(decimalNumber);
			DecimalFormat df;

			if (value >= 100)
				df = new DecimalFormat("#,##0");
			else
				df = new DecimalFormat("0.00");

			if (value != 0)
				decimalNumber = df.format(value);
			else
				return "0";
		} catch (Exception e) {
			logger.error("Could not parse number: {}", decimalNumber);
		}

		return decimalNumber;
	}

	public static String formatNumber(String number) {
		// Returns only digits and decimal points
		number = number.replaceAll("[^\\d\\.]", "");
		// Make sure that number doesn't have more than one decimal point
		try {
			String n1 = number.substring(0, number.indexOf(".") + 1);
			String n2 = number.substring(number.indexOf(".") + 1, number.length());
			n2 = n2.replace(".", "");
			number = n1 + n2;
		} catch (Exception e) {
			logger.error("Error parsing: {}", number);
		}

		return number;
	}

	public static String trimTrailingZeros(String number) {
		if (!number.contains(".")) {
			return number;
		}

		number = number.replaceAll("0*$", "");
		if (number.charAt(number.length() - 1) == '.') {
			number = number.substring(0, number.length() - 1);
		}
		return number;
	}

	public static String maskSSN(String ssn) {
		if (Strings.isEmpty(ssn))
			return null;
		ssn.replaceFirst("^(\\d{3})(\\d{2})(\\d{4})$", "XXX-XX-$3");
		return "XXX-XX-" + ssn.substring(ssn.length() - 4);
	}

	/**
	 * Is countries contained in the expression
	 * 
	 * @param expression
	 *            like !|CA|FR|
	 * @param countries
	 *            like US
	 * @return
	 */
	public static boolean isInCountries(String expression, Set<String> countries) {
		if (countries == null || countries.size() == 0)
			return true;
		if (isEmpty(expression))
			return true;

		if (expression.substring(0, 1).equals("!")) {
			String[] notInCountries = expression.substring(1).split("\\|");
			for (String country : notInCountries) {
				if (countries.contains(country))
					countries.remove(country);
			}
			return countries.size() > 0;
		} else {
			for (String country : countries) {
				if (expression.contains("|" + country + "|"))
					return true;
			}
			return false;
		}
	}

	public static String getCountry(String expression) {
		if (isEmpty(expression))
			return null;

		return expression.replace("|", "");
	}

	public static Locale parseLocale(String locale) {
		Locale test = null;
		String[] loc = locale.split("[_-]");
		try {
			test = new Locale(loc[0], loc[1], loc[2]);
		} catch (Exception no_variant) {
			try {
				test = new Locale(loc[0], loc[1]);
			} catch (Exception no_country) {
				try {
					test = new Locale(loc[0]);
				} catch (Exception bad_input) {
				}
			}
		}
		return test;
	}

	/**
	 * For computing the number of character differences between two strings
	 * Levenshtein Distance If needed, can be optimized
	 * 
	 * @param m
	 * @param n
	 * @return
	 */
	public static int editDistance(String m, String n) {
		// d is a table with m+1 rows and n+1 columns
		int[][] d = new int[m.length() + 1][n.length() + 1];

		for (int i = 0; i <= m.length(); i++)
			d[i][0] = i; // deletion
		for (int j = 0; j <= n.length(); j++)
			d[0][j] = j; // insertion

		for (int j = 1; j <= n.length(); j++) {
			for (int i = 1; i <= m.length(); i++) {
				if (m.charAt(i - 1) == n.charAt(j - 1))
					d[i][j] = d[i - 1][j - 1];
				else
					d[i][j] = Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + 1));
			}
		}

		return d[m.length()][n.length()];
	}

	public static boolean isSimilarTo(String m, String n, int characterDifferenceThreshold) {
		return (editDistance(m, n) <= characterDifferenceThreshold) ? true : false;
	}

	public static boolean isSimilarTo(String m, String n) {
		return (editDistance(m.toLowerCase(), n.toLowerCase()) <= Math.sqrt(Math.min(m.length(), n.length())) - .25) ? true
				: false;
	}

	public static String formatDateLong(Date d) {
		I18nCache cache = I18nCache.getInstance();
		Locale locale = TranslationActionSupport.getLocaleStatic();
		String format = cache.getText("date.long", locale);
		return new DateTool().format(format, d);
	}

	public static String formatDateShort(Date d) {
		I18nCache cache = I18nCache.getInstance();
		Locale locale = TranslationActionSupport.getLocaleStatic();
		String format = cache.getText("date.short", locale);
		return new DateTool().format(format, d);
	}

	public static String capitalize(String uncapitalized) {
		if (!isEmpty(uncapitalized)) {
			return uncapitalized.substring(0, 1).toUpperCase() + uncapitalized.substring(1, uncapitalized.length());
		}

		return null;
	}
	
	public static String nullToBlank(String value) {
		if (value == null) {
			return "";
		}
		
		return value;
	}
}
