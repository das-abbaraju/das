package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.*;

import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.toggle.FeatureToggle;
import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class TranslateJS extends PicsActionSupport {

    @Autowired
    private FeatureToggle featureToggleChecker;

    private TranslationService translationService = TranslationServiceFactory.getTranslationService();
	private JSONObject translations = new JSONObject();

	@SuppressWarnings("unchecked")
	@Anonymous
	public String execute() throws Exception {
        if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)) {
            Map<String,String> jsTranslations = translationService.getTextLike("JS", locale());
            for(String key : jsTranslations.keySet()) {
                translations.put(key, jsTranslations.get(key));
            }
        } else {
            populateTranslationsFromOldSchema();
        }
		return SUCCESS;
	}

    private String locale() {
        Locale current = getLocale();
        if (current == null) {
            return Locale.ENGLISH.toString();
        } else {
            return current.toString();
        }
    }

    private void populateTranslationsFromOldSchema() throws SQLException {
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
        if (!Strings.isEmpty(current.getCountry())) {
            locales.add(new Locale(current.getLanguage(), current.getCountry()).toString());
        }

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
    }

    public JSONObject getTranslations() {
		return translations;
	}
}
