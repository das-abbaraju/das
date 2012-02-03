package com.picsauditing.PICS;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.TranslatableString.Translation;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class I18nCache implements Serializable {

	private static final long serialVersionUID = -9105914451729814391L;

	static public final String I18N_CACHE_KEY = "I18nCache";
	static public final String CACHE_NAME = "daily";
	static public final String DEFAULT_LANGUAGE = "en";
	static public final String DEFAULT_TRANSLATION = "Translation missing";

	private static I18nCache INSTANCE;

	private Table<String, String, String> cache;
	private Set<String> cacheUsed = new HashSet<String>();

	private I18nCache() {
	}

	public static I18nCache getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new I18nCache();
		}

		return INSTANCE;
	}

	private boolean hasKey(String key, String locale) {
		return getCache().contains(key, locale);
	}

	public boolean hasKey(String key, Locale locale) {
		return hasKey(key, getLocaleFallback(key, locale, false));
	}

	public Map<String, String> getText(String key) {
		return getCache().row(key);
	}

	private String getText(String key, String locale) {
		cacheUsed.add(key);
		return getCache().get(key, locale);
	}

	public String getText(String key, Locale locale) {
		return getText(key, getLocaleFallback(key, locale, true));
	}

	private String getText(String key, String locale, Object... args) {
		if (hasKey(key, locale)) {
			if (args == null || args.length == 0)
				return getText(key, locale);
			else {
				MessageFormat message = new MessageFormat(fixFormatCharacters(getText(key, locale)),
						Strings.parseLocale(locale));
				StringBuffer buffer = new StringBuffer();
				message.format(args, buffer, null);
				return buffer.toString();
			}
		}

		return null;
	}

	public String getText(String key, Locale locale, Object... args) {
		return getText(key, getLocaleFallback(key, locale, true), args);
	}

	/**
	 * Fix characters that cause problems with MessageFormat, such as "'"
	 * 
	 * @param text
	 *            the text to be formatted
	 * @return text formatted to be used in MessageFormat.format
	 */
	private String fixFormatCharacters(String text) {
		return text.replaceAll("'", "''");
	}

	public Table<String, String, String> getCache() {
		if (cache == null) {
			try {
				long startTime = System.currentTimeMillis();
				cache = TreeBasedTable.create();
				Database db = new Database();
				List<BasicDynaBean> messages = db.select(
						"SELECT msgKey, locale, msgValue, lastUsed FROM app_translation", false);
				for (BasicDynaBean message : messages) {
					cache.put(String.valueOf(message.get("msgKey")), String.valueOf(message.get("locale")),
							String.valueOf(message.get("msgValue")));
				}
				long endTime = System.currentTimeMillis();
				System.out.println("Built i18n Cache in " + (endTime - startTime) + "ms");
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return cache;
	}

	public void clear() {
		if (cacheUsed.size() > 0) {
			Database db = new Database();
			String sql = "UPDATE app_translation SET lastUsed = NOW() WHERE msgKey IN ("
					+ Strings.implodeForDB(cacheUsed, ",") + ")";
			try {
				db.execute(sql);
			} catch (SQLException doNothing) {
				throw new RuntimeException("Failed to update app_translation lastUsed on clear.");
			}
		}
		cache = null;
		cacheUsed.clear();
	}

	private String getLocaleFallback(String key, Locale locale, boolean insertMissing) {
		String localeString = locale.toString();
		if (!hasKey(key, localeString)) {
			localeString = locale.getLanguage();
			if (!hasKey(key, localeString)) {
				localeString = DEFAULT_LANGUAGE;
				if (!hasKey(key, localeString)) {
					if (insertMissing) {
						// insert the default msg into the table and the cache
						try {
							Database db = new Database();

							AppTranslation newTranslation = new AppTranslation();
							newTranslation.setKey(key);
							newTranslation.setLocale(localeString);
							newTranslation.setValue(DEFAULT_TRANSLATION);
							newTranslation.setApplicable(true);
							if (newTranslation.isKeyContentDriven())
								newTranslation.setContentDriven(true);

							db.executeInsert(buildInsertStatement(newTranslation));
							cache.put(key, localeString, DEFAULT_TRANSLATION);
						} catch (SQLException e) {
							// In case the translation already existed, it might
							// be best to clear the cache
							clear();
						}
					} else {
						return null;
					}
				}
			}
		}

		return localeString;
	}

	public void saveTranslatableString(String key, TranslatableString value, List<String> requiredLanguages)
			throws SQLException {
		if (value == null)
			return;

		Database db = new Database();
		String sourceLanguage = null;

		if (!requiredLanguages.isEmpty()) {
			sourceLanguage = requiredLanguages.get(0);
		}
		// Make sure we handle clearing the cache across multiple servers
		Iterator<Translation> iterator = value.getTranslations().iterator();
		while (iterator.hasNext()) {
			Translation translationFromCache = iterator.next();

			AppTranslation newTranslation = new AppTranslation();
			newTranslation.setKey(key);
			newTranslation.setLocale(translationFromCache.getLocale());
			newTranslation.setValue(Utilities.escapeQuotes(translationFromCache.getValue()));
			newTranslation.setSourceLanguage(sourceLanguage);
			newTranslation.setQualityRating(TranslationQualityRating.Good);
			newTranslation.setApplicable(true);

			if (requiredLanguages.size() > 0 && !requiredLanguages.contains(newTranslation.getLocale())) {
				newTranslation.setApplicable(false);
			}
			
			if (newTranslation.isKeyContentDriven())
				newTranslation.setContentDriven(true);

			if (translationFromCache.isDelete()) {
				String sql = "DELETE FROM app_translation WHERE msgKey = '" + key + "' AND locale = '"
						+ translationFromCache.getLocale() + "'";
				db.executeUpdate(sql);
				cache.remove(newTranslation.getKey(), newTranslation.getLocale());
				iterator.remove();
			} else if (translationFromCache.isModified()) {
				db.executeUpdate(buildUpdateStatement(newTranslation));
			} else if (translationFromCache.isInsert()) {
				String insert = buildInsertStatement(newTranslation);

				if (insert != null) {
					db.executeInsert(insert);
				}
			}

			updateCacheAndRemoveTranslationFlagsIfNeeded(translationFromCache, newTranslation);
		}

		insertUpdateRequiredLanguages(requiredLanguages, key, sourceLanguage);
	}

	public void removeTranslatableStrings(List<String> keys) throws SQLException {
		if (keys.size() > 0) {
			for (String key : keys) {
				getCache().row(key).clear();
			}

			String sql = "DELETE FROM app_translation WHERE msgKey IN (" + Strings.implodeForDB(keys, ",") + ")";
			Database db = new Database();
			db.executeUpdate(sql);
		}
	}

	private String buildInsertStatement(AppTranslation translationToinsert) {
		if (translationToinsert.isApplicable()) {
			String sourceLanguage = translationToinsert.getSourceLanguage();
			if (Strings.isEmpty(sourceLanguage)) {
				sourceLanguage = "NULL";
			} else {
				sourceLanguage = "'" + sourceLanguage + "'";
			}

			String format = "INSERT INTO app_translation (msgKey, locale, msgValue, qualityRating, sourceLanguage, createdBy, "
					+ "updatedBy, creationDate, updateDate, lastUsed)"
					+ " VALUES ('%s', '%s', '%s', %d, %s, 1, 1, NOW(), NOW(), NOW())";

			return String.format(format, translationToinsert.getKey(), translationToinsert.getLocale(),
					translationToinsert.getValue(), translationToinsert.getQualityRating().ordinal(), sourceLanguage);
		}

		return null;
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

		String format = "UPDATE app_translation " + setClause + " WHERE msgKey = '%s' AND locale = '%s'";
		String translationValueQuotationEscaped = StringUtils.replace(translationToUpdate.getValue(), "'", "''");

		String updateSQL = String.format(format, translationValueQuotationEscaped, translationToUpdate.getKey(),
				translationToUpdate.getLocale());

		return updateSQL;
	}

	private void updateCacheAndRemoveTranslationFlagsIfNeeded(Translation translationFromCache,
			AppTranslation newTranslation) {
		if (translationFromCache.isInsert() || translationFromCache.isModified()) {
			updateCacheWithTranslation(newTranslation);
			translationFromCache.commit();
		}
	}

	private void updateCacheWithTranslation(AppTranslation newTranslation) {
		cache.put(newTranslation.getKey(), newTranslation.getLocale(), newTranslation.getValue());
	}

	private void insertUpdateRequiredLanguages(List<String> requiredLanguages, String key, String source)
			throws SQLException {
		setUnneededLanguagesNotApplicable(requiredLanguages, key, source);
		
		for (String requiredLanguage : requiredLanguages) {
			AppTranslation insertUpdateTranslation = new AppTranslation();
			insertUpdateTranslation.setKey(key);
			insertUpdateTranslation.setLocale(requiredLanguage);
			insertUpdateTranslation.setSourceLanguage(source);
			insertUpdateTranslation.setApplicable(true);

			if (insertUpdateTranslation.isKeyContentDriven())
				insertUpdateTranslation.setContentDriven(true);

			if (hasKey(key, requiredLanguage)) {
				updateRequiredLanguage(requiredLanguage, insertUpdateTranslation);
			} else {
				insertRequiredTranslation(insertUpdateTranslation);
			}

			updateCacheWithTranslation(insertUpdateTranslation);
		}
	}

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
				
				if (unneededTranslation.isKeyContentDriven())
					unneededTranslation.setContentDriven(true);

				updateRequiredLanguage(locale, unneededTranslation);
			}
		}
	}

	private void insertRequiredTranslation(AppTranslation insertUpdateTranslation) throws SQLException {
		insertUpdateTranslation.setValue(DEFAULT_TRANSLATION);
		insertUpdateTranslation.setQualityRating(TranslationQualityRating.Bad);

		Database db = new Database();
		db.executeInsert(buildInsertStatement(insertUpdateTranslation));
	}

	private void updateRequiredLanguage(String requiredLanguage, AppTranslation insertUpdateTranslation)
			throws SQLException {
		String requiredLanguageMsgValue = getText(insertUpdateTranslation.getKey(), requiredLanguage);

		insertUpdateTranslation.setValue(requiredLanguageMsgValue);
		insertUpdateTranslation.setQualityRating(TranslationQualityRating.Questionable);

		Database db = new Database();
		db.executeUpdate(buildUpdateStatement(insertUpdateTranslation));
	}
}
