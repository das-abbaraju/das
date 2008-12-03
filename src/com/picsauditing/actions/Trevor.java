package com.picsauditing.actions;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		output = "Hello World";
		return super.execute();
	}
}
