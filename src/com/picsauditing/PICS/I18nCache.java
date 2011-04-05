package com.picsauditing.PICS;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.TranslatableString;
import com.picsauditing.jpa.entities.TranslatableString.Translation;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class I18nCache {

	static public final String DEFAULT_LANGUAGE = "en";
	static public final String DEFAULT_TRANSLATION = "Translation missing";

	private final static I18nCache INSTANCE = new I18nCache();

	private Table<String, String, String> cache;

	private I18nCache() {
	}

	public static I18nCache getInstance() {
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
		return getCache().get(key, locale);
	}

	public String getText(String key, Locale locale) {
		return getText(key, getLocaleFallback(key, locale, true));
	}

	private String getText(String key, String locale, Object... args) {
		if (hasKey(key, locale)) {
			if (args == null || args.length == 0)
				return getText(key, locale);
			else
				return MessageFormat.format(fixFormatCharacters(getText(key, locale)), args);
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
				List<BasicDynaBean> messages = db.select("SELECT msgKey, locale, msgValue FROM app_translation", false);
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
		cache = null;
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
							String sql = "INSERT INTO app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed)"
									+ " VALUES ('"
									+ key
									+ "', '"
									+ localeString
									+ "', '"
									+ DEFAULT_TRANSLATION
									+ "', 1, 1, NOW(), NOW(), NOW())";
							db.executeInsert(sql);
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

	public void saveTranslatableString(String key, TranslatableString value) throws SQLException {
		if (value == null)
			return;
		Database db = new Database();
		// Make sure we handle clearing the cache across multiple servers
		for (Translation translation : value.getTranslations()) {
			String locale = translation.getLocale();
			String newValue = Utilities.escapeQuotes(translation.getValue());
			if (translation.isDelete()) {
				String sql = "DELETE FROM app_translation WHERE msgKey = '" + key + "' AND locale = '" + locale + "'";
				db.executeUpdate(sql);
				cache.remove(key, locale);
				translation.commit();
			} else if (translation.isModified()) {
				String sql = "UPDATE app_translation SET value = '" + newValue + "' WHERE msgKey = '" + key
						+ "' AND locale = '" + locale + "' AND updateDate = NOW()";
				db.executeUpdate(sql);
				cache.put(key, locale, translation.getValue());
				translation.commit();
			} else if (translation.isInsert()) {
				String sql = "INSERT INTO app_translation (msgKey, locale, msgValue, createdBy, updatedBy, creationDate, updateDate, lastUsed)"
						+ " VALUES ('" + key + "', '" + locale + "', '" + newValue + "', 1, 1, NOW(), NOW(), NOW())";
				db.executeInsert(sql);
				cache.put(key, locale, translation.getValue());
				translation.commit();
			}
		}
	}

}
