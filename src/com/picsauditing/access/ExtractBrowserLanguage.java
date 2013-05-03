package com.picsauditing.access;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class ExtractBrowserLanguage {
	public static final String ACCEPT_LANGUAGE = "accept-language";

	private HttpServletRequest httpServletRequest;
	private Collection<Language> stableLanguages;
	private String browserLanguage;
	private String browserDialect;

	public ExtractBrowserLanguage(HttpServletRequest httpServletRequest, Collection<Language> stableLanguages) {
		this.httpServletRequest = httpServletRequest;
		this.stableLanguages = stableLanguages;

		parseLanguageFromHeaders();
	}

	public String getBrowserLanguage() {
		if (browserLanguage == null) {
			browserLanguage = Locale.UK.getLanguage();
		}

		return browserLanguage;
	}

	public String getBrowserDialect() {
		if (Strings.isNotEmpty(browserDialect)) {
			return browserDialect;
		}

		return null;
	}

	private void parseLanguageFromHeaders() {
		Enumeration<String> values = httpServletRequest.getHeaders(ACCEPT_LANGUAGE);

		if (values != null && values.hasMoreElements()) {
			String acceptLanguages = values.nextElement();
			String firstLanguage = acceptLanguages.split(",")[0];
			String[] firstLanguageComponents = extractLanguageAndDialect(firstLanguage);

			if (stableLanguages == null) {
				stableLanguages = new ArrayList<>();
			}

			for (Language stableLanguage : stableLanguages) {
				if (stableLanguage.getLanguage().equals(firstLanguageComponents[0])) {
					browserLanguage = firstLanguageComponents[0];

					if (firstLanguageComponents.length > 1) {
						browserDialect = firstLanguageComponents[1];
					}
				}
			}
		}
	}

	private String[] extractLanguageAndDialect(String firstLanguage) {
		String[] firstLanguageComponents = firstLanguage.split("_");

		if (firstLanguage.contains("-")) {
			firstLanguageComponents = firstLanguage.split("-");
		}

		return firstLanguageComponents;
	}

	public Locale getBrowserLocale() {
		if (getBrowserDialect() != null) {
			return new Locale(getBrowserLanguage(), getBrowserDialect());
		}

		return new Locale(getBrowserLanguage());
	}
}
