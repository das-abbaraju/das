package com.picsauditing.actions;

import java.util.Locale;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class PicsPolicy extends PicsActionSupport {
	Locale request_locale;

	@Anonymous
	public String execute() {
		if (permissions == null)
			loadPermissions();
		if (request_locale != null)
			permissions.setLocale(request_locale);

		return SUCCESS;
	}

	public Locale getRequest_locale() {
		return request_locale;
	}

	public void setRequest_locale(Locale requestLocale) {
		request_locale = requestLocale;
	}

}
