package com.picsauditing.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class TranslateJS extends PicsActionSupport {

	private JSONObject translations = new JSONObject();

	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() throws Exception {
		Database db = new Database();
		SelectSQL sql = new SelectSQL("app_translation");
		sql.addField("msgKey");
		sql.addField("msgValue");
		sql.addWhere("msgKey LIKE 'JS.%'");

		// Get a set of Locales such as en, en_US, and en_US_foo
		Set<String> locales = new HashSet<String>();
		locales.add(Locale.ENGLISH.toString());
		Locale current = getLocale();
		locales.add(current.toString());
		locales.add(new Locale(current.getLanguage()).toString());
		if (!Strings.isEmpty(current.getCountry()))
			locales.add(new Locale(current.getLanguage(), current.getCountry()).toString());

		sql.addWhere("locale IN (" + Strings.implodeForDB(locales) + ")");
		// Order in this way fr_CA_Suncor, fr_CA, fr, en
		sql.addOrderBy("CASE locale WHEN 'EN' THEN 1 ELSE 0 END, locale DESC");

		List<BasicDynaBean> messages = db.select(sql.toString(), false);
		for (BasicDynaBean message : messages) {
			String msgKey = message.get("msgKey").toString();
			if (!translations.containsKey(msgKey)) {
				translations.put(msgKey, message.get("msgValue").toString());
			}
		}

		return SUCCESS;
	}

	public JSONObject getTranslations() {
		return translations;
	}
}
