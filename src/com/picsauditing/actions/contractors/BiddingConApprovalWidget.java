package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class BiddingConApprovalWidget extends PicsActionSupport {
	@Autowired
	ContractorOperatorDAO contractorOperatorDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorOperator> getPendingApprovalBiddingContractors() {
		return contractorOperatorDAO.findPendingApprovalContractors(permissions.getAccountId(), true, permissions.isCorporate());
	}
}
