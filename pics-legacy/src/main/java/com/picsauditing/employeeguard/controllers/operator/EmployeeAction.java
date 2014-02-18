package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;

public class EmployeeAction extends PicsRestActionSupport {

	private int siteId;

	public String show() {
		return SHOW;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
}
