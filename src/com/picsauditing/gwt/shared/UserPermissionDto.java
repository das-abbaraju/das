package com.picsauditing.gwt.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;


public class UserPermissionDto implements IsSerializable {
	
	//private OpPerms opPerm;
	private Boolean viewFlag;
	private Boolean editFlag;
	private Boolean deleteFlag;
	private Boolean grantFlag;
	private Date lastUpdate;
	private String grantedBy;

//	public OpPerms getOpPerm() {
//		return opPerm;
//	}
//
//	public void setOpPerm(OpPerms opPerm) {
//		this.opPerm = opPerm;
//	}

	public Boolean getViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(Boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public Boolean getEditFlag() {
		return editFlag;
	}

	public void setEditFlag(Boolean editFlag) {
		this.editFlag = editFlag;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Boolean getGrantFlag() {
		return grantFlag;
	}

	public void setGrantFlag(Boolean grantFlag) {
		this.grantFlag = grantFlag;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getGrantedBy() {
		return grantedBy;
	}

	public void setGrantedBy(String grantedBy) {
		this.grantedBy = grantedBy;
	}

}
