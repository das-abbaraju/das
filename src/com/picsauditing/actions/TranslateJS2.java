package com.picsauditing.actions;

import java.util.*;

import com.picsauditing.toggle.FeatureToggle;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class TranslateJS2 extends PicsActionSupport {
    private static final long serialVersionUID = 688114963286561699L;

    private JSONObject translations = new JSONObject();

    @Autowired
    private FeatureToggle featureToggleChecker;

	@Anonymous
	public String execute() throws Exception {
		buildTranslationJsonResponse();
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void buildTranslationJsonResponse() {
		List<Map<String, String>> translationsForJS = getTranslationService().getTranslationsForJS(
				URLUtils.getActionNameFromRequest(getRequest()), URLUtils.getActionMethodNameFromRequest(getRequest()),
				buildLocales());

		if (CollectionUtils.isEmpty(translationsForJS)) {
			return;
		}

		for (Map<String, String> translationMap : translationsForJS) {
			for (Map.Entry<String, String> translation : translationMap.entrySet()) {
				String key = translation.getKey();
				if (!translations.containsKey(key)) {
					translations.put(key, translation.getValue());
				}
			}
		}
	}

	private Set<String> buildLocales() {
        Set<String> locales = new HashSet<>();
        Locale current = getLocale();
        if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER)) {
            locales.add(current.toString());
        } else {
            locales.add(Locale.ENGLISH.toString());
            locales.add(current.toString());
            locales.add(new Locale(current.getLanguage()).toString());
            if (!Strings.isEmpty(current.getCountry())) {
                locales.add(new Locale(current.getLanguage(), current.getCountry()).toString());
            }
        }
		return locales;
	}

	public JSONObject getTranslations() {
		return translations;
	}

	private static TranslationService getTranslationService() {
		return TranslationServiceFactory.getTranslationService();
	}
}
