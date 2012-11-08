package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

public class AboutPICS extends PicsActionSupport {

	private static final long serialVersionUID = 1L;
	
	@Anonymous
	public String execute() {
		return SUCCESS;
	}

}
