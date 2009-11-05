package com.picsauditing.access;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class LoginAjax extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		return SUCCESS;
	}
}
