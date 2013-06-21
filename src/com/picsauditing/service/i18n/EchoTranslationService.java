package com.picsauditing.service.i18n;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class EchoTranslationService implements TranslationService {

	@Override
	public String getText(String key, Locale locale) {
		return MessageFormat.format("Translate[{0}=>{1}]", key, locale);
	}

	@Override
	public String getText(String key, String locale) {
		return MessageFormat.format("Translate[{0}=>{1}]", key, locale);
	}

	@Override
	public String getText(String key, Locale locale, Object... args) {
		return MessageFormat.format("Translate[{0}=>{1}=>{2}]", key, locale, Arrays.toString(args));
	}

	@Override
	public String getText(String key, String locale, Object... args) {
		return MessageFormat.format("Translate[{0}=>{1}=>{2}]", key, locale, Arrays.toString(args));
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, String> getText(final String key) {
		return new HashMap<String, String>() {
			{
				put("Locale", key);
			}
		};
	}

	@Override
	public boolean hasKey(String key, Locale locale) {
		return true;
	}

	@Override
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) {
	}

	@Override
	public void removeTranslations(List<String> keys) throws SQLException {
	}

	@Override
	public void removeTranslation(String key) {
	}

	@Override
	public List<Map<String, String>> getTranslationsForJS(final String actionName, final String methodName,
			final Set<String> locales) {
		@SuppressWarnings("serial")
		Map<String, String> translations = new HashMap<String, String>() {
			{
				put(actionName + "." + "methodName" + ".", locales.toString());
			}
		};

		return new ArrayList<Map<String, String>>(Arrays.asList(translations));
	}

	@Override
	public void clear() {
	}

	@Override
	public Date getLastCleared() {
		return new Date();
	}

	@Override
	public void saveTranslation(String key, String translation) throws Exception {
	}

}
