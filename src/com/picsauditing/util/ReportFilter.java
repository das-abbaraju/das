package com.picsauditing.util;

import com.picsauditing.access.Permissions;

public class ReportFilter {
	protected String destinationAction = "";
	protected boolean ajax = false;
	protected boolean allowCollapsed = true;
	
	public boolean isAllowCollapsed() {
		return allowCollapsed;
	}


	public void setAllowCollapsed(boolean allowCollapsed) {
		this.allowCollapsed = allowCollapsed;
	}


	public String getDestinationAction() {
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
