package com.picsauditing.actions.converters;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.Strings;

@SuppressWarnings("rawtypes")
public class TranslatableStringConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		TranslatableString response = new TranslatableString();

		// if (values.length > 0) {
		// try {
		// ActionInvocation invocation =
		// ActionContext.getContext().getActionInvocation();
		// response = (TranslatableString) invocation.getStack().findValue(
		// context.get("conversion.property.fullName").toString());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// Locale locale = ActionContext.getContext().getLocale();
		// response.handleTranslation(locale, values[0]);
		// } else {
		// response = null;
		// }
		return response;
	}

	@Override
	public String convertToString(Map context, Object o) {
		try {
			return o.toString();
		} catch (Exception e) {
			return Strings.EMPTY_STRING;
		}
	}
}
