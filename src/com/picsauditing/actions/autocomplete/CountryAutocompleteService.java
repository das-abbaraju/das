package com.picsauditing.actions.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.xalan.xsltc.runtime.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CountryAutocompleteService extends AutocompleteService<Country> {
	
	@Autowired
	protected CountryDAO countryDAO;
	
	private static final int ISO_CODE_LENGTH = 2;

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
		String parsedIsoCodes = parseIsoCodeList(queryString);
		if (!Strings.isEmpty(parsedIsoCodes)) {
//			List<Country> countryList = countryDAO.findWhere("isoCode IN ("	+ parsedIsoCodes + ")");
			result.addAll(countryDAO.findWhere("isoCode IN (" + parsedIsoCodes + ")"));

//			countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(queryString) + "%");
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(queryString) + "%"));
		} else {
//			List<Country> countryList = countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(queryString) + "%");
			result.addAll(countryDAO.findByTranslatableField(Country.class, "%" + Strings.escapeQuotes(queryString) + "%"));
		}

		return result;
	}
	
	// TODO: this method is doing 2 things, so it needs to be refactored 
	private static String parseIsoCodeList(String query) {
		if (Strings.isEmpty(query)) {
			return Strings.EMPTY_STRING;
		}
		
		List<String> isoCodes = new ArrayList<String>();
		String[] parsedIsoCodes = query.split(",");
		if (ArrayUtils.isNotEmpty(parsedIsoCodes)) {
			for (String isoCode : parsedIsoCodes) {				
				if (Strings.isEmpty(isoCode) || isoCode.length() != ISO_CODE_LENGTH) {
					return Strings.EMPTY_STRING; // exit early because there are invalid ISO Codes in this list
				}
				
				isoCodes.add(isoCode);
			}
		}
		
		return Strings.implodeForDB(isoCodes, ","); 
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