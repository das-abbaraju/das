package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class BiddingConApprovalWidget extends PicsActionSupport {
	ContractorOperatorDAO contractorOperatorDAO;

	public BiddingConApprovalWidget(ContractorOperatorDAO contractorOperatorDAO) {
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorOperator> getPendingApprovalBiddingContractors() {
		return contractorOperatorDAO.findPendingApprovalContractors(permissions.getAccountId(), true);
	}
}
