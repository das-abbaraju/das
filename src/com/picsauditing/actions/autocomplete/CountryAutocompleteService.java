package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CountryAutocompleteService extends AutocompleteService<Country> {
	@Autowired
	protected CountryDAO countryDAO;

	@Override
	protected Collection<Country> getItems(String q) {
		Collection<Country> result = new HashSet<Country>();
		if (!Strings.isEmpty(q)) {
			if (queryContainsIsoCodes(q)) {
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
	
	private static boolean queryContainsIsoCodes(String query) {
		if (Strings.isEmpty(query)) {
			return false;
		}
		
		String[] parsedIsoCodes = query.split(",");
		if (ArrayUtils.isNotEmpty(parsedIsoCodes)) {
			for (String isoCode : parsedIsoCodes) {
				if (!Strings.isEmpty(isoCode) && isoCode.length() != 2) {
					return false;
				}
			}
		}
		
		return true;
	}

	class CountryAutocomplete extends Country {
		Autocompleteable a;

		@Override
		public String getAutocompleteResult() {
			return super.getAutocompleteResult() + "X";
		}
	}
}