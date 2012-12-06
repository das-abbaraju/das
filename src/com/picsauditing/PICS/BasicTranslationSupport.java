package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.User;

import java.util.Locale;

public class BasicTranslationSupport {

	private I18nCache i18nCache = I18nCache.getInstance();

	protected String getText(String key, User user) {
		return i18nCache.getText(key, getLocale(user));
	}

	protected String getText(String key, User user, Object... args) {
		return i18nCache.getText(key, getLocale(user), args);
	}

	private Locale getLocale(User user) {
		if (user != null) {
			return user.getLocale();
		}
		return Locale.ENGLISH;
	}
}
