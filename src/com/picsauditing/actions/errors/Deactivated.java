package com.picsauditing.actions.errors;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

public class Deactivated extends PicsActionSupport {
	private static final long serialVersionUID = 1L;

	@Anonymous
	public String execute() throws Exception {
        clearPermissionsSessionAndCookie();
		return SUCCESS;
	}

}