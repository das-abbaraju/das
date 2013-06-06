package com.picsauditing.model.i18n;

import java.util.Locale;

import org.apache.struts2.ServletActionContext;

public class StrutsLocaleProvider implements LocaleProvider {

	@Override
	public Locale getLocale() {
		return ServletActionContext.getContext().getLocale();
	}

}
