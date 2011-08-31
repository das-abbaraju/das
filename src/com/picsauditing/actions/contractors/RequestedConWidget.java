package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.WaitingOn;

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
		return getRequestedContractors(true);
	}
	
	public List<ContractorRegistrationRequest> getReturnedContractors() {
		return requestDAO.findByOp(permissions.getAccountId(), true, WaitingOn.Operator, 10);
	}
	
	public List<ContractorRegistrationRequest> getRequestedContractors(boolean open) {
		// Assuming that this widget is only shown to CSRs
		if (permissions.isOperator())
			return requestDAO.findByOp(permissions.getAccountId(), open);
		else if (permissions.isCorporate())
			return requestDAO.findByCorp(permissions.getAccountId(), open);
		else
			return requestDAO.findByCSR(permissions.getShadowedUserID(), open);
	}
}
