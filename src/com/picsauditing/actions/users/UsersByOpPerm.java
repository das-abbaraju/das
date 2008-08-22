package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class UsersByOpPerm extends PicsActionSupport {
	private OpPerms opPerm;
	private List<User> users;
	private UserDAO userDao;
	
	public UsersByOpPerm(UserDAO userDao) {
		this.userDao = userDao;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (opPerm != null) {
			String where = "u IN (SELECT user FROM UserAccess WHERE opPerm = '"+ opPerm.toString()+"')";
			if (!permissions.hasPermission(OpPerms.AllOperators))
				where += " AND u.account.id = " + permissions.getAccountId();
			
			users = userDao.findWhere(where);
		}

		return SUCCESS;
	}

	public OpPerms getOpPerm() {
		return opPerm;
	}

	public void setOpPerm(OpPerms opPerm) {
		this.opPerm = opPerm;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
