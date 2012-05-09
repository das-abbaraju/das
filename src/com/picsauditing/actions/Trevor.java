package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	private int test = 0;

	public String execute() throws Exception {
		// ServletActionContext.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ServletActionContext.setResponse(null);

		return SUCCESS;
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}
}