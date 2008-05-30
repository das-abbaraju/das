package com.picsauditing.util;

import java.util.Collection;

import com.picsauditing.PICS.Utilities;

public class Strings {
	public static String insertSpaces(String value) {
		if (value == null) return null;
		StringBuilder newValue = new StringBuilder();
		
		for(int i=0; i < value.length(); i++) {
			newValue.append(value.charAt(i));
			newValue.append(" ");
		}
		return newValue.toString().trim();
	}
	
	public static String implode(int[] array, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		for (int o : array) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append(o);
		}
		return buffer.toString();
	}
	
	public static String implodeForDB(Enum[] array, String delimiter) {
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
		StringBuffer buffer = new StringBuffer();
		for (Object o : collection) {
			if (buffer.length() > 0)
				buffer.append(delimiter);
			buffer.append(o);
		}
		return buffer.toString();
	}
}
