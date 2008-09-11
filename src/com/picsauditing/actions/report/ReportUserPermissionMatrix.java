package com.picsauditing.actions.report;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

public class ReportUserPermissionMatrix extends ReportActionSupport {
	private static final long serialVersionUID = 4977866366164495660L;
	
	private int accountID;
	private List<User> users;
	private Set<OpPerms> perms;
	private UserDAO userDAO;

	public ReportUserPermissionMatrix(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;
		
		if (accountID == 0 || !permissions.hasPermission(OpPerms.AllOperators))
			accountID = permissions.getAccountId();
		
		perms = new TreeSet<OpPerms>();
		users = userDAO.findByAccountID(accountID, "Yes", "");
		for(User user : users) {
			for(UserAccess access : user.getPermissions())
				perms.add(access.getOpPerm());
		}
		
		
		return SUCCESS;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	
	public List<User> getUsers() {
		return users;
	}

	public Set<OpPerms> getPerms() {
		return perms;
	}
	
}
