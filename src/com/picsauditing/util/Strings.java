package com.picsauditing.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.picsauditing.jpa.entities.Note;

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

	public static String implode(Collection<Integer> collection, String delimiter) {
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

	public static ArrayList<Note> convertNotes(String oldNote) {
		// ([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)? [\(]*(.*?)[\)]*: (.*)
		//System.out.println(oldNote);
		ArrayList<Note> notes = new ArrayList<Note>();

		String expression = "^([0-9]{1,4}/[0-9]{1,2}/[0-9]{1,4})( [0-9]{1,2}:[0-9]{2} [AP]M .{3}?)? [\\(]*(.*?)[\\)]*: (.*)";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(oldNote);
		Note note = new Note();
		while (matcher.find()) {
			System.out.println(matcher.groupCount() + " groups");
			System.out.println("0 " + matcher.group(0));
			System.out.println("1 " + matcher.group(1));
			System.out.println("2 " + matcher.group(2));
			System.out.println("3 " + matcher.group(3));
			System.out.println("4 " + matcher.group(4));
			System.out.println(String.format("I found the text \"%s\" starting at "
					+ "index %d and ending at index %d.%n", matcher.group(), matcher.start(), matcher.end()));
		}
		return notes;
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
	
	public static float toFloat(String value) {
		float newValue = 0;
		try {
			newValue = Float.parseFloat(value);
		} catch (NumberFormatException e) {}
		return newValue;
	}
	
}
