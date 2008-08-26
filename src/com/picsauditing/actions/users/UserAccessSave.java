package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.UserAccess;

public class UserAccessSave extends UsersManage {
	protected OpPerms opPerm;
	protected int accessId;
	protected UserAccessDAO userAccessDAO;
	protected UserAccess userAccess = null;
	
	
	public UserAccessSave(OperatorAccountDAO operatorDao, UserDAO userDAO, UserAccessDAO userAccessDAO) {
		super(operatorDao, userDAO);
		this.userAccessDAO = userAccessDAO;
	}

	public String execute() {
		try {
			super.execute();
		} catch (Exception e) {
		}

		if ("AddPerm".equals(button)) {
			for (UserAccess userAccess : user.getOwnedPermissions()) {
				if(userAccess.getOpPerm() != userAccess.getOpPerm()){
					UserAccess usAccess = new UserAccess();
					usAccess.setUser(user);
					usAccess.setOpPerm(opPerm);
					userAccessDAO.save(userAccess);
				}
				else
					userAccessDAO.save(userAccess);
			}
		}
		if ("RemovePerm".equals(button)) {
			userAccessDAO.remove(userAccess.getId());
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
