package com.picsauditing.actions;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;

@SuppressWarnings("serial")
public class CorporateWidget extends PicsActionSupport {
	protected int operatorCount;
	protected int corporateCount;
	protected int activeContractorCount;
	protected int userCount;
	protected int totalContractorCount;
	ContractorAccountDAO cAccountDAO;
	OperatorAccountDAO opAccountDAO;
	UserDAO userDAO;

	public CorporateWidget(ContractorAccountDAO cAccountDAO, OperatorAccountDAO opAccountDAO, UserDAO userDAO) {
		this.cAccountDAO = cAccountDAO;
		this.opAccountDAO = opAccountDAO;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		totalContractorCount = cAccountDAO.getActiveContractorCounts("");
		activeContractorCount = cAccountDAO.getActiveContractorCounts("c.status = 'Active'");
		operatorCount = opAccountDAO.getOperatorCounts("o.type = 'Operator'");
		corporateCount = opAccountDAO.getOperatorCounts("o.type = 'Corporate'");
		userCount = userDAO.getUsersCounts();
		return SUCCESS;
	}

	public int getOperatorCount() {
		return operatorCount;
	}

	public int getActiveContractorCount() {
		return activeContractorCount;
	}

	public int getUserCount() {
		return userCount;
	}

	public int getTotalContractorCount() {
		return totalContractorCount;
	}

	public int getCorporateCount() {
		return corporateCount;
	}
}
