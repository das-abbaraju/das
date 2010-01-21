package com.picsauditing.actions.users;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

public class ManageUserPermissions extends PicsActionSupport implements Preparable {
	protected Account account;
	protected List<User> userList;

	static public OpPerms[] permissionTypes = new OpPerms[] { OpPerms.ContractorAdmin, OpPerms.ContractorBilling,
			OpPerms.ContractorSafety, OpPerms.ContractorInsurance };

	protected AccountDAO accountDAO;
	protected UserAccessDAO userAccessDAO;

	public ManageUserPermissions(AccountDAO accountDAO, UserAccessDAO userAccessDAO) {
		this.accountDAO = accountDAO;
		this.userAccessDAO = userAccessDAO;
	}

	@Override
	public void prepare() throws Exception {
		int aID = getParameter("accountId");
		if (aID > 0)
			account = accountDAO.find(aID);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor() || permissions.isAdmin()) {
			if (account == null) {
				if (account.getType().equals("Admin"))
					account = accountDAO.find(permissions.getAccountId());
			}
			userList = account.getUsers();

			// Test for all the conditions!
			int[] permCounts = new int[] { 0, 0, 0, 0 };
			for (User user : userList) {
				if (user.isActiveB()) {
					List<UserAccess> userAccessSet = user.getOwnedPermissions();
					check: for (UserAccess ua : userAccessSet) {
						for (int i = 0; i < permissionTypes.length; i++) {
							if (ua.getOpPerm().equals(permissionTypes[i])) {
								permCounts[i]++;
								continue check;
							}
						}
					}
				}
			}
			// Add action error messages if the conditions fail
			if (permCounts[0] == 0 || permCounts[0] > 3)
				addActionError("You need 1-3 users with Admin permission");
			if (permCounts[1] == 0)
				addActionError("You need at least one user with Billing permission");
			if (permCounts[2] == 0)
				addActionError("You need at least one user with Safety permission");
			if (permCounts[3] == 0)
				addActionError("You need at least one user with Insurance permission");

			return SUCCESS;
		} else
			throw new NoRightsException("Contractors or Administration");
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
