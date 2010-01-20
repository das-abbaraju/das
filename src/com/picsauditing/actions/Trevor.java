package com.picsauditing.actions;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		tryPermissions(OpPerms.DevelopmentEnvironment);

		return SUCCESS;
	}
}
