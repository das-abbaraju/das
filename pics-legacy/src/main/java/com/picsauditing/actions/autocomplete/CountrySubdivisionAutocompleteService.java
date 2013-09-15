package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;

public class CountrySubdivisionAutocompleteService extends AbstractAutocompleteService<CountrySubdivision> {

	private static final int COUNTRY_SUBDIVISION_LENGTH = 5;

	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;

	@Override
	protected Collection<CountrySubdivision> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		Collection<CountrySubdivision> result = new HashSet<CountrySubdivision>();

		// search both iso and translated fields for the 5 letter combinations
		if (search.length() == COUNTRY_SUBDIVISION_LENGTH) {
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findWhere("isoCode = '"
					+ Strings.escapeQuotes(search) + "'");
			result.addAll(countrySubdivisionList);

			countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(CountrySubdivision.class, "%"
					+ Strings.escapeQuotes(search) + "%", RESULT_SET_LIMIT);

			result.addAll(countrySubdivisionList);
		} else { // any more or less characters, then search only through
					// translations
			List<CountrySubdivision> countrySubdivisionList = countrySubdivisionDAO.findByTranslatableField(
					CountrySubdivision.class, "%" + Strings.escapeQuotes(search) + "%", RESULT_SET_LIMIT);

			result.addAll(countrySubdivisionList);
		}

		return result;
	}

	@Override
	protected Object getKey(CountrySubdivision subdivision) {
		return subdivision.getIsoCode();
	}

	@Override
	protected Object getValue(CountrySubdivision subdivision, Permissions permissions) {
		return TranslationServiceFactory.getTranslationService().getText(subdivision.getI18nKey(),
				permissions.getLocale());
	}

	@Override
	protected Collection<CountrySubdivision> getItemsForSearchKey(String searchKey, Permissions permissions) {
		if (Strings.isEmpty(searchKey)) {
			return Collections.emptyList();
		}

		return Arrays.asList(countrySubdivisionDAO.find(searchKey));
	}
}