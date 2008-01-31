package com.picsauditing.access;

import java.sql.*;
import javax.servlet.http.HttpServletRequest;

/**
 * This controls the permission for a specific user or group of users to do a set of action
 * View/Edit/Delete/Grant
 * Edit means to Insert and Update
 * Warning: this class is stored in a Set<Permission> in session
 * Make sure you keep the footprint very small
 */
public class Permission {
	private boolean viewFlag = false;
	private boolean editFlag = false;
	private boolean deleteFlag = false;
	private boolean grantFlag = false;
	private OpPerms accessType;
	
	public void setFromResultSet(ResultSet SQLResult) {
		try {
			viewFlag = SQLResult.getShort("viewFlag") == 1 ? true : false;
			editFlag = SQLResult.getShort("editFlag") == 1 ? true : false;
			deleteFlag = SQLResult.getShort("deleteFlag") == 1 ? true : false;
			grantFlag = SQLResult.getShort("grantFlag") == 1 ? true : false;
			accessType = OpPerms.valueOf(SQLResult.getString("accesstype"));
		} catch(Exception e){
			
		}
	}
	public void setFromRequest(HttpServletRequest request) {
		try {
		} catch(Exception e){
			
		}
	}

	public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}
	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public void setGrantFlag(boolean grantFlag) {
		this.grantFlag = grantFlag;
	}
	public void setAccessType(OpPerms accessType) {
		this.accessType = accessType;
	}
	public boolean isViewFlag() {
		return viewFlag;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public boolean isGrantFlag() {
		return grantFlag;
	}

	public OpPerms getAccessType() {
		return accessType;
	}
		
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		
		if((obj == null) || (obj.getClass() != this.getClass()))
				return false;
	
		Permission test = (Permission)obj;
		return (0 == accessType.compareTo(test.accessType)); 
		
	}

	public int hashCode() {
		return 984 + accessType.hashCode();
	}
}
