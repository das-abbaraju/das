package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class UsersLoggedWidget extends PicsActionSupport {
	UserDAO userDAO;

	public UsersLoggedWidget(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<User> getLoggedContractors() {
		return userDAO.findRecentLoggedContractors(permissions);
	}

	public List<User> getLoggedOperators() {
		return userDAO.findRecentLoggedOperators();
	}
}
