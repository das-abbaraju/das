package com.picsauditing.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;

public class TranslateJS2 extends PicsActionSupport {

	private JSONObject translations = new JSONObject();

	private static final long serialVersionUID = 688114963286561699L;

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

	private static TranslationService getTranslationService() {
		return TranslationServiceFactory.getTranslationService();
	}
}
