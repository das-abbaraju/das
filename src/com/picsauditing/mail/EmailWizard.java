package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;

public class EmailWizard extends PicsActionSupport {
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}
	
}
