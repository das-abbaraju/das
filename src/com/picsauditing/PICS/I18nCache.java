package com.picsauditing.PICS;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BasicDynaBean;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.search.Database;

public class I18nCache {

	public final String DEFAULT_LANGUAGE = "en";
	public final String DEFAULT_TRANSLATION = "Translation missing";

	private final static I18nCache INSTANCE = new I18nCache();

	private Table<String, String, String> cache;

	private I18nCache() {

	}

	public static I18nCache getInstance() {
		return INSTANCE;
	}

	public boolean hasKey(String key, String locale) {
		return getCache().contains(key, locale);
	}

	public boolean hasKey(String key, Locale locale) {
		return hasKey(key, getLocaleFallback(key, locale));
	}

	public String getText(String key, String locale) {
		return getCache().get(key, locale);
	}

	public String getText(String key, Locale locale) {
		return getText(key, getLocaleFallback(key, locale));
	}

	public String getText(String key, String locale, Object... args) {
		if (hasKey(key, locale))
			return MessageFormat.format(getText(key, locale), args);
		else
			return null;
	}

	public String getText(String key, Locale locale, Object... args) {
		return getText(key, getLocaleFallback(key, locale), args);
	}

	public Table<String, String, String> getCache() {
		if (cache == null || true) {
			try {
				cache = TreeBasedTable.create();
				Database db = new Database();
				List<BasicDynaBean> messages = db.select("SELECT msgKey, locale, msgValue FROM app_translation", false);
				for (BasicDynaBean message : messages) {
					cache.put(String.valueOf(message.get("msgKey")), String.valueOf(message.get("locale")),
							String.valueOf(message.get("msgValue")));
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return cache;
	}

	public void clear() {
		cache = null;
	}

	public String getLocaleFallback(String key, Locale locale) {
		String localeString = locale.toString();
		if (!hasKey(key, localeString)) {
			localeString = locale.getLanguage();
			if (!hasKey(key, localeString)) {
				localeString = DEFAULT_LANGUAGE;
				if (!hasKey(key, localeString)) {
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
						// In case the translation already existed, it might be
						// best to clear the cache
						clear();
					}
				}
			}
		}

		return localeString;
	}
}
