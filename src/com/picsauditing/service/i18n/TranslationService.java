package com.picsauditing.service.i18n;

import java.sql.SQLException;
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

	void saveTranslation(String key, String translation, List<String> requiredLanguages);

	void removeTranslations(List<String> keys) throws SQLException;

	void removeTranslation(String key);

	List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales);

	void clear();

	Date getLastCleared();
}
