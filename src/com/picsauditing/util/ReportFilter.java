package com.picsauditing.util;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.Permissions;

public class ReportFilter {
	protected String destinationAction = "";
	protected boolean ajax = false;
	protected boolean allowCollapsed = true;
	protected boolean allowMailMerge = false;

	public boolean isAllowMailMerge() {
		return allowMailMerge;
	}

	public void setAllowMailMerge(boolean allowMailMerge) {
		this.allowMailMerge = allowMailMerge;
	}

	public boolean isAllowCollapsed() {
		return allowCollapsed;
	}

	public void setAllowCollapsed(boolean allowCollapsed) {
		this.allowCollapsed = allowCollapsed;
	}

	public String getDestinationAction() {
		if (destinationAction.equals(""))
			return ServletActionContext.getActionMapping().getName();
		return destinationAction;
	}

	public void setDestinationAction(String destinationAction) {
		this.destinationAction = destinationAction;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setPermissions(Permissions permissions) {

	}
}
