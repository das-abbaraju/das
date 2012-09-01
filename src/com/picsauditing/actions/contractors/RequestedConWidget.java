package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@SuppressWarnings("serial")
public class RequestedConWidget extends PicsActionSupport {
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorRegistrationRequest> getRequestedContractors() {
		return requestDAO.findByPermissions(permissions);
	}
}