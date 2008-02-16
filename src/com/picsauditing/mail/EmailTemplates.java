package com.picsauditing.mail;

public enum EmailTemplates {
	welcome("Account Activation"),
	annual_update("Annual Update"),
	certificate_expire("Certificate Expired"),
	dasubmit("D&A Audit Submitted"),
	desktopsubmit("Desktop Audit Submitted"),
	contractoradded("Facility added Contractor"),
	password("Forgot Password"),
	newuser("Welcome New User");

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
