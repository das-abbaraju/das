package com.picsauditing.actions.contractors;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport {
	public ContractorRegistrationRequest newContractor;
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	
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
	

}
