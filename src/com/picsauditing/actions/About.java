package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

public class About extends PicsActionSupport {

	private static final long serialVersionUID = 1L;

	@Override
	@Anonymous
	public String execute() {
		return SUCCESS;
	}
}