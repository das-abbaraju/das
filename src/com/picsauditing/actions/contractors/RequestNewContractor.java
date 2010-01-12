package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport {
	public ContractorRegistrationRequest newContractor;
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;

	private CountryDAO countryDAO;
	private StateDAO stateDAO;

	public RequestNewContractor(ContractorRegistrationRequestDAO contractorRegistrationRequestDAO) {
		this.contractorRegistrationRequestDAO = contractorRegistrationRequestDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}

	public ContractorRegistrationRequest getContractorRegistrationRequest() {
		return newContractor;
	}

	public void setContractorRegistrationRequest(ContractorRegistrationRequest newContractor) {
		this.newContractor = newContractor;
	}

	public CountryDAO getCountryDAO() {
		if (countryDAO == null)
			countryDAO = (CountryDAO) SpringUtils.getBean("CountryDAO");
		return countryDAO;
	}

	public StateDAO getStateDAO() {
		if (stateDAO == null)
			stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
		return stateDAO;
	}

	public List<Country> getCountryList() {
		return getCountryDAO().findAll();
	}

	public List<State> getStateList() {
		return getStateDAO().findAll();
	}
}
