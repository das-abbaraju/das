package com.picsauditing.actions.users;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

public class SessionTimeout extends PicsActionSupport {

	@Anonymous
	public String execute() throws Exception {
		return SUCCESS;
	}
}
