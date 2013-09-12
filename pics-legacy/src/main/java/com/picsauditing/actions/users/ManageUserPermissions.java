package com.picsauditing.actions.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ManageUserPermissions extends ContractorActionSupport {
	protected List<User> userList;

	static public OpPerms[] permissionTypes = new OpPerms[] { OpPerms.ContractorAdmin, OpPerms.ContractorBilling,
			OpPerms.ContractorSafety, OpPerms.ContractorInsurance };

	@Autowired
	protected UserAccessDAO userAccessDAO;

	public ManageUserPermissions() {
		this.subHeading = "Manage User Permissions";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isAdmin() || permissions.isContractor()) {
			if (permissions.isContractor()) {
				permissions.tryPermission(OpPerms.ContractorAdmin);
			}

			findContractor();
			userList = account.getUsers();

			return SUCCESS;
		} else {
			throw new NoRightsException("Contractor Administration or PICS Administration");
		}
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
