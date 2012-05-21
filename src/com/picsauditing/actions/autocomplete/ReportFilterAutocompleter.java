package com.picsauditing.actions.autocomplete;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.AutocompleteType;

public final class ReportFilterAutocompleter {
	@Autowired
	OperatorAutocompleteService operatorAutocompleteService;
	@Autowired
	CountryAutocompleteService countryAutocompleteService;
	@Autowired
	StateAutocompleteService stateAutocompleteService;
	@Autowired
	OperatorTagAutocompleteService operatorTagAutocompleteService;
	@Autowired
	TradeAutocompleteService tradeAutocompleteService;
	@Autowired
	ContractorAutocompleteService contractorAutocompleteService;
	@Autowired
	AuditQuestionAutocompleteService auditQuestionAutocompleteService;
	@Autowired
	AuditTypeAutocompleteService auditTypeAutocompleteService;
	@Autowired
	AuditCategoryAutocompleteService auditCategoryAuditcompleteService;
	@Autowired
	OptionGroupAutocompleteService optionGroupAutocompleteService;
	@Autowired
	OptionValueAutocompleteService optionValueAutocompleteService;
	
	
	
	public JSONObject getFilterAutocompleteResultsJSON(AutocompleteType autocompleteFilter, String searchQuery, Permissions permissions) {
		switch (autocompleteFilter) {
		case Country:
			return countryAutocompleteService.json(searchQuery);
		case State:
			return stateAutocompleteService.json(searchQuery);
		case Trade:
			return tradeAutocompleteService.json(searchQuery);
		case Operator:
			return operatorAutocompleteService.json(searchQuery, permissions);
		case OperatorTag:
			return operatorTagAutocompleteService.json(searchQuery);
		case Contractor:
			return contractorAutocompleteService.json(searchQuery);
		case AuditQuestion:
			return auditQuestionAutocompleteService.json(searchQuery);
		case AuditType:
			return auditTypeAutocompleteService.json(searchQuery);
		case AuditCategory:
			return auditCategoryAuditcompleteService.json(searchQuery);
		case OptionGroup:
			return optionGroupAutocompleteService.json(searchQuery);
		case OptionValue:
			return optionValueAutocompleteService.json(searchQuery);
		default:
			return new JSONObject();
		}
	}
}
