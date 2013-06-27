package com.picsauditing.service.i18n;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.picsauditing.util.Strings;

public interface TranslationService {

	public static final String CACHE_NAME = "daily";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;
	public static final String ACTION_TRANSLATION_KEYWORD = "ACTION";

	String getText(String key, Locale locale);

	String getText(String key, String locale);

	@Deprecated
	String getText(String key, Locale locale, Object... args);

	@Deprecated
	String getText(String key, String locale, Object... args);

	Map<String, String> getText(String key);

	boolean hasKey(String key, Locale locale);

    boolean hasKeyInLocale(String key, String locale);

	void saveTranslation(String key, String translation, List<String> requiredLanguages) throws Exception;

	void saveTranslation(String key, String translation) throws Exception;

	void removeTranslations(List<String> keys) throws Exception;

	void removeTranslation(String key) throws Exception;

	List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales);

	void clear();

	Date getLastCleared();
}
