package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;

public class UsersLoggedWidget extends PicsActionSupport {
	private List<ContractorAccount> loggedCon;
	private List<User> loggedOp;
	ContractorAccountDAO accountDao;
	UserDAO userDAO;

	public UsersLoggedWidget(ContractorAccountDAO accountDao, UserDAO userDAO) {
		this.accountDao = accountDao;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorAccount> getLoggedContractors() {
		loggedCon = accountDao.findRecentLoggedContractors();
		return loggedCon;
	}

	public List<User> getLoggedOperators() {
		loggedOp = userDAO.findRecentLoggedOperators();
		return loggedOp;
	}
}
