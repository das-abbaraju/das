package com.picsauditing.jsf.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utilities {

	public static int getCurrentYear() {
		 Calendar cal = Calendar.getInstance();
		 return cal.get(Calendar.YEAR);
	}
	
	public static String getTodaysDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("M/d/yy");
		String temp = format.format(cal.getTime());
		return temp;
	}
}
