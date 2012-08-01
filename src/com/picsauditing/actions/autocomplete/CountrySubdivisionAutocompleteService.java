package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;

public class CountrySubdivisionAutocompleteService extends AutocompleteService<CountrySubdivision> {
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;

	@Override
	protected Collection<CountrySubdivision> getItems(String q) {
		Collection<CountrySubdivision> result = new HashSet<CountrySubdivision>();
		if (!Strings.isEmpty(q)) {
			if (q.length() == 2) {
				// search both iso and translated fields for the 2 letter
				// combinations
				List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode = '" + Strings.escapeQuotes(q) + "'");
				result.addAll(countrySubdivisionList);

				countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class, "%" + Strings.escapeQuotes(q) + "%");

				result.addAll(countrySubdivisionList);
				return result;
			} else {
				// any more or less characters, then search only through
				// translations
				List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class, "%" + Strings.escapeQuotes(q)
						+ "%");

				result.addAll(countrySubdivisionList);

				return result;
			}
		}

		return Collections.emptyList();
	}
}