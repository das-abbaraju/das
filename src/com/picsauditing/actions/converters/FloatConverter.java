package com.picsauditing.actions.converters;

import java.util.Map;

import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class FloatConverter extends EnumConverter {
	public FloatConverter() {
		enumClass = int.class;
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
			if (arg1.length > 0 && !Strings.isEmpty(arg1[0])) {
				String temp = arg1[0];
				temp = temp.replaceAll("\\,", "");
				temp = temp.replaceAll(" ", "");
				try {
					test = Float.parseFloat(temp);
				} catch (Exception e) {
					try {
						test = performFallbackConversion(arg0, arg1, arg2);
					} catch (Exception e2) {
						test = 0;
					}
				}
			}
		}

		return test;
	}

}
