package com.picsauditing.access;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class PermissionDB extends com.picsauditing.PICS.DataBean {
	private int userID;
	
	public void save(HttpServletRequest request, Permissions byUser) throws Exception {
		String[] types = request.getParameterValues("accessType");
		if (types!=null) {
			for(int i=0; i<types.length; i++) {
				String accessType = types[i];
				Permission permission = new Permission();
				permission.setAccessType(OpPerms.valueOf(accessType));
				permission.setViewFlag("on".equals(request.getParameter(accessType+"_viewFlag")));
				permission.setEditFlag("on".equals(request.getParameter(accessType+"_editFlag")));
				permission.setDeleteFlag("on".equals(request.getParameter(accessType+"_deleteFlag")));
				permission.setGrantFlag("on".equals(request.getParameter(accessType+"_grantFlag")));
				this.save(permission, byUser);
			}
		}
		String accessType = request.getParameter("new");
		if (accessType != null && accessType.length() > 0) {
			Permission permission = new Permission();
			permission.setAccessType(OpPerms.valueOf(accessType));
			accessType = "new";
			permission.setViewFlag("on".equals(request.getParameter(accessType+"_viewFlag")));
			permission.setEditFlag("on".equals(request.getParameter(accessType+"_editFlag")));
			permission.setDeleteFlag("on".equals(request.getParameter(accessType+"_deleteFlag")));
			permission.setGrantFlag("on".equals(request.getParameter(accessType+"_grantFlag")));
			this.save(permission, byUser);
		}
	}
	public void save(Permission permission, Permissions byUser) throws Exception {
		if (!(userID > 0)) throw new IllegalStateException("userID must be set first");
		
		String sql = "INSERT INTO useraccess (userID, accessType, viewFlag, " +
				"editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) " +
			"VALUES (" + 
				this.userID + ", '" +
				permission.getAccessType().toString() + "', " +
				convertBool(permission.isViewFlag()) + ", " +
				convertBool(permission.isEditFlag()) + ", " +
				convertBool(permission.isDeleteFlag()) + ", " +
				convertBool(permission.isGrantFlag()) + ", " +
				"NOW(), " + byUser.getUserId() + ") " +
			"ON DUPLICATE KEY UPDATE " +
				"viewFlag=VALUES(viewFlag), editFlag=VALUES(editFlag), " +
				"deleteFlag=VALUES(deleteFlag), grantFlag=VALUES(grantFlag), " +
				"lastUpdate=VALUES(lastUpdate), grantedByID=VALUES(grantedByID)";
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}
	}
	
	private String convertBool(boolean value) {
		if (value) return "1";
		else return "0";
	}

	public void delete(String type) throws Exception {
		if (!(userID > 0)) throw new IllegalStateException("userID must be set first");
		String sql = "DELETE FROM useraccess WHERE userID="+this.userID+" AND accessType='"+type+"'";
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}
	}
	public void deleteAll() throws Exception {
		if (!(userID > 0)) throw new IllegalStateException("userID must be set first");
		String sql = "DELETE FROM useraccess WHERE userID="+this.userID;
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}
	}
	
	public void setUserID(int value) {
		this.userID = value;
	}
	public void setUserID(String value) {
		this.userID = Integer.parseInt(value);
	}
}
