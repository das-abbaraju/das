package com.picsauditing.mail;

public enum EmailTemplates {
	annual_update("Annual Update"),
	contractoradded("Facility added Contractor"),
	dasubmit("D&A Audit Submitted"),
	desktopsubmit("Desktop Audit Submitted"),
	newuser("Welcome New User"),
	password("Forgot Password"),
	welcome("Account Activation");

	private String description;
	public String getDescription(){
		return description;
	}
	private String type="Contractor";
	public String getType(){
		return type;
	}
	EmailTemplates(String description){
		this.description = description;
	}
	EmailTemplates(String description, String type){
		this.description = description;
		this.type = type;
	}
}
