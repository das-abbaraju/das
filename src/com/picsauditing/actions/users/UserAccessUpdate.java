package com.picsauditing.actions.users;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.jpa.entities.UserAccess;

public class UserAccessUpdate extends PicsActionSupport {
	protected UserAccessDAO userAccessDAO;
	protected int accessId;
	protected OpType type;
	protected Boolean permValue;
	
	public UserAccessUpdate(UserAccessDAO userAccessDAO) {
		this.userAccessDAO = userAccessDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EditUsers, OpType.Grant);
		
		UserAccess access = userAccessDAO.find(accessId);
		
		switch( type )
		{
			case View:
				access.setViewFlag(permValue);
				break;
			case Edit:
				access.setEditFlag(permValue);
				break;
			case Delete:
				access.setDeleteFlag(permValue);
				break;
			case Grant:
				access.setGrantFlag(permValue);
		}
		
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

	public Boolean getPermValue() {
		return permValue;
	}

	public void setPermValue(Boolean permValue) {
		this.permValue = permValue;
	}
}
