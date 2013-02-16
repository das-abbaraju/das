package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;

/**
 * TODO: Find out if this class is even working correctly.
 */
public class LocationAutocompleteService extends AbstractAutocompleteService<Autocompleteable> {

	private static final int ISO_COUNTRY_CODE_LENGTH = 2;

	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	protected CountryDAO countryDAO;

	@Override
	protected Collection<Autocompleteable> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (search.length() == ISO_COUNTRY_CODE_LENGTH) { // search both iso and translated fields for the 2 letter combinations
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode LIKE '%"
					+ Strings.escapeQuotes(search) + "'");
			List<Country> countryList = countryDAO.findWhere("isoCode LIKE '%" + Strings.escapeQuotes(search) + "'");
			result.addAll(countrySubdivisionList);
			result.addAll(countryList);

			countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class, "%"
					+ Strings.escapeQuotes(search) + "%");
			countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(search) + "%");

			result.addAll(countrySubdivisionList);
			result.addAll(countryList);
		} else { // any more or less characters, then search only through translations
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(
					CountrySubdivision.class, "%" + Strings.escapeQuotes(search) + "%");
			List<Country> countryList = countryDAO.findByTranslatableField(Country.class,
					"%" + Strings.escapeQuotes(search) + "%");

			result.addAll(countrySubdivisionList);
			result.addAll(countryList);

		}

		return result;
	}

	@Override
	protected Object getKey(Autocompleteable item) {
		return item.getAutocompleteItem();
	}

	@Override
	protected Object getValue(Autocompleteable item, Permissions permissions) {
		return item.getAutocompleteValue();
	}

	@Override
	protected Collection<Autocompleteable> getItemsForSearchKey(String searchKey, Permissions permissions) {
		return getItemsForSearch(searchKey, permissions);
	}
}