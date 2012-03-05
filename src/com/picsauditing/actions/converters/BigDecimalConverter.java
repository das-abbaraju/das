package com.picsauditing.actions.converters;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class BigDecimalConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		Object test = null;

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			test = performFallbackConversion(context, values[0], BigDecimal.class);
		}

		return test;
	}

	@Override
	public String convertToString(Map context, Object o) {
		return String.valueOf(performFallbackConversion(context, o, BigDecimal.class));
	}
}
