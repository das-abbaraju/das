package com.picsauditing.actions.autocomplete;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public class LocationAutocomplete extends AutocompleteActionSupport<Autocompleteable> {

	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	protected CountryDAO countryDAO;
	@Autowired
	protected LocationAutocompleteService locationAutocompleteService;

	@Override
	protected Collection<Autocompleteable> getItems() {
		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				return locationAutocompleteService.getItemsForSearch(q, permissions);
			}
		} else if (itemKeys.length > 0) {
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode IN (" +
					Strings.implodeForDB(itemKeys, ",") + ") AND countryCode IN (" + Strings.implodeForDB(Country
					.COUNTRIES_WITH_SUBDIVISIONS, ",") + ")");
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