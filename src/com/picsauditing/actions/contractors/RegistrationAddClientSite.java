package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RegistrationAddClientSite extends ContractorActionSupport {
	@Autowired
	private OperatorAccountDAO operatorDao;
	@Autowired
	ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private FacilityChanger facilityChanger;

	private List<OperatorAccount> searchResults;
	private String searchValue;
	private OperatorAccount operator;

	public RegistrationAddClientSite() {
		this.noteCategory = NoteCategory.OperatorChanges;
		this.currentStep = ContractorRegistrationStep.Clients;
	}

	@Override
	public String execute() throws Exception {
		if (permissions.isOperator())
			throw new NoPermissionException(getText("ContractorFacilities.error.OperatorCannotView"));
		findContractor();

		subHeading = getText("RegistrationAddClientSite.title");

		return SUCCESS;
	}

	@Override
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (contractor.getOperatorAccounts().size() > 0)
			return ContractorRegistrationStep.values()[ContractorRegistrationStep.Clients.ordinal() + 1];

		return null;
	}

	@Override
	public String nextStep() throws Exception {
		redirect(getNextRegistrationStep().getUrl());
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

	public String search() throws Exception {
		// FInd contractor
		// CHeck search value
		// if search value empty run default search
		// else if search value is *
		// find all the operators
		// else create search engine
		// use engine to build list of terms
		// use engine to build query
		// find using query
		// from results remove all operators contractor is already associated
		// with
		// return ajax page or normal page
		findContractor();

		if (Strings.isEmpty(searchValue)) {
			// if empty perform default
			searchResults = loadSearchResults();
		} else {
			// * == search for all
			if (searchValue.equals("*")) {
				searchResults = operatorDao.findWhere(false, null, permissions);
			} else {
				SearchEngine searchEngine = new SearchEngine(permissions);
				List<String> terms = searchEngine.buildTerm(searchValue, true, true);
				String select = searchEngine.buildNativeOperatorSearch(permissions, terms);

				searchResults = operatorDao.nativeClientSiteSearch(select);

				// If searchResults returns a GC Free operator,
				// search for their operators and make free operator
				// nonselectable somehow.
			}
		}
		searchResults.removeAll(contractor.getOperatorAccounts());

		if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
			return "ClientSiteList";
		} else {
			return SUCCESS;
		}
	}

	private List<OperatorAccount> loadSearchResults() throws Exception {
		List<OperatorAccount> results = new ArrayList<OperatorAccount>();

		if (contractor.getNonCorporateOperators().size() == 0) {
			if (contractor.getCountry().getIsoCode().equals("US") || contractor.getCountry().getIsoCode().equals("CA")) {

				List<BasicDynaBean> data = SmartFacilitySuggest.getFirstFacility(contractor, permissions);

				for (BasicDynaBean d : data) {
					OperatorAccount o = new OperatorAccount();

					o.setId(Integer.parseInt(d.get("opID").toString()));
					o.setName(d.get("name").toString());
					if (d.get("dbaName") != null)
						o.setDbaName(d.get("dbaName").toString());
					if (d.get("city") != null)
						o.setCity(d.get("city").toString());
					if (d.get("state") != null)
						o.setState(new State(d.get("state").toString()));
					if (d.get("country") != null)
						o.setCountry(new Country(d.get("country").toString()));
					o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
					o.setOnsiteServices(1 == (Integer) d.get("onsiteServices"));
					o.setOffsiteServices(1 == (Integer) d.get("offsiteServices"));
					o.setMaterialSupplier(1 == (Integer) d.get("materialSupplier"));

					results.add(o);
				}
			} else {
				// Search for a list of operators in the contractor's
				// country?
				String status = "'Active'";

				if (contractor.getStatus().isDemo())
					status += ",'Demo', 'Pending'";

				results = operatorDao.findWhere(false, "a.country = '" + contractor.getCountry().getIsoCode()
						+ "' AND a.status IN (" + status + ")", 10);
			}
		} else {
			if (!permissions.isCorporate()) {
				int limit = 10;
				List<BasicDynaBean> data = SmartFacilitySuggest.getSimilarOperators(contractor, limit);
				for (BasicDynaBean d : data) {
					OperatorAccount o = new OperatorAccount();

					o.setId(Integer.parseInt(d.get("opID").toString()));
					o.setName(d.get("name").toString());
					if (d.get("dbaName") != null)
						o.setDbaName(d.get("dbaName").toString());
					if (d.get("city") != null)
						o.setCity(d.get("city").toString());
					if (d.get("state") != null)
						o.setState(new State(d.get("state").toString()));
					if (d.get("country") != null)
						o.setCountry(new Country(d.get("country").toString()));
					o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
					o.setOnsiteServices(1 == (Integer) d.get("onsiteServices"));
					o.setOffsiteServices(1 == (Integer) d.get("offsiteServices"));
					o.setMaterialSupplier(1 == (Integer) d.get("materialSupplier"));

					results.add(o);
				}
			} else {
				// Corporate users should only see the operators under
				// their umbrella
				OperatorAccount op = operatorDao.find(permissions.getAccountId());
				for (Facility f : op.getOperatorFacilities()) {
					if (!contractor.getOperatorAccounts().contains(f.getOperator()))
						results.add(f.getOperator());
				}
			}
		}

		return results;
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

		Collections.sort(searchResults, new Comparator<OperatorAccount>() {
			@Override
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return searchResults;
	}

	public void setSearchResults(List<OperatorAccount> searchResults) {
		this.searchResults = searchResults;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
}
