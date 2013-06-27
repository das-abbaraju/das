package com.picsauditing.service.i18n;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TranslationServiceAdapter implements TranslationService {

	private static final TranslationServiceAdapter INSTANCE = new TranslationServiceAdapter();

	private TranslationServiceAdapter() {
	}

	public static TranslationServiceAdapter getInstance() {
		return INSTANCE;
	}

	@Override
	public String getText(String key, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(String key, String locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(String key, Locale locale, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(String key, String locale, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getText(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasKey(String key, Locale locale) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean hasKeyInLocale(String key, String locale) {
        return false;
    }

    @Override
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTranslations(List<String> keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTranslation(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getLastCleared() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTranslation(String key, String translation) throws Exception {
		// TODO Auto-generated method stub

	}

}
