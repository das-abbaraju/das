package com.picsauditing.actions.autocomplete;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.AutocompleteType;

public final class ReportFilterAutocompleter {
	@Autowired
	OperatorAutocompleteService clientSiteAutocompleteService;
	@Autowired
	CountryAutocompleteService countryAutocompleteService;
	@Autowired
	StateAutocompleteService stateAutocompleteService;
	@Autowired
	OperatorTagAutocompleteService clientSiteTagAutocompleteService;
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
		case State:
			return stateAutocompleteService.tokenJson(searchQuery);
		case Trade:
			return tradeAutocompleteService.tokenJson(searchQuery);
		case ClientSite:
			return clientSiteAutocompleteService.tokenJson(searchQuery, permissions);
		case ClientSiteTag:
			return clientSiteTagAutocompleteService.tokenJson(searchQuery);
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
