package com.picsauditing.actions.converters;

import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class TranslatableStringConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		TranslatableString response = new TranslatableString();

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			Locale locale = ActionContext.getContext().getLocale();
			response.putTranslation(locale.toString(), values[0]);
		} else {
			response = null;
		}
		return response;
	}

	@Override
	public String convertToString(Map context, Object o) {
		return o.toString();
	}
}
