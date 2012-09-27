package com.picsauditing.actions.autocomplete;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.AutocompleteType;
import com.picsauditing.util.SpringUtils;

public final class ReportFilterAutocompleterFactory {
	
	private static final Map<AutocompleteType, AbstractAutocompleteService<?>> availableServices = Collections.unmodifiableMap(populateMap());
	
	// TODO: This should really be done through Spring injection/mapping in Spring Application Context
	private static Map<AutocompleteType, AbstractAutocompleteService<?>> populateMap() {
		Map<AutocompleteType, AbstractAutocompleteService<?>> serviceImplementations = new HashMap<AutocompleteType, AbstractAutocompleteService<?>>();
		serviceImplementations.put(AutocompleteType.Country, (AbstractAutocompleteService<?>) SpringUtils.getBean("CountryAutocompleteService"));
		serviceImplementations.put(AutocompleteType.Subdivision, (AbstractAutocompleteService<?>) SpringUtils.getBean("CountrySubdivisionAutocompleteService"));
		serviceImplementations.put(AutocompleteType.Trade, (AbstractAutocompleteService<?>) SpringUtils.getBean("TradeAutocompleteService"));
		serviceImplementations.put(AutocompleteType.Operator, (AbstractAutocompleteService<?>) SpringUtils.getBean("OperatorAutocompleteService"));
		serviceImplementations.put(AutocompleteType.OperatorTag, (AbstractAutocompleteService<?>) SpringUtils.getBean("OperatorTagAutocompleteService"));
		serviceImplementations.put(AutocompleteType.Contractor, (AbstractAutocompleteService<?>) SpringUtils.getBean("ContractorAutocompleteService"));
		serviceImplementations.put(AutocompleteType.AuditQuestion, (AbstractAutocompleteService<?>) SpringUtils.getBean("AuditQuestionAutocompleteService"));
		serviceImplementations.put(AutocompleteType.AuditType, (AbstractAutocompleteService<?>) SpringUtils.getBean("AuditTypeAutocompleteService"));
		serviceImplementations.put(AutocompleteType.AuditCategory, (AbstractAutocompleteService<?>) SpringUtils.getBean("AuditCategoryAutocompleteService"));
		serviceImplementations.put(AutocompleteType.OptionGroup, (AbstractAutocompleteService<?>) SpringUtils.getBean("OptionGroupAutocompleteService"));
		serviceImplementations.put(AutocompleteType.OptionValue, (AbstractAutocompleteService<?>) SpringUtils.getBean("OptionValueAutocompleteService"));
		
		return serviceImplementations;
	}
	
	public JSONObject getFilterAutocompleteResultsJSON(AutocompleteType autocompleteFilter, String search, Permissions permissions) {
		if (!availableServices.containsKey(autocompleteFilter)) {
			throw new IllegalArgumentException("Invalid autocomplete filter type = " + autocompleteFilter);
		}
		
		return availableServices.get(autocompleteFilter).tokenJson(search, permissions);
	}
}
