package com.picsauditing.actions.converters;

import java.util.Map;
import java.util.TimeZone;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class TimeZoneConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		Object test = null;

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			String temp = values[0];
			test = TimeZone.getTimeZone(temp);
		}

		return test;
	}

	@Override
	public String convertToString(Map context, Object o) {
		return ((TimeZone) o).getID();
	}

}
