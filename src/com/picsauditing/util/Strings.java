package com.picsauditing.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.picsauditing.PICS.Utilities;

public class Strings {

	public static boolean isEmpty(String value) {
		if (value == null)
			return true;
		value = value.trim();
		return value.length() == 0;
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
			return "";
		StringBuffer buffer = new StringBuffer();
		for (String o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append("'");
			buffer.append(Utilities.escapeQuotes(o));
			buffer.append("'");
		}
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	public static String implodeForDB(Enum[] array, String delimiter) {
		if (array == null)
			return "";
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
			digest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
		digest.update(seed.getBytes());
		byte[] hashed = digest.digest();
		String value = Base64.encodeBytes(hashed);
		return value;
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
			return Integer.parseInt(idString);
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
		input = input.replace('�', '"');
		input = input.replace('�', '"');
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

		String expression = "[A-Z0-9]+";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(name);

		StringBuffer buf = new StringBuffer();
		boolean found = false;
		while ((found = matcher.find())) {
			System.out.println(matcher.group());
			buf.append(matcher.group());
		}
		
		return buf.toString();
	}
	
	
	public static String htmlStrip(String input) {
		if (Strings.isEmpty(input))
			return null;

		return input.replaceAll("<", "").replaceAll(">", "");
	}

}
