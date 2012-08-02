package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public class LocationAutocomplete extends AutocompleteActionSupport<Autocompleteable> {

	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	protected CountryDAO countryDAO;

	@Override
	protected Collection<Autocompleteable> getItems() {
		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (q.length() == 2) {
					// search both iso and translated fields for the 2 letter combinations
					List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode LIKE '%" + Strings.escapeQuotes(q) + "'");
					List<Country> countryList = countryDAO.findWhere("isoCode LIKE '%" + Strings.escapeQuotes(q) + "'");
					result.addAll(countrySubdivisionList);
					result.addAll(countryList);
					
					countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class, "%" + Strings.escapeQuotes(q) + "%",
							getLocale());
					countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(q)
							+ "%", getLocale());

					result.addAll(countrySubdivisionList);
					result.addAll(countryList);
					return result;
				} else {
					// any more or less characters, then search only through translations
					List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class,
							"%" + Strings.escapeQuotes(q) + "%", getLocale());
					List<Country> countryList = countryDAO.findByTranslatableField(Country.class,
							"%" + Strings.escapeQuotes(q) + "%", getLocale());

					result.addAll(countrySubdivisionList);
					result.addAll(countryList);

					return result;
				}
			}
		} else if (itemKeys.length > 0) {
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode IN (" + Strings.implodeForDB(itemKeys, ",") + ")");
			List<Country> countryList = countryDAO
					.findWhere("isoCode IN (" + Strings.implodeForDB(itemKeys, ",") + ")");
			result.addAll(countrySubdivisionList);
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