package com.picsauditing.actions;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Anonymous;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TranslateJS2 extends PicsActionSupport {

	@Autowired
	private FeatureToggle featureToggle;

	private JSONObject translations = new JSONObject();

	private static final long serialVersionUID = 688114963286561699L;

	// this is for testing
	private static I18nCache i18nCache;

	@Anonymous
	public String execute() throws Exception {
	    buildTranslationJsonResponse();
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void buildTranslationJsonResponse() {
		List<Map<String, String>> translationsForJS = getI18nCache().getTranslationsForJS(
				URLUtils.getActionNameFromRequest(getRequest()),
				URLUtils.getActionMethodNameFromRequest(getRequest()), buildLocales());

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
		Set<String> locales = new HashSet<String>();
		locales.add(Locale.ENGLISH.toString());
		Locale current = getLocale();
		locales.add(current.toString());
		locales.add(new Locale(current.getLanguage()).toString());
		if (!Strings.isEmpty(current.getCountry())) {
			locales.add(new Locale(current.getLanguage(), current.getCountry()).toString());
		}

		return locales;
	}

	public JSONObject getTranslations() {
		return translations;
	}

	private static I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}
}
