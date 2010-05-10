package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@SuppressWarnings("serial")
public class RequestedConWidget extends PicsActionSupport {
	private ContractorRegistrationRequestDAO requestDAO;
	
	public RequestedConWidget(ContractorRegistrationRequestDAO requestDAO) {
		this.requestDAO = requestDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}
	
	public List<ContractorRegistrationRequest> getRequestedContractors() {
		// Assuming that this widget is only shown to CSRs
		return requestDAO.findByCSR(permissions.getUserId());
	}
}
