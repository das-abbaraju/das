package com.picsauditing.service.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface TranslationProvider {

	String getText(String key, Locale locale);

	String getText(String key, String locale);

	@Deprecated
	String getText(String key, Locale locale, Object... args);

	@Deprecated
	String getText(String key, String locale, Object... args);

	Map<String, String> getText(String key);

	boolean hasKey(String key, Locale locale);

	List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales);

}
