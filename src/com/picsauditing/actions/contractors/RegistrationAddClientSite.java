package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationAddClientSite extends RegistrationAction {
	@Autowired
	ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private FacilityChanger facilityChanger;

	private List<OperatorAccount> searchResults;
	private List<OperatorAccount> generalContractorClientSites = new ArrayList<OperatorAccount>();
	private String searchValue;
	private OperatorAccount operator;
	private OperatorAccount generalContractor;
	private SearchEngine searchEngineForTesting;

	public RegistrationAddClientSite() {
		this.noteCategory = NoteCategory.OperatorChanges;
		this.currentStep = ContractorRegistrationStep.Clients;
	}

	@Override
	public String execute() throws Exception {
		if (permissions.isOperator())
			throw new NoRightsException(getText("ContractorFacilities.error.OperatorCannotView"));
		findContractor();

		subHeading = getText("RegistrationAddClientSite.title");

		return SUCCESS;
	}

	@SkipValidation
	public String clientList() throws Exception {
		if (generalContractor != null) {
			generalContractorClientSites.addAll(generalContractor.getClientSitesOrGeneralContractors());
		}

		return "GeneralContractorSiteList";
	}

	@Override
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (contractor.getOperatorAccounts().size() > 0)
			return ContractorRegistrationStep.values()[ContractorRegistrationStep.Clients.ordinal() + 1];

		return null;
	}

	@Override
	public String nextStep() throws Exception {
		if (ContractorRegistrationStep.containsAtLeastOneClientSiteForGCFree(contractor)) {
			return setUrlForRedirect(getNextRegistrationStep().getUrl());
		} else {
			List<OperatorAccount> missingGCFreeClientSites = getMissingGCFreeClientSites();
			if (!missingGCFreeClientSites.isEmpty()) {
				String missingOperatorNames = missingGCFreeClientSites.get(0).getName();

				for (int index = 1; index < missingGCFreeClientSites.size(); index++) {
					missingOperatorNames += ", " + missingGCFreeClientSites.get(index).getName();
				}

				addActionError(getTextParameterized("RegistrationAddClientSite.MissingRequiredOperators",
						missingOperatorNames));
			}
		}

		return SUCCESS;
	}

	public void ajaxAdd() throws Exception {
		findContractor();
		contractor.setRenew(true);

		checkServiceTypeRestrictions();

		facilityChanger.setContractor(contractor);
		facilityChanger.setPermissions(permissions);
		facilityChanger.setOperator(operator.getId());
		facilityChanger.add();

		if (generalContractor != null) {
			facilityChanger.setOperator(generalContractor);
			facilityChanger.add();
		}
	}

	public void ajaxRemove() throws Exception {
		findContractor();

		facilityChanger.setContractor(contractor);
		facilityChanger.setPermissions(permissions);
		facilityChanger.setOperator(operator.getId());
		facilityChanger.remove();
	}

	public void checkServiceTypeRestrictions() {
		int on = (operator.isOnsiteServices() ? 1 : 0);
		int off = (operator.isOffsiteServices() ? 1 : 0);
		int ms = (operator.isMaterialSupplier() ? 1 : 0);
		int trx = (operator.isTransportationServices() ? 1 : 0);

		if (on + off + ms + trx == 1) {
			if (operator.isOnsiteServices())
				contractor.setOnsiteServices(true);
			if (operator.isOffsiteServices())
				contractor.setOffsiteServices(true);
			if (operator.isMaterialSupplier())
				contractor.setMaterialSupplier(true);
			if (operator.isTransportationServices())
				contractor.setTransportationServices(true);
		}
	}

	private SearchEngine searchEngine(Permissions permissions) {
		if (searchEngineForTesting == null) {
			return new SearchEngine(permissions);
		}
		return searchEngineForTesting;
	}
	
	public String search() throws Exception {
		findContractor();

		if (Strings.isEmpty(searchValue)) {
			searchResults = loadSearchResults();
		} else {
			// * == search for all
			if (searchValue.equals("*")) {
				searchResults = operatorDAO.findWhere(false, null, permissions);
			} else {
				SearchEngine searchEngine = searchEngine(permissions);
				List<String> terms = searchEngine.buildTerm(searchValue, true, true);
				String select = searchEngine.buildNativeOperatorSearch(permissions, terms);

				searchResults = operatorDAO.nativeClientSiteSearch(select);

				// If searchResults returns a GC Free operator,
				// search for their operators and make free operator
				// nonselectable somehow.
			}
		}

		for (OperatorAccount existingOperator : contractor.getOperatorAccounts()) {
			searchResults.remove(existingOperator);
		}

		if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
			return "ClientSiteList";
		} else {
			return SUCCESS;
		}
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public List<OperatorAccount> getSearchResults() throws Exception {
		if (searchResults == null || searchResults.isEmpty()) {
			findContractor();
			searchResults = loadSearchResults();
		}

		return searchResults;
	}

	public List<OperatorAccount> getGeneralContractorClientSites() {
		return generalContractorClientSites;
	}

	public void setGeneralContractorClientSites(List<OperatorAccount> generalContractorClientSites) {
		this.generalContractorClientSites = generalContractorClientSites;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public OperatorAccount getGeneralContractor() {
		return generalContractor;
	}

	public void setGeneralContractor(OperatorAccount generalContractor) {
		this.generalContractor = generalContractor;
	}

	public boolean isWorksForOperator(OperatorAccount operatorAccount) throws Exception {
		findContractor();

		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (operator.getId() == operatorAccount.getId())
				return true;
		}

		return false;
	}

	private List<OperatorAccount> getMissingGCFreeClientSites() {
		List<OperatorAccount> missingOperators = new ArrayList<OperatorAccount>();
		for (OperatorAccount operator : contractor.getGeneralContractorOperatorAccounts()) {
			if ("No".equals(operator.getDoContractorsPay())) {

				List<OperatorAccount> linkedClientSites = new ArrayList<OperatorAccount>(
						operator.getLinkedClientSites());
				linkedClientSites.removeAll(contractor.getOperatorAccounts());

				missingOperators.addAll(linkedClientSites);
			}
		}

		return missingOperators;
	}

	private List<OperatorAccount> loadSearchResults() throws Exception {
		List<OperatorAccount> results = new ArrayList<OperatorAccount>();
		List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();

		if (contractor.getCountry().getIsoCode().equals("US") || contractor.getCountry().getIsoCode().equals("CA")) {
			if (contractor.getNonCorporateOperators().size() == 0) {
				data = SmartFacilitySuggest.getFirstFacility(contractor, permissions);
			} else {
				data = SmartFacilitySuggest.getSimilarOperators(contractor, 10);
			}

			List<Integer> operatorIDs = new ArrayList<Integer>();

			for (BasicDynaBean d : data) {
				operatorIDs.add(Integer.parseInt(d.get("opID").toString()));
			}

			results = operatorDAO.findWhere(false, "a.id IN (" + Strings.implode(operatorIDs) + ")");
		} else {
			// Search for a list of operators in the contractor's
			// country?
			String status = "'Active'";

			if (contractor.getStatus().isDemo())
				status += ",'Demo', 'Pending'";

			results = operatorDAO.findWhere(false, "a.country = '" + contractor.getCountry().getIsoCode()
					+ "' AND a.status IN (" + status + ")", 10);
		}

		return results;
	}
}
