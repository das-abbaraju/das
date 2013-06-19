package com.picsauditing.PICS;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.dao.AppTranslationDAO;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TranslationUtil;

public class I18nCache implements TranslationService, Serializable {

	private static final String TRANSLATION_MISSING = "Translation Missing";

	private static final long serialVersionUID = -9105914451729814391L;

	// public static final String CACHE_NAME = "daily";
	// public static final String DEFAULT_LANGUAGE = "en";
	// public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;
	// public static final String ACTION_TRANSLATION_KEYWORD = "ACTION";

	private transient static I18nCache INSTANCE;
	private transient static Date LAST_CLEARED;

	private volatile transient Table<String, String, String> cache;
	private volatile transient Table<String, String, String> translationsForJS;
	private transient Map<String, Date> cacheUsage;

	private static List<I18nCacheBuildAware> buildListeners = new ArrayList<I18nCacheBuildAware>();

	private static final Logger logger = LoggerFactory.getLogger(I18nCache.class);

	// This is for JUnit testing
	static AtomicInteger instantiationCount = new AtomicInteger(0);
	private static AppTranslationDAO appTranslationDAO;

	private I18nCache() {
	}

	public static I18nCache getInstance() {
		I18nCache cache = INSTANCE;
		if (cache == null) {
			synchronized (I18nCache.class) {
				cache = INSTANCE;
				if (cache == null) {
					INSTANCE = new I18nCache();
					I18nCache.instantiationCount.getAndIncrement();
					INSTANCE.buildCache();
				}
			}
		}
		return INSTANCE;
	}

	private String findAnyLocale(String key) {
		Map<String, String> locales = cache.row(TranslationUtil.prepareKeyForCache(key));
		if (locales == null) {
			return null;
		}

		if (locales.size() > 0) {
			for (String locale : locales.keySet()) {
				// This always returns the first locale. Is that what we want?
				return locale;
			}
		}

		return null;
	}

	private boolean hasKey(String key, String locale) {
		return cache.contains(TranslationUtil.prepareKeyForCache(key), locale);
	}

	public boolean hasKey(String key, Locale locale) {
		return hasKey(key, getLocaleFallback(key, locale));
	}

	public Map<String, String> getText(String key) {
		return cache.row(TranslationUtil.prepareKeyForCache(key));
	}

	public String getText(String key, String locale) {
		updateCacheUsed(key);

		String value = cache.get(TranslationUtil.prepareKeyForCache(key), locale);

		value = getTranslationFallback(key, value, locale);

		return value;
	}

	private void updateCacheUsed(String key) {
		Date lastUsed = cacheUsage.get(key);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);

		if (lastUsed == null || lastUsed.before(yesterday.getTime())) {
			cacheUsage.put(key, new Date());
			appTranslationDAO.updateTranslationLastUsed(key);
		}
	}

	public String getText(String key, Locale locale) {
		return getText(key, getLocaleFallback(key, locale));
	}

	public String getText(String key, String locale, Object... args) {
		if (hasKey(key, locale)) {
			if (args == null || args.length == 0) {
				return getText(key, locale);
			}

			MessageFormat message = new MessageFormat(fixFormatCharacters(getText(key, locale)),
					Strings.parseLocale(locale));
			StringBuffer buffer = new StringBuffer();
			message.format(args, buffer, null);

			return buffer.toString();
		}

		return getTranslationFallback(key, null, locale);
	}

	public String getText(String key, Locale locale, Object... args) {
		return getText(key, getLocaleFallback(key, locale), args);
	}

	private String fixFormatCharacters(String text) {
		return text.replaceAll("'", "''");
	}

	public void clear() {
		synchronized (this) {
			buildCache();
		}
	}

	private void buildCache() {
		boolean successful = true;
		StopWatch stopWatch = startBuild();

		try {
			Table<String, String, String> newCache = TreeBasedTable.create();
			Map<String, Date> newCacheUsage = new HashMap<String, Date>();
			Table<String, String, String> newTranslationsForJS = TreeBasedTable.create();

			List<BasicDynaBean> messages = getAppTranslationDAO().getTranslationsForI18nCache();
			for (BasicDynaBean message : messages) {
				String key = String.valueOf(message.get("msgKey"));
				newCache.put(TranslationUtil.prepareKeyForCache(key), String.valueOf(message.get("locale")),
						String.valueOf(message.get("msgValue")));
				Date lastUsed = (Date) message.get("lastUsed");
				newCacheUsage.put(key, lastUsed);
			}

			List<ContextTranslation> contextTranslations = getAppTranslationDAO().findAllForJS();
			for (ContextTranslation contextTranslation : contextTranslations) {
				String key = contextTranslation.getI18nKey();
				String translation = contextTranslation.getTranslaton();
				newCache.put(TranslationUtil.prepareKeyForCache(key), contextTranslation.getLocale(), translation);
				newCacheUsage.put(key, contextTranslation.getLastUsed());
				newTranslationsForJS.put(buildKeyForJS(contextTranslation), key, translation);
			}

			cache = newCache;
			translationsForJS = newTranslationsForJS;
			cacheUsage = newCacheUsage;
			LAST_CLEARED = new Date();
		} catch (SQLException e) {
			logger.error("Error building i18nCache: {}", e.getMessage());
			successful = false;
		} finally {
			cleanupAfterCacheIsBuilt(successful, stopWatch);
		}
	}

	private String buildKeyForJS(ContextTranslation translation) {
		return buildKeyForJS(translation.getActionName(), translation.getMethodName(), translation.getLocale());
	}

	private String buildKeyForJS(String actionName, String methodName, String locale) {
		return new StringBuffer(actionName).append(".").append(methodName).append(".").append(locale).toString();
	}

	private void cleanupAfterCacheIsBuilt(boolean successful, StopWatch stopWatch) {
		stopBuild(stopWatch, successful);
		if (cache == null) {
			cache = TreeBasedTable.create();
		}

		if (cacheUsage == null) {
			cacheUsage = new HashMap<String, Date>();
		}

		if (translationsForJS == null) {
			translationsForJS = TreeBasedTable.create();
		}
	}

	private void stopBuild(StopWatch stopWatch, boolean successful) {
		stopWatch.stop();
		logger.info("Built i18n Cache in {} ms", stopWatch.getElapsedTime());
		for (I18nCacheBuildAware listener : buildListeners) {
			listener.cacheBuildStopped(stopWatch.getElapsedTime(), successful);
		}
	}

	private StopWatch startBuild() {
		StopWatch stopWatch = new StopWatch("I18nCache");
		stopWatch.start();
		logger.debug("Starting build of i18nCache: {}", stopWatch.getStartTime());
		for (I18nCacheBuildAware listener : buildListeners) {
			listener.cacheBuildStarted(stopWatch.getStartTime());
		}
		return stopWatch;
	}

	// private Database getDatabase() {
	// if (databaseForTesting == null) {
	// return new Database();
	// } else {
	// return databaseForTesting;
	// }
	// }

	private String getLocaleFallback(String key, Locale locale) {
		String localeString = locale.toString();
		if (hasKey(key, localeString)) {
			return localeString;
		}

		String languageString = locale.getLanguage();
		if (hasKey(key, languageString)) {
			return languageString;
		}

		if (!hasKey(key, DEFAULT_LANGUAGE)) {
			String anyLocale = findAnyLocale(key);

			if (anyLocale != null) {
				return anyLocale;
			}
		}

		return DEFAULT_LANGUAGE;
	}

	private String getTranslationFallback(String key, String translatedValue, String locale) {
		if (isValidTranslation(translatedValue)) {
			return translatedValue;
		}

		String fallbackTranslation = key;

		// TODO this should be temporary because we probably need a new column
		// in the DB
		// that flags whether a translation is ready for primetime. This would
		// require us to
		// change all the logic that automatically inserts translations into the
		// DB.
		if (key.toLowerCase().endsWith("helptext")) {
			fallbackTranslation = Strings.EMPTY_STRING;
		}

		if (DEFAULT_LANGUAGE.equals(locale)) {
			logger.error("Translation key '{}' has no english translation.", key);
		} else {
			// If a foreign translation was invalid, check the English
			// translation
			String englishValue = cache.get(TranslationUtil.prepareKeyForCache(key), DEFAULT_LANGUAGE);

			if (isValidTranslation(englishValue)) {
				fallbackTranslation = englishValue;
			} else {
				logger.error("Translation key '{}' has no english translation.", key);
			}
		}

		return fallbackTranslation;
	}

	public Date getLastCleared() {
		return LAST_CLEARED;
	}

	private boolean isValidTranslation(String translation) {
		if (translation == null) {
			return false;
		}

		if (DEFAULT_TRANSLATION.equals(translation)) {
			return false;
		}

		// TODO remove this once we have scrubbed the database of
		// "Translation Missing"s
		if (TRANSLATION_MISSING.equalsIgnoreCase(translation)) {
			return false;
		}

		return true;
	}

	public List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales) {
		if (CollectionUtils.isEmpty(locales) || Strings.isEmpty(actionName) || Strings.isEmpty(methodName)) {
			return Collections.emptyList();
		}

		List<String> keys = generateKeys(actionName, methodName, locales);

		List<Map<String, String>> translations = new ArrayList<>();
		for (String key : keys) {
			translations.add(Collections.unmodifiableMap(translationsForJS.row(key)));
		}

		return translations;
	}

	private List<String> generateKeys(String actionName, String methodName, Set<String> locales) {
		List<String> keys = new ArrayList<String>();
		for (String locale : locales) {
			keys.add(buildKeyForJS(actionName, methodName, locale));
			keys.add(buildKeyForJS(actionName, ACTION_TRANSLATION_KEYWORD, locale));
		}

		return keys;
	}

	private AppTranslationDAO getAppTranslationDAO() {
		if (appTranslationDAO == null) {
			// not using the SpringUtils because the SpringContext has not been
			// loaded before this is called.
			appTranslationDAO = new AppTranslationDAO();
			return appTranslationDAO;
		}

		return appTranslationDAO;
	}

	@Override
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTranslations(List<String> keys) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTranslation(String key) {
		// TODO Auto-generated method stub

	}
}
