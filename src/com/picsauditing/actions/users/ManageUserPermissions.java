package com.picsauditing.actions.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

@SuppressWarnings("serial")
public class ManageUserPermissions extends ContractorActionSupport {
	@Autowired
	protected UserAccessDAO userAccessDAO;

	protected List<User> userList;

	static public OpPerms[] permissionTypes = new OpPerms[] { OpPerms.ContractorAdmin, OpPerms.ContractorBilling,
			OpPerms.ContractorSafety, OpPerms.ContractorInsurance };

	public String execute() throws Exception {
		this.subHeading = getText("ManageUserPermissions.title");

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

	public boolean isUserHasPermission(User user, OpPerms permission) {
		for (UserAccess userAccess : user.getOwnedPermissions()) {
			if (permission.equals(userAccess.getOpPerm()))
				return true;
		}

		return false;
	}
}
