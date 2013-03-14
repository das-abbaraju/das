package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class OperatorPicsContactsWidget extends PicsActionSupport {
	@Autowired
	private AccountUserDAO amDAO;
	
	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}
	
	public Map<UserAccountRole, List<User>> getContacts() {
		List<AccountUser> users = amDAO.findByAccountCurrent(permissions.getAccountId());
		
		List<User> salesReps = new ArrayList<User>();
		List<User> accountManagers = new ArrayList<User>();
		
		for (AccountUser au : users) {
			if (au.getRole().equals(UserAccountRole.PICSAccountRep))
				accountManagers.add(au.getUser());
			else
				salesReps.add(au.getUser());
		}
		
		Map<UserAccountRole, List<User>> managers = new TreeMap<UserAccountRole, List<User>>();
		if (accountManagers.size() > 0)
			managers.put(UserAccountRole.PICSAccountRep, accountManagers);
		if (salesReps.size() > 0)
			managers.put(UserAccountRole.PICSSalesRep, salesReps);
		
		return managers;
	}
}
