package com.picsauditing.actions.users;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

public class UserAccessSave extends UsersManage {
	protected OpPerms opPerm;
	protected UserAccessDAO userAccessDAO;
	protected int accessId;

	public UserAccessSave(OperatorAccountDAO operatorDao, UserDAO userDAO, UserAccessDAO userAccessDAO) {
		super(operatorDao, userDAO);
		this.userAccessDAO = userAccessDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin()) {
			addActionError("Timeout: you need to login again");
			return LOGIN;
		}
		super.execute();

		if (user == null) {
			addActionError("user is not set");
			return SUCCESS;
		}

		if ("AddPerm".equals(button)) {
			if (opPerm == null) {
				addActionError("permission is not selected");
				return SUCCESS;
			}
			// Make sure they don't already have it
			boolean hasPerm = false;
			for (UserAccess userAccess : user.getOwnedPermissions()) {
				if (userAccess.getOpPerm().equals(opPerm)) {
					hasPerm = true;
				}
			}
			if (!hasPerm) {
				// The don't already have the perm, add it now
				UserAccess userAccess = new UserAccess();
				userAccess.setUser(user);
				userAccess.setOpPerm(opPerm);
				userAccess.setGrantedBy(new User(permissions.getUserId()));
				userAccess.setLastUpdate(new Date());
				if (opPerm.usesView())
					userAccess.setViewFlag(true);
				if (opPerm.usesEdit())
					userAccess.setEditFlag(true);
				if (opPerm.usesDelete())
					userAccess.setDeleteFlag(true);
				userAccess.setGrantFlag(true);

				userAccessDAO.save(userAccess);
				user.getOwnedPermissions().add(userAccess);
				// Resort the list
				Set<UserAccess> temp = new TreeSet<UserAccess>();
				temp.addAll(user.getOwnedPermissions());
				user.getOwnedPermissions().clear();
				user.getOwnedPermissions().addAll(temp);
			}
		}

		if ("RemovePerm".equals(button)) {
			userAccessDAO.remove(accessId);
		}
		return SUCCESS;
	}

	public OpPerms getOpPerm() {
		return opPerm;
	}

	public void setOpPerm(OpPerms opPerm) {
		this.opPerm = opPerm;
	}

	public int getAccessId() {
		return accessId;
	}

	public void setAccessId(int accessId) {
		this.accessId = accessId;
	}
}
