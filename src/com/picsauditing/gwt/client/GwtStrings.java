package com.picsauditing.gwt.client;

/**
 * GWT Safe 
 * @author Trevor
 *
 */
public class GwtStrings {
	
	public static boolean isEmpty(String value) {
		if (value == null)
			return true;
		value = value.trim();
		return value.length() == 0;
	}

}
