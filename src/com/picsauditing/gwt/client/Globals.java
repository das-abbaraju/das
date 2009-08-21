package com.picsauditing.gwt.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class Globals {
	
	public static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("MM/dd/yyyy");

	public static String formatDate(Date date){
		if(date == null) return "";
		return dateTimeFormat.format(date);
	}
}
