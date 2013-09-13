package com.picsauditing.actions.users;

import java.util.Date;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;

@SuppressWarnings("serial")
public class UserAccessUpdate extends PicsActionSupport {
	protected UserAccessDAO userAccessDAO;
	protected int accessId;
	protected OpType type;
	protected String permValue;

	public UserAccessUpdate(UserAccessDAO userAccessDAO) {
		this.userAccessDAO = userAccessDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EditUsers, OpType.Grant);

		UserAccess access = userAccessDAO.find(accessId);

		// had to do our own conversion because struts was giving us a false
		// when we wanted a null java.lang.Boolean
		Boolean permValueBoolean = null;
		if (getPermValue() != null && getPermValue().length() != 0) {
			permValueBoolean = new Boolean(getPermValue());
		}

		switch (type) {
		case View:
			access.setViewFlag(permValueBoolean);
			break;
		case Edit:
			access.setEditFlag(permValueBoolean);
			break;
		case Delete:
			access.setDeleteFlag(permValueBoolean);
			break;
		case Grant:
			access.setGrantFlag(permValueBoolean);
		}

		access.setGrantedBy(new User(permissions.getUserId()));
		access.setLastUpdate(new Date());
		userAccessDAO.save(access);

		return SUCCESS;
	}

	public int getAccessId() {
		return accessId;
	}

	public void setAccessId(int accessId) {
		this.accessId = accessId;
	}

	public OpType getType() {
		return type;
	}

	public void setType(OpType type) {
		this.type = type;
	}

	public String getPermValue() {
		return permValue;
	}

	public void setPermValue(String permValue) {
		this.permValue = permValue;
	}

}
