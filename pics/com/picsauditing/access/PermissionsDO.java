package com.picsauditing.access;

import java.sql.ResultSet;

import com.picsauditing.PICS.DateBean;

public class PermissionsDO {
	
	private int permission_id;
	private int user_id;
	private String seeOsha;
	private String seeFullPQF;
	private String seeFlagCriteria;
	private String seeForcedFlags;
	private String editNotes;
	
	public int getPermission_id() {
		return permission_id;
	}
	public void setPermission_id(int permission_id) {
		this.permission_id = permission_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getSeeOsha() {
		return seeOsha;
	}
	public void setSeeOsha(String seeOsha) {
		this.seeOsha = seeOsha;
	}
	public String getSeeFullPQF() {
		return seeFullPQF;
	}
	public void setSeeFullPQF(String seeFullPQF) {
		this.seeFullPQF = seeFullPQF;
	}
	public String getSeeFlagCriteria() {
		return seeFlagCriteria;
	}
	public void setSeeFlagCriteria(String seeFlagCriteria) {
		this.seeFlagCriteria = seeFlagCriteria;
	}
	public String getSeeForcedFlags() {
		return seeForcedFlags;
	}
	public void setSeeForcedFlags(String seeForcedFlags) {
		this.seeForcedFlags = seeForcedFlags;
	}
	public String getEditNotes() {
		return editNotes;
	}
	public void setEditNotes(String editNotes) {
		this.editNotes = editNotes;
	}
	
	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		setPermission_id(Integer.parseInt(request.getParameter("permission_id")));
		setUser_id(Integer.parseInt(request.getParameter("user_id")));
		setSeeOsha(request.getParameter("seeOsha"));
		setSeeFullPQF(request.getParameter("seeFullPQF"));
		setSeeFlagCriteria(request.getParameter("seeFlagCriteria"));
		setSeeForcedFlags(request.getParameter("seeForcedFlags"));
		setEditNotes(request.getParameter("editNotes"));
	}//setFromRequest

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		setPermission_id(SQLResult.getInt("permission_id"));
		setUser_id(SQLResult.getInt("user_id"));
		setSeeOsha(SQLResult.getString("seeOsha"));
		setSeeFullPQF(SQLResult.getString("seeFullPQF"));
		setSeeFlagCriteria(SQLResult.getString("seeFlagCriteria"));
		setSeeForcedFlags(SQLResult.getString("seeForcedFlags"));
		setEditNotes(SQLResult.getString("editNotes"));
	}//setFromResultSet

}
