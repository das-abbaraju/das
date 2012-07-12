package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class PicsStyleGuide extends PicsActionSupport {
	@Anonymous
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	@Anonymous
	public String buttons() {
		return "buttons";
	}

	@Anonymous
	public String forms() {
		return "forms";
	}

	@Anonymous
	public String pills() {
		return "pills";
	}
}
