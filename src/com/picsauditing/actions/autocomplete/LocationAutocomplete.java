package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class LocationAutocomplete extends AutocompleteActionSupport<Autocompleteable> {

	@Autowired
	protected StateDAO stateDAO;
	@Autowired
	protected CountryDAO countryDAO;

	@Override
	protected Collection<Autocompleteable> getItems() {
		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (q.length() == 2) {
					// search both iso and translated fields for the 2 letter combinations
					List<State> stateList = stateDAO.findWhere("isoCode = '" + Strings.escapeQuotes(q) + "'");
					List<Country> countryList = countryDAO.findWhere("isoCode = '" + Strings.escapeQuotes(q) + "'");
					result.addAll(stateList);
					result.addAll(countryList);
					
					stateList = stateDAO.findByTranslatableField(State.class, "%" + Strings.escapeQuotes(q) + "%",
							getLocale());
					countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(q)
							+ "%", getLocale());

					result.addAll(stateList);
					result.addAll(countryList);
					return result;
				} else {
					// any more or less characters, then search only through translations
					List<State> stateList = stateDAO.findByTranslatableField(State.class,
							"%" + Strings.escapeQuotes(q) + "%", getLocale());
					List<Country> countryList = countryDAO.findByTranslatableField(Country.class,
							"%" + Strings.escapeQuotes(q) + "%", getLocale());

					result.addAll(stateList);
					result.addAll(countryList);

					return result;
				}
			}
		} else if (itemKeys.length > 0) {
			List<State> stateList = stateDAO.findWhere("isoCode IN (" + Strings.implodeForDB(itemKeys, ",") + ")");
			List<Country> countryList = countryDAO
					.findWhere("isoCode IN (" + Strings.implodeForDB(itemKeys, ",") + ")");
			result.addAll(stateList);
			result.addAll(countryList);
			return result;
		}
		return Collections.emptyList();
	}

	class CountryAutocomplete extends Country {
		Autocompleteable a;

		@Override
		public String getAutocompleteResult() {
			return super.getAutocompleteResult() + "X";
		}
	}
}