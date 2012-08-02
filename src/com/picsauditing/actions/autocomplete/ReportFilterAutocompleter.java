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
	CountrySubdivisionAutocompleteService countrySubdivisionAutocompleteService;
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
			return countryAutocompleteService.tokenJson(searchQuery);
		case Subdivision:
			return countrySubdivisionAutocompleteService.tokenJson(searchQuery);
		case Trade:
			return tradeAutocompleteService.tokenJson(searchQuery);
		case Operator:
			return operatorAutocompleteService.tokenJson(searchQuery, permissions);
		case OperatorTag:
			return operatorTagAutocompleteService.tokenJson(searchQuery);
		case Contractor:
			return contractorAutocompleteService.tokenJson(searchQuery);
		case AuditQuestion:
			return auditQuestionAutocompleteService.tokenJson(searchQuery);
		case AuditType:
			return auditTypeAutocompleteService.tokenJson(searchQuery);
		case AuditCategory:
			return auditCategoryAuditcompleteService.tokenJson(searchQuery);
		case OptionGroup:
			return optionGroupAutocompleteService.tokenJson(searchQuery);
		case OptionValue:
			return optionValueAutocompleteService.tokenJson(searchQuery);
		default:
			return new JSONObject();
		}
	}
}
