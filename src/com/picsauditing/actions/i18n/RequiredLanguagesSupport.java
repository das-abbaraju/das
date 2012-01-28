package com.picsauditing.actions.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public abstract class RequiredLanguagesSupport extends PicsActionSupport {
	protected List<Locale> availableLocales;
	protected List<Locale> selectedLocales;
	
	private static List<Locale> getDefaultLocales() {
		List<Locale> locales = new ArrayList<Locale>();
		// locales.add(new Locale("ar"));
		// locales.add(new Locale("zh", "CN"));
		// locales.add(new Locale("zh", "TW"));
		// locales.add(new Locale("nl"));
		locales.add(new Locale("en"));
		locales.add(new Locale("en", "AU"));
		locales.add(new Locale("en", "CA"));
		locales.add(new Locale("en", "UK"));
		locales.add(new Locale("en", "US"));
		locales.add(new Locale("en", "ZA"));
		locales.add(new Locale("fr"));
		locales.add(new Locale("fr", "CA"));
		locales.add(new Locale("fr", "FR"));
		// locales.add(new Locale("de"));
		// locales.add(new Locale("ja"));
		// locales.add(new Locale("pt"));
		locales.add(new Locale("es"));
		locales.add(new Locale("es", "MX"));
		locales.add(new Locale("es", "ES"));
		
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
	
	abstract protected void fillSelectedLocales();
}
