package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

public class CountryAutocompleteService extends AbstractAutocompleteService<Country> {

	@Autowired
	protected CountryDAO countryDAO;

	// Expected ISO Country Code list is "AF,AD,AM" or just one ISO Country Code
	// "US"
	private static final Pattern ISO_CODE_REGULAR_EXPRESSION = Pattern.compile("([a-zA-Z]{2}\\,)*([a-zA-Z]{2})");

	/**
	 * If the query String is one or more ISO Country codes, then a search will
	 * be done using the list of Country ISO Codes against the Country table and
	 * another query will be run against the Country Table with a join against
	 * the translations.
	 * 
	 * If the query String is not a set of ISO Codes, then it is assumed to be a
	 * Country name and the Country and translations table will be queries for
	 * that.
	 * 
	 * The returned Collection will be a unique set of Countries.
	 */
	@Override
	protected Collection<Country> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		Collection<Country> result = new HashSet<Country>();

		if (queryContainsIsoCodes(search)) {
			// no need to escape string because it will fail Regex check
			result.addAll(countryDAO.findWhere("isoCode IN ('" + search + "')"));
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + search + "%"));
		} else {
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(search)
					+ "%"));
		}

		return result;
	}

	private static boolean queryContainsIsoCodes(String query) {
		if (Strings.isEmpty(query)) {
			return false;
		}

		Matcher matcher = ISO_CODE_REGULAR_EXPRESSION.matcher(query);
		return matcher.matches();
	}

	@Override
	protected Object getKey(Country country) {
		return country.getIsoCode();
	}

	@Override
	protected Object getValue(Country country, Permissions permissions) {
		return I18nCache.getInstance().getText(country.getI18nKey(), permissions.getLocale());
	}
}