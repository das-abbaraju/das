package com.picsauditing.actions.users;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserAccessDAO;
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

		Boolean permValueBoolean = null;  //had to do our own convesion because struts was giving us a false when we wanted a null java.lang.Boolean
		
		if( getPermValue() != null && getPermValue().length() != 0 )
		{
			permValueBoolean = new Boolean( getPermValue() );
		}
		
		switch( type )
		{
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
