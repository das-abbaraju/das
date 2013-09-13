package com.picsauditing.validator;

import java.util.Locale;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public class BasicTranslationSupport {

	private TranslationService translationService = TranslationServiceFactory.getTranslationService();

	protected String getText(String key, User user) {
		return translationService.getText(key, getLocale(user));
	}

	@SuppressWarnings("deprecation")
	protected String getText(String key, User user, Object... args) {
		return translationService.getText(key, getLocale(user), args);
	}

	private Locale getLocale(User user) {
		if (user != null) {
			return user.getLocale();
		}

		return Locale.ENGLISH;
	}
}
