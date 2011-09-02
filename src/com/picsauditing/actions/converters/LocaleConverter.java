package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class LocaleConverter extends StrutsTypeConverter {
	@Override
	public String convertToString(Map arg0, Object arg1) {
		return (String) performFallbackConversion(arg0, arg1, String.class);
	}

	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {
		Object test = null;

		if (arg1.length > 0) {
			test = Strings.parseLocale(arg1[0]);
		}

		if (test == null) {
			test = performFallbackConversion(arg0, arg1, arg2);
		}

		return test;
	}

}
