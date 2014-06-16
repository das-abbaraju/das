package com.picsauditing.actions;

@SuppressWarnings("serial")
public class Temp extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String contractorMenu() throws Exception {
		return "contractor-menu";
	}

	public String operatorMenu() throws Exception {
		return "operator-menu";
	}

	public String picsMenu() throws Exception {
		return "pics-menu";
	}
}