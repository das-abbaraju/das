package com.picsauditing.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.picsauditing.security.EncodedMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strings {

	public static final String EMPTY_STRING = "";
	public static final String SINGLE_SPACE = " ";
	public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private static final int NO_STRING_ESCAPE_STRATEGY = 0;
	private static final int STRING_ESCAPE_STRATEGY = 1;
	private static final int OBJECT_TO_STRING_ESCAPE_STRATEGY = 2;

	private static final Logger logger = LoggerFactory.getLogger(Strings.class);

	/** Use StringUtils.isEmpty() instead */
	public static boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}

		value = value.trim();
		return value.length() == 0;
	}

	/** Use StringUtils.isNotEmpty() instead */
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	/** Use StringUtils.equals() going forward */
	@Deprecated
	public static boolean isEqualNullSafe(String value1, String value2) {
		return StringUtils.equals(value1, value2);
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
		if (isEmpty(value))
			return EMPTY_STRING;

		String singleQuote = "\'";

		return value.replace(singleQuote, singleQuote + singleQuote);
	}

    public static String escapeSlashes(String value) {
        if (isEmpty(value))
            return EMPTY_STRING;

        String singleSlash = "\\";

        return value.replace(singleSlash, singleSlash + singleSlash);
    }

    public static String escapeQuotesAndSlashes(String value) {
        return escapeSlashes(escapeQuotes(value));
    }

    public static String implode(int[] array) {
		return implode(array, ",");
	}

	public static String implode(int[] array, String delimiter) {
		if (array == null || array.length == 0)
			return EMPTY_STRING;

		StringBuffer buffer = new StringBuffer();
		for (int o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);

			buffer.append(o);
		}

		return buffer.toString();
	}

	public static String implodeForDB(String[] array, String delimiter) {
		return genericArrayImplode(array, delimiter, STRING_ESCAPE_STRATEGY);
	}

	public static <E extends Enum<E>> String implodeForDB(Enum<E>[] array, String delimiter) {
		return genericArrayImplode(array, delimiter, STRING_ESCAPE_STRATEGY);
	}

	public static String implodeForDB(Collection<? extends Object> collection) {
		return genericImplode(collection, ",", OBJECT_TO_STRING_ESCAPE_STRATEGY);
	}

    @Deprecated
    public static String implodeForDB(Collection<? extends Object> collection, String delimiter) {
        return genericImplode(collection, ",", OBJECT_TO_STRING_ESCAPE_STRATEGY);
    }

	public static String implode(Collection<? extends Object> collection) {
		return genericImplode(collection, ",", NO_STRING_ESCAPE_STRATEGY);
	}

	public static String implode(Collection<? extends Object> collection, String delimiter) {
		return genericImplode(collection, delimiter, NO_STRING_ESCAPE_STRATEGY);
	}

	public static String implode(List<String> collection, String delimiter) {
		return genericImplode(collection, delimiter, NO_STRING_ESCAPE_STRATEGY);
	}

	private static <E> String genericArrayImplode(E[] array, String delimiter, int escapeType) {
        if (array == null || array.length == 0) {
            return EMPTY_STRING;
        }

		StringBuilder builder = new StringBuilder();
		for (E entity : array) {
			if (builder.length() > 0) {
				builder.append(delimiter);
			}

			appendEntity(builder, entity, escapeType);
		}

		return builder.toString();
	}

	private static <E> String genericImplode(Collection<E> collection, String delimiter, int escapeType) {
        if (collection == null || collection.size() == 0) {
            return EMPTY_STRING;
        }

		StringBuilder builder = new StringBuilder();
		for (E entity : collection) {
			if (builder.length() > 0) {
				builder.append(delimiter);
			}

			appendEntity(builder, entity, escapeType);
		}

		return builder.toString();
	}

	private static <E> void appendEntity(StringBuilder builder, E entity, int escapeType) {
		switch (escapeType) {
		case NO_STRING_ESCAPE_STRATEGY:
			builder.append(entity);
			break;

		case STRING_ESCAPE_STRATEGY:
			performStringEscapeStrategy(builder, entity);
			break;

		case OBJECT_TO_STRING_ESCAPE_STRATEGY:
			builder.append("'").append(escapeQuotesAndSlashes(String.valueOf(entity))).append("'");
			break;

		default:
			throw new RuntimeException("Invalid use of string escaping.");
		}
	}

	private static <E> void performStringEscapeStrategy(StringBuilder stringBuilder, E entity) {
		stringBuilder.append("'");

		if (entity instanceof String) {
			stringBuilder.append(escapeQuotesAndSlashes((String) entity));
		} else {
			stringBuilder.append(entity);
		}

		stringBuilder.append("'");
	}

    public static String hashUrlSafe(String seed) {
        String value = EncodedMessage.hash(seed);
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

	// TODO Why just the variations on the letter I? What about all of the other
	// diacriticals?
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
		name = name.replaceAll("[\\'\\\"]", EMPTY_STRING);
		// Take out "the" at the beginning of the sentences, llc & inc (in
		// multiple variations) at the end of sentences
		name = name.replaceAll("^THE ", EMPTY_STRING);
		name = name.replaceAll(" L ?L ?C$", EMPTY_STRING);
		name = name.replaceAll(" I ?N ?C$", EMPTY_STRING);
		// Remove all non-alphanumeric characters
//		name = name.replaceAll("\\W", EMPTY_STRING);
		name = name.replaceAll("_", EMPTY_STRING);
		// Change multiple spaces into nothing
		name = name.replaceAll(" +", EMPTY_STRING);
		name = name.trim();

		return name;
	}

	// TODO This function is useless. We should either properly validate input
	// with InputValidator or properly escape HTML for display
	@Deprecated
	public static String htmlStrip(String input) {
		if (Strings.isEmpty(input))
			return null;

		return input.replaceAll("<", EMPTY_STRING).replaceAll(">", EMPTY_STRING);
	}

	@Deprecated
	// TODO Remove this function because it's duplicated in EmailAddressUtils
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
			return EMPTY_STRING;

		if (input.length() <= maxlength)
			return input;

		return input.substring(0, maxlength - 3) + "...";
	}

	// TODO Remove this function, because it's duplicated in EmailAddressUtils
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
		}
		return result;
	}

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
		number = number.replaceAll("[^\\d\\.]", EMPTY_STRING);
		// Make sure that number doesn't have more than one decimal point
		try {
			String n1 = number.substring(0, number.indexOf(".") + 1);
			String n2 = number.substring(number.indexOf(".") + 1, number.length());
			n2 = n2.replace(".", EMPTY_STRING);
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

		number = number.replaceAll("0*$", EMPTY_STRING);
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

		return expression.replace("|", EMPTY_STRING);
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

	public static boolean isSimilarTo(String m, String n, int characterDifferenceThreshold) {
		return (StringUtils.getLevenshteinDistance(m, n) <= characterDifferenceThreshold) ? true : false;
	}

	public static boolean isSimilarTo(String m, String n) {
		return (StringUtils.getLevenshteinDistance(m.toLowerCase(), n.toLowerCase()) <= Math.sqrt(Math.min(m.length(),
				n.length())) - .25) ? true : false;
	}

	public static String capitalize(String uncapitalized) {
		if (!isEmpty(uncapitalized)) {
			return uncapitalized.substring(0, 1).toUpperCase() + uncapitalized.substring(1, uncapitalized.length());
		}

		return null;
	}

	public static String nullToBlank(String value) {
		if (value == null) {
			return EMPTY_STRING;
		}

		return value;
	}

	public static boolean isMixedCase(String value) {
		return (!value.equals(value.toUpperCase()) && !value.equals(value.toLowerCase()));
	}

	public static boolean bothNonBlanksAndOneBeginsWithTheOther(String first, String second) {
		if (!isEmpty(first) && !isEmpty(second)) {
			if (first.startsWith(second)) {
				return true;
			}

			if (second.startsWith(first)) {
				return true;
			}
		}

		return false;
	}

	public static String stripNonAlphaNumericCharacters(String toStrip) {
		if (!Strings.isEmpty(toStrip)) {
			return toStrip.replaceAll("[^A-Za-z0-9]", "");
		}

		return EMPTY_STRING;
	}

	public static String toStringPreserveNull(Object object) {
		if (object == null) {
			return null;
		}

		return object.toString();
	}

	public static String toString(Object object) {
		if (object == null) {
			return "";
		}

		return object.toString();
	}

    public static List<Integer> explodeCommaDelimitedStringOfIds(String stringOfIds) throws NumberFormatException {
        String[] stringArrayIds = stringOfIds.split(",");
        List<Integer> idsList = new ArrayList<Integer>();

        for (String contractorId: stringArrayIds) {
            try {
                idsList.add(Integer.valueOf(contractorId.trim()));
            } catch (NumberFormatException e) {
                logger.error("Tried parse ID : " + contractorId, e);
                throw e;
            }
        }
        return idsList;
    }

    public static String formatInternationalNumber(int number, Locale locale) {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.format(number);
    }

	public static int compareToIgnoreCase(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return 0;
		}

		if (s1 == null) {
			return 1;
		}

		if (s2 == null) {
			return -1;
		}

		return s1.compareToIgnoreCase(s2);
	}
}
