package com.picsauditing.actions;


@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	private int test = 0;

	public String execute() throws Exception {
		//ServletActionContext.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//ServletActionContext.setResponse(null);

		return SUCCESS;
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}
}