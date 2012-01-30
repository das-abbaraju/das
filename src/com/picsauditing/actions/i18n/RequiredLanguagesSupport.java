package com.picsauditing.actions.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTableRequiringLanguages;

@SuppressWarnings("serial")
public abstract class RequiredLanguagesSupport extends PicsActionSupport {
	protected List<Locale> availableLocales;
	protected List<Locale> selectedLocales;
	
	private static List<Locale> getDefaultLocales() {
		List<Locale> locales = new ArrayList<Locale>();
		locales.add(new Locale("en"));
		locales.add(new Locale("fr"));
		locales.add(new Locale("es"));
		
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
	
	public List<Locale> getSelectedLocalesFrom(BaseTableRequiringLanguages entity) {
		selectedLocales = new ArrayList<Locale>();
		
		for (String language : entity.getLanguages()) {
			selectedLocales.add(new Locale(language));
		}
		
		return selectedLocales;
	}
	
	abstract protected void fillSelectedLocales();
}
