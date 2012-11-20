package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class Liveagent extends PicsActionSupport {
	@Anonymous
	public String execute() throws Exception {
		return SUCCESS;
	}
}
