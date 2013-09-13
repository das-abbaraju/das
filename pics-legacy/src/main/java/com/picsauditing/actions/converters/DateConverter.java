package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class DateConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		Object test = null;

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			String temp = values[0];
			test = DateBean.parseDate(temp);
		}

		return test;
	}

	@Override
	public String convertToString(Map context, Object o) {
		return String.valueOf(o);
	}

}
