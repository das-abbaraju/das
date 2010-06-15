package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

@SuppressWarnings("unchecked")
abstract public class EnumConverter extends StrutsTypeConverter {
	protected Class enumClass;

	@Override
	public Object convertFromString(Map arg0, String[] arg1, Class arg2) {

		Object response = null;

		if (arg2.equals(enumClass)) {
			if (arg1.length > 0) {
				response = Enum.valueOf(enumClass, arg1[0]);
			} else {
				response = null;
			}
		}
		return response;
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {

		String response = null;

		if (arg1.getClass().equals(enumClass)) {
			response = (Enum.class.cast(arg1)).name();
		}

		return response;
	}
}
