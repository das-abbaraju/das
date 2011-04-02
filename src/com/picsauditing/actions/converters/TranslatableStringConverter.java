package com.picsauditing.actions.converters;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.util.Strings;

import freemarker.template.utility.StringUtil;

@SuppressWarnings("unchecked")
public class TranslatableStringConverter extends StrutsTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		TranslatableString response = new TranslatableString();

		if (values.length > 0 && !Strings.isEmpty(values[0])) {
			try {
				Object action = ActionContext.getContext().getActionInvocation().getAction();
				Field object = action.getClass().getDeclaredField(context.get("current.property.path").toString());
				action.getClass().getMethod("get" + StringUtil.capitalize(context.get("current.property.path").toString()));
				context.get("conversion.property.fullName");
				Locale locale = ActionContext.getContext().getLocale();
				response.putTranslation(locale.toString(), values[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
