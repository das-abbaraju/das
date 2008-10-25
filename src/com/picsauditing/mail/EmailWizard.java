package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.ReportFilterContractor;

public class EmailWizard extends PicsActionSupport {
	
	public String execute() {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}
	
}
