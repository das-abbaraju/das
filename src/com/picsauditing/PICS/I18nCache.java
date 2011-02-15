package com.picsauditing.PICS;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BasicDynaBean;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.search.Database;

public class I18nCache {

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
		return hasKey(key, locale.toString());
	}

	public String getText(String key, String locale) {
		return getCache().get(key, locale);
	}

	public String getText(String key, Locale locale) {
		return getText(key, locale.toString());
	}

	public String getText(String key, String locale, Object... args) {
		if (hasKey(key, locale))
			return String.format(getText(key, locale), args);
		else
			return null;
	}

	public String getText(String key, Locale locale, Object... args) {
		if (args == null)
			return getText(key, locale);
		return getText(key, locale.toString(), args);
	}

	public Table<String, String, String> getCache() {
		if (cache == null || "1".equals(System.getProperty("pics.debug"))) {
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
}
