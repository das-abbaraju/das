package com.picsauditing.actions.converters;

import java.util.Locale;
import java.util.Map;

public class LocaleConverter extends EnumConverter {
	public LocaleConverter() {
		enumClass = Locale.class;
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		return (String) performFallbackConversion(arg0, arg1, String.class);
	}

	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {
		Object test = null;
		try {
			test = super.convertFromString(arg0, arg1, arg2);
		} catch (Exception itllJustStayNull) {
		}

		if (test == null) {
			if (arg1.length > 0) {
				String[] loc = arg1[0].split("[_-]");
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
			}
		}

		if (test == null) {
			test = performFallbackConversion(arg0, arg1, arg2);
		}

		return test;
	}
}
