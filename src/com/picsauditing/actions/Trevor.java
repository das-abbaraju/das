package com.picsauditing.actions;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	private int test = 0;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public String redirect() throws Exception {
		addActionMessage("Info");
		addActionError("Error");

		return redirect("Trevor.action?test=1");
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}
}