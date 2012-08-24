package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CountryAutocompleteService extends AutocompleteService<Country> {
	
	@Autowired
	protected CountryDAO countryDAO;
	
	// Expected ISO Country Code list is "AF,AD,AM" or just one ISO Country Code "US"
	private static final Pattern ISO_CODE_REGULAR_EXPRESSION = Pattern.compile("([a-zA-Z]{2}\\,)*([a-zA-Z]{2})");

	/**
	 * If the query String is one or more ISO Country codes, then a search will be done using the list
	 * of Country ISO Codes against the Country table and another query will be run against the Country Table
	 * with a join against the translations.
	 * 
	 * If the query String is not a set of ISO Codes, then it is assumed to be a Country name and the Country 
	 * and translations table will be queries for that.
	 * 
	 * The returned Collection will be a unique set of Countries.
	 */
	@Override
	protected Collection<Country> getItems(String queryString) {
		if (Strings.isEmpty(queryString)) {
			return Collections.emptyList();
		}
		
		Collection<Country> result = new HashSet<Country>();		
		
		if (queryContainsIsoCodes(queryString)) { 
			// no need to escape string because it will fail Regex check
			result.addAll(countryDAO.findWhere("isoCode IN ('" + queryString + "')"));
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + queryString + "%"));
		} else {
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(queryString) + "%"));
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

	class CountryAutocomplete extends Country {
		Autocompleteable a;

		@Override
		public String getAutocompleteResult() {
			return super.getAutocompleteResult() + "X";
		}
	}
}