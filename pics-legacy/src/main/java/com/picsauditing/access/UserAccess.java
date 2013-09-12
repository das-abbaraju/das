package com.picsauditing.access;

import java.io.Serializable;

public class UserAccess implements Serializable {
	private static final long serialVersionUID = -2527281530367542781L;
	private OpPerms opPerm;
	private boolean viewFlag = true;
	private boolean editFlag = false;
	private boolean deleteFlag = false;
	private boolean grantFlag = false;

	public UserAccess() {
	}

	public UserAccess(com.picsauditing.jpa.entities.UserAccess jpa) {
		this.opPerm = jpa.getOpPerm();
		this.viewFlag = isTrue(jpa.getViewFlag());
		this.editFlag = isTrue(jpa.getEditFlag());
		this.deleteFlag = isTrue(jpa.getDeleteFlag());
		this.grantFlag = isTrue(jpa.getGrantFlag());
	}

	private boolean isTrue(Boolean flag) {
		if (flag == null)
			return false;
		else
			return flag;
	}

	public OpPerms getOpPerm() {
		return opPerm;
	}

	public void setOpPerm(OpPerms opPerm) {
		this.opPerm = opPerm;
	}

	public boolean isViewFlag() {
		return viewFlag;
	}

    public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public boolean isGrantFlag() {
		return grantFlag;
	}

	public void setGrantFlag(boolean grantFlag) {
		this.grantFlag = grantFlag;
	}

    @Override
    public String toString() {
        return "UserAccess{" +
                "opPerm=" + opPerm +
                '}';
    }
}
