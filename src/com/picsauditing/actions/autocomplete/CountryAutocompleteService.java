package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CountryAutocompleteService extends AutocompleteService<Autocompleteable> {
	@Autowired
	protected CountryDAO countryDAO;

	@Override
	protected Collection<Autocompleteable> getItems(String q) {
		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (!Strings.isEmpty(q)) {
			if (q.length() == 2) {
				// search both iso and translated fields for the 2 letter
				// combinations
				List<Country> countryList = countryDAO.findWhere("isoCode = '" + Strings.escapeQuotes(q) + "'");
				result.addAll(countryList);

				countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(q) + "%");

				result.addAll(countryList);
				return result;
			} else {
				// any more or less characters, then search only through
				// translations
				List<Country> countryList = countryDAO.findByTranslatableField(Country.class, "%"
						+ Strings.escapeQuotes(q) + "%");

				result.addAll(countryList);

				return result;
			}
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