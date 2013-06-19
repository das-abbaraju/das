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
import org.apache.commons.lang3.StringUtils;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.dao.AppTranslationDAO;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.ContextTranslation;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class I18nCache implements Serializable {

	private static final long serialVersionUID = -9105914451729814391L;

	public static final String I18N_CACHE_KEY = "I18nCache";
	public static final String CACHE_NAME = "daily";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;
	public static final String ACTION_TRANSLATION_KEYWORD = "ACTION";

	private transient static I18nCache INSTANCE;
	private transient static Date LAST_CLEARED;

	private volatile transient Table<String, String, String> cache;
	private volatile transient Table<String, String, String> translationsForJS;
	private transient Map<String, Date> cacheUsage;

	private static Database databaseForTesting = null;
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

	public void addBuildListener(I18nCacheBuildAware listener) {
		if (!buildListeners.contains(listener)) {
			buildListeners.add(listener);
		}
	}

	public boolean removeBuildListener(I18nCacheBuildAware listener) {
		for (I18nCacheBuildAware candidate : buildListeners) {
			if (candidate.equals(listener)) {
				return buildListeners.remove(listener);
			}
		}

		return false;
	}

	public void clearBuildListeners() {
		buildListeners.clear();
	}

	private String findAnyLocale(String key) {
		Map<String, String> locales = cache.row(prepareKeyForCache(key));
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
		return cache.contains(prepareKeyForCache(key), locale);
	}

	public boolean hasKey(String key, Locale locale) {
		return hasKey(key, getLocaleFallback(key, locale));
	}

	public Map<String, String> getText(String key) {
		return cache.row(prepareKeyForCache(key));
	}

	private String getText(String key, String locale) {
		updateCacheUsed(key);

		String value = cache.get(prepareKeyForCache(key), locale);

		value = getTranslationFallback(key, value, locale);

		return value;
	}

	private void updateCacheUsed(String key) {
		Date lastUsed = cacheUsage.get(key);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);

		if (lastUsed == null || lastUsed.before(yesterday.getTime())) {
			Database db = getDatabase();
			String sql = "UPDATE app_translation SET lastUsed = NOW() WHERE msgKey = '" + Strings.escapeQuotes(key)
					+ "'";
			cacheUsage.put(key, new Date());

			try {
				db.execute(sql);
			} catch (SQLException e) {
				throw new RuntimeException("Failed to reset lastUsed on app_translation because: " + e.getMessage());
			}
		}
	}

	public String getText(String key, Locale locale) {
		return getText(key, getLocaleFallback(key, locale));
	}

	private String getText(String key, String locale, Object... args) {
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
				newCache.put(prepareKeyForCache(key), String.valueOf(message.get("locale")),
						String.valueOf(message.get("msgValue")));
				Date lastUsed = (Date) message.get("lastUsed");
				newCacheUsage.put(key, lastUsed);
			}

			List<ContextTranslation> contextTranslations = getAppTranslationDAO().findAllForJS();
			for (ContextTranslation contextTranslation : contextTranslations) {
				String key = contextTranslation.getI18nKey();
				String translation = contextTranslation.getTranslaton();
				newCache.put(prepareKeyForCache(key), contextTranslation.getLocale(), translation);
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

	private Database getDatabase() {
		if (databaseForTesting == null) {
			return new Database();
		} else {
			return databaseForTesting;
		}
	}

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
			fallbackTranslation = "";
		}

		if (DEFAULT_LANGUAGE.equals(locale)) {
			logger.error("Translation key '{}' has no english translation.", key);
		} else {
			// If a foreign translation was invalid, check the English
			// translation
			String englishValue = cache.get(prepareKeyForCache(key), DEFAULT_LANGUAGE);

			if (isValidTranslation(englishValue)) {
				fallbackTranslation = englishValue;
			} else {
				logger.error("Translation key '{}' has no english translation.", key);
			}
		}

		return fallbackTranslation;
	}

	// public void saveTranslatableString(String key, TranslatableString value,
	// List<String> requiredLanguages)
	// throws SQLException {
	// if (value == null) {
	// return;
	// }
	//
	// Database db = getDatabase();
	// String sourceLanguage = null;
	//
	// if (!requiredLanguages.isEmpty()) {
	// sourceLanguage = requiredLanguages.get(0);
	// }
	//
	// // Make sure we handle clearing the cache across multiple servers
	// Iterator<Translation> iterator = value.getTranslations().iterator();
	// while (iterator.hasNext()) {
	// Translation translationFromCache = iterator.next();
	//
	// AppTranslation newTranslation = new AppTranslation();
	// newTranslation.setKey(key);
	// newTranslation.setLocale(translationFromCache.getLocale());
	// newTranslation.setValue(translationFromCache.getValue());
	// newTranslation.setSourceLanguage(sourceLanguage);
	// newTranslation.setQualityRating(TranslationQualityRating.Good);
	// newTranslation.setApplicable(true);
	//
	// if (requiredLanguages.size() > 0 &&
	// !requiredLanguages.contains(newTranslation.getLocale())) {
	// newTranslation.setApplicable(false);
	// }
	//
	// if (newTranslation.isKeyContentDriven()) {
	// newTranslation.setContentDriven(true);
	// }
	//
	// if (translationFromCache.isDelete()) {
	// String sql =
	// String.format("DELETE FROM app_translation WHERE msgKey = '%s' AND locale = '%s'",
	// key,
	// translationFromCache.getLocale());
	// db.executeUpdate(sql);
	// cache.remove(prepareKeyForCache(newTranslation.getKey()),
	// newTranslation.getLocale());
	// iterator.remove();
	// } else if (translationFromCache.isModified()) {
	// db.executeUpdate(buildUpdateStatement(newTranslation));
	// } else if (translationFromCache.isInsert()) {
	// String insert = buildInsertStatement(newTranslation);
	//
	// if (insert != null) {
	// db.executeInsert(insert);
	// }
	// }
	//
	// updateCacheAndRemoveTranslationFlagsIfNeeded(translationFromCache,
	// newTranslation);
	// }
	//
	// insertUpdateRequiredLanguages(requiredLanguages, key, sourceLanguage);
	// }

	// public void removeTranslatableStrings(List<String> keys) throws
	// SQLException {
	// if (CollectionUtils.isEmpty(keys)) {
	// return;
	// }
	//
	// for (String key : keys) {
	// cache.row(prepareKeyForCache(key)).clear();
	// }
	//
	// String sql = "DELETE FROM app_translation WHERE msgKey IN (" +
	// Strings.implodeForDB(keys) + ")";
	// Database db = getDatabase();
	// db.executeUpdate(sql);
	// }

	public static Date getLastCleared() {
		return LAST_CLEARED;
	}

	private String buildInsertStatement(AppTranslation translationToinsert) {
		String sourceLanguage = translationToinsert.getSourceLanguage();
		if (Strings.isEmpty(sourceLanguage)) {
			sourceLanguage = "NULL";
		} else {
			sourceLanguage = "'" + sourceLanguage + "'";
		}

		String format = "INSERT INTO app_translation (msgKey, locale, msgValue, qualityRating, sourceLanguage, "
				+ "createdBy, updatedBy, creationDate, updateDate, lastUsed, contentDriven, applicable) "
				+ "VALUES ('%s', '%s', '%s', %d, %s, 1, 1, NOW(), NOW(), NOW(), %d, %d) "
				+ "ON DUPLICATE KEY UPDATE msgValue = '%s', qualityRating = %d, "
				+ "updateDate = NOW(), contentDriven = %d, applicable = %d";

		String msgKey = translationToinsert.getKey();
		String locale = translationToinsert.getLocale();
		String msgValue = Strings.escapeQuotes(translationToinsert.getValue());
		int qualityRating = translationToinsert.getQualityRating().ordinal();

		int contentDriven = translationToinsert.isContentDriven() ? 1 : 0;
		int applicable = translationToinsert.isApplicable() ? 1 : 0;

		return String.format(format, msgKey, locale, msgValue, qualityRating, sourceLanguage, contentDriven,
				applicable, msgValue, qualityRating, contentDriven, applicable);
	}

	private String buildUpdateStatement(AppTranslation translationToUpdate) {
		String setClause = "SET msgValue = '%s', updateDate = NOW()";

		if (translationToUpdate.getQualityRating() != null) {
			setClause += ", qualityRating = " + translationToUpdate.getQualityRating().ordinal();
		}

		String sourceLanguage = translationToUpdate.getSourceLanguage();
		if (!Strings.isEmpty(sourceLanguage)) {
			setClause += ", sourceLanguage = '" + sourceLanguage + "'";
		}

		setClause += ", applicable = " + (translationToUpdate.isApplicable() ? "1" : "0");
		setClause += ", contentDriven = " + (translationToUpdate.isContentDriven() ? "1" : "0");

		String format = "UPDATE app_translation " + setClause + " WHERE msgKey = '%s' AND locale = '%s'";
		String translationValueQuotationEscaped = StringUtils.replace(translationToUpdate.getValue(), "'", "''");

		String updateSQL = String.format(format, translationValueQuotationEscaped, translationToUpdate.getKey(),
				translationToUpdate.getLocale());

		return updateSQL;
	}

	// private void updateCacheAndRemoveTranslationFlagsIfNeeded(Translation
	// translationFromCache,
	// AppTranslation newTranslation) {
	// if (translationFromCache.isInsert() || translationFromCache.isModified())
	// {
	// updateCacheWithTranslation(newTranslation);
	// translationFromCache.commit();
	// }
	// }

	private void updateCacheWithTranslation(AppTranslation newTranslation) {
		cache.put(prepareKeyForCache(newTranslation.getKey()), newTranslation.getLocale(), newTranslation.getValue());
	}

	// private void insertUpdateRequiredLanguages(List<String>
	// requiredLanguages, String key, String source)
	// throws SQLException {
	// setUnneededLanguagesNotApplicable(requiredLanguages, key, source);
	//
	// for (String requiredLanguage : requiredLanguages) {
	// AppTranslation insertUpdateTranslation = new AppTranslation();
	// insertUpdateTranslation.setKey(key);
	// insertUpdateTranslation.setLocale(requiredLanguage);
	// insertUpdateTranslation.setSourceLanguage(source);
	// insertUpdateTranslation.setApplicable(true);
	//
	// if (insertUpdateTranslation.isKeyContentDriven()) {
	// insertUpdateTranslation.setContentDriven(true);
	// }
	//
	// if (hasKey(key, requiredLanguage)) {
	// updateRequiredLanguage(requiredLanguage, insertUpdateTranslation);
	// } else {
	// insertRequiredTranslation(insertUpdateTranslation);
	// }
	//
	// updateCacheWithTranslation(insertUpdateTranslation);
	// }
	// }

	private void setUnneededLanguagesNotApplicable(List<String> requiredLanguages, String key, String source)
			throws SQLException {
		Map<String, String> allLanguages = getText(key);

		for (String locale : allLanguages.keySet()) {
			if (!requiredLanguages.contains(locale)) {
				AppTranslation unneededTranslation = new AppTranslation();
				unneededTranslation.setKey(key);
				unneededTranslation.setLocale(locale);
				unneededTranslation.setSourceLanguage(source);
				unneededTranslation.setApplicable(false);

				if (unneededTranslation.isKeyContentDriven()) {
					unneededTranslation.setContentDriven(true);
				}

				updateRequiredLanguage(locale, unneededTranslation);
			}
		}
	}

	private void insertRequiredTranslation(AppTranslation insertUpdateTranslation) throws SQLException {
		insertUpdateTranslation.setValue(DEFAULT_TRANSLATION);
		insertUpdateTranslation.setQualityRating(TranslationQualityRating.Bad);

		Database db = getDatabase();
		db.executeInsert(buildInsertStatement(insertUpdateTranslation));
	}

	private void updateRequiredLanguage(String requiredLanguage, AppTranslation insertUpdateTranslation)
			throws SQLException {
		String requiredLanguageMsgValue = getText(insertUpdateTranslation.getKey(), requiredLanguage);

		insertUpdateTranslation.setValue(requiredLanguageMsgValue);
		insertUpdateTranslation.setQualityRating(TranslationQualityRating.Questionable);

		Database db = getDatabase();
		db.executeUpdate(buildUpdateStatement(insertUpdateTranslation));
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
		if ("Translation Missing".equalsIgnoreCase(translation)) {
			return false;
		}

		return true;
	}

	public String prepareKeyForCache(String key) {
		String keyUppercase = Strings.EMPTY_STRING;

		try {
			keyUppercase = key.toUpperCase();
		} catch (Exception e) {
			logger.warn("Null passed to I18nCache to be translated. Is this OK?");
		}

		return keyUppercase;
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
			appTranslationDAO = new AppTranslationDAO(getDatabase());
			return appTranslationDAO;
		}

		return appTranslationDAO;
	}
}
