package com.picsauditing.actions;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;

public class CorporateWidget extends PicsActionSupport {
	protected int operatorCount;
	protected int contractorCount;
	protected int userCount;
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
		contractorCount = cAccountDAO.getContractorCounts();
		operatorCount = opAccountDAO.getOperatorCounts();
		userCount = userDAO.getUsersCounts();
		return SUCCESS;
	}

	public int getOperatorCount() {
		return operatorCount;
	}

	public int getContractorCount() {
		return contractorCount;
	}

	public int getUserCount() {
		return userCount;
	}
}
