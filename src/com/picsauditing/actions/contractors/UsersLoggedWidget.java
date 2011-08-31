package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class UsersLoggedWidget extends PicsActionSupport {
	@Autowired
	UserDAO userDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<User> getLoggedContractors() {
		return userDAO.findRecentLoggedContractors();
	}

	public List<User> getLoggedOperators() {
		return userDAO.findRecentLoggedOperators();
	}
}
