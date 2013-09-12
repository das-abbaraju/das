package com.picsauditing.actions.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseHistoryRequiringLanguages;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.RequiresLanguages;

@SuppressWarnings("serial")
public abstract class RequiredLanguagesSupport extends PicsActionSupport {
	public static String[] DEFAULT_LOCALES = new String[] { "en", "fr", "es", "de", "nl", "fi", "sv", "zh", "pt", "no" };
	protected List<Locale> availableLocales;
	protected List<Locale> selectedLocales;

	private static List<Locale> getDefaultLocales() {
		List<Locale> locales = new ArrayList<Locale>();

		for (String locale : DEFAULT_LOCALES) {
			locales.add(new Locale(locale));
		}

		return locales;
	}

	public List<Locale> getAvailableLocales() {
		if (availableLocales == null) {
			availableLocales = getDefaultLocales();
			availableLocales.removeAll(getSelectedLocales());
		}

		return availableLocales;
	}

	public List<Locale> getSelectedLocales() {
		if (selectedLocales == null) {
			selectedLocales = new ArrayList<Locale>();

			fillSelectedLocales();
		}

		return selectedLocales;
	}

	public List<Locale> getAvailableLocalesFrom(RequiresLanguages entity) {
		availableLocales = new ArrayList<Locale>();

		for (String language : entity.getLanguages()) {
			availableLocales.add(new Locale(language));
		}
		availableLocales.removeAll(getSelectedLocales());

		return availableLocales;
	}

	public void addUserPreferredLanguage(RequiresLanguages entity) {
		List<String> preferredLanguage = new ArrayList<>();
		preferredLanguage.add(permissions.getLocale().getLanguage());
		entity.setLanguages(preferredLanguage);
	}

	abstract protected void fillSelectedLocales();
}