package com.picsauditing.access;

import java.sql.*;

public class Permission {
	// Warning: this class is stored in a Set<Permission> in session
	// Make sure you keep the footprint very small
	
	private boolean viewFlag = false;
	private boolean editFlag = false;
	private boolean deleteFlag = false;
	private boolean grantFlag = false;
	private OpPerms accessType;
	
	public void setFromResultSet(ResultSet SQLResult){
		try{
			viewFlag = SQLResult.getShort("viewFlag") == 1 ? true : false;
			editFlag = SQLResult.getShort("editFlag") == 1 ? true : false;
			deleteFlag = SQLResult.getShort("deleteFlag") == 1 ? true : false;
			grantFlag = SQLResult.getShort("grantFlag") == 1 ? true : false;
			accessType = OpPerms.valueOf(SQLResult.getString("accesstype"));
		}catch(Exception e){
			
		}
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
