package com.picsauditing.actions;

import java.util.Locale;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class PicsPolicy extends PicsActionSupport {
	Locale request_locale = Locale.US;

	@Anonymous
	public String execute() {
		if (permissions == null) {
			/**
			 * Block for passing in locale anonymously, for places like Email Subscriptions or Registration
			 */
			loadPermissions();
			User u = new User(User.SYSTEM);
			u.setLocale(request_locale);
			try {
				permissions.login(u);
			} catch (Exception e) {
			}
		}

		return SUCCESS;
	}

	public Locale getRequest_locale() {
		return request_locale;
	}

	public void setRequest_locale(Locale requestLocale) {
		request_locale = requestLocale;
	}

}
