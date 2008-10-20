package com.picsauditing.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

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
		for(String item : list) {
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

	public static String implode(Collection<Integer> collection,
			String delimiter) {
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
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
		digest.update(seed.getBytes());
		byte[] hashed = digest.digest();
		String value = Base64.encodeBytes(hashed);
		return value;
	}

	public static String hashUrlSafe(String seed) {
		String value = Strings.hash(seed);
		value = value.replace("+", "_");
		return value;
	}
}
