package com.picsauditing.actions.converters;

import java.util.Locale;
import java.util.Map;

import com.picsauditing.util.Strings;

public class LocaleConverter extends EnumConverter {
	public LocaleConverter() {
		enumClass = Locale.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String convertToString(Map arg0, Object arg1) {
		return (String) performFallbackConversion(arg0, arg1, String.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {
		Object test = null;
		try {
			test = super.convertFromString(arg0, arg1, arg2);
		} catch (Exception itllJustStayNull) {
		}

		if (test == null) {
			if (arg1.length > 0) {
				test = Strings.parseLocale(arg1[0]);
			}
		}

		if (test == null) {
			test = performFallbackConversion(arg0, arg1, arg2);
		}

		return test;
	}
}
