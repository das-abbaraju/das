package com.picsauditing.actions.users;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.UserAccess;

@SuppressWarnings("serial")
public class UserAccessSave extends UsersManage {
	protected OpPerms opPerm;
	protected int accessId;
	protected JSONObject json = new JSONObject();

	public UserAccessSave(AccountDAO accountDao, OperatorAccountDAO operatorDao, UserDAO userDAO, UserAccessDAO userAccessDAO) {
		super(accountDao, operatorDao, userDAO, userAccessDAO);
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
			if (account.isContractor() && opPerm.equals(OpPerms.ContractorAdmin)) {
				if (((ContractorAccount) account).getUsersByRole(opPerm).size() >= 3) {
					json.put("title", "Too Many Users");
					json.put("reset", true);
					json.put("msg", "You can only have 1-3 users with the " + opPerm.getDescription() + " permission");
					return SUCCESS;
				}
			}
			
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
				json.put("title", "Added New Permission");
				json.put("msg", "Successfully added the " + opPerm.getDescription() + " permission to " + user.getName());
			}
		}

		if ("RemovePerm".equals(button)) {
			if (account.isContractor()) {
				if (((ContractorAccount) account).getUsersByRole(opPerm).size() <= 1) {
					json.put("title", "Cannot Remove Permission");
					json.put("reset", true);
					json.put("msg", "You must have at least one user with the " + opPerm.getDescription() + " permission");
					return SUCCESS;
				}
				
				for (UserAccess userAccess : userAccessDAO.findByUser(user.getId())) {
					if (userAccess.getOpPerm().equals(opPerm)) {
						user.getOwnedPermissions().remove(userAccess);
						userAccessDAO.remove(userAccess.getId());
						json.put("title", "Removed Permission");
						json.put("msg", "Successfully removed the " + opPerm.getDescription() + " permission from " + user.getName());
					}	
				}
			}
			
			if (accessId > 0)
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

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
}
