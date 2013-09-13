package com.picsauditing.actions.autocomplete;

import java.util.*;

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

		Collection<Autocompleteable> result = new HashSet<>();
		result.addAll(searchCountrySubdivision(search));
		result.addAll(searchCountry(search));

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

	private List<Country> searchCountry(String search) {
		List<Country> matchingCountries = new ArrayList<>();
		// search both iso and translated fields for the 2 letter combinations
		if (search.length() == ISO_COUNTRY_CODE_LENGTH) {
			matchingCountries.addAll(countryDAO.findWhere("isoCode LIKE '%" + Strings.escapeQuotes(search) + "'"));
		}

		matchingCountries.addAll(searchCountryByTranslation(search));

		return matchingCountries;
	}

	private List<CountrySubdivision> searchCountrySubdivision(String search) {
		List<CountrySubdivision> matchingSubdivisions = new ArrayList<>();
		// search both iso and translated fields for the 2 letter combinations
		if (search.length() == ISO_COUNTRY_CODE_LENGTH) {
			matchingSubdivisions.addAll(countrySubdivisionDAO.findWhere("isoCode LIKE '%"
					+ Strings.escapeQuotes(search) + "'"));
		}

		matchingSubdivisions.addAll(searchCountrySubdivisionByTranslation(search));

		Iterator<CountrySubdivision> iterator = matchingSubdivisions.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getCountry().isHasCountrySubdivisions()) {
				iterator.remove();
			}
		}

		return matchingSubdivisions;
	}

	private List<Country> searchCountryByTranslation(String search) {
		return countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(search) + "%");
	}

	private List<CountrySubdivision> searchCountrySubdivisionByTranslation(String search) {
		return countrySubdivisionDAO.findByTranslatableField(
				CountrySubdivision.class, "%" + Strings.escapeQuotes(search) + "%");
	}
}