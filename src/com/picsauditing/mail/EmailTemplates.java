package com.picsauditing.mail;

public enum EmailTemplates {
	welcome("Account Activation", "EmailContractorBean"),
	annual_update("Annual Update", "EmailContractorBean"),
	certificate_expire("Certificate Expired"),
	dasubmit("D&A Audit Submitted"),
	desktopsubmit("Desktop Audit Submitted"),
	contractoradded("Facility added Contractor"),
	password("Forgot Password"),
	newuser("Welcome New User"),
	verifyPqf("Verify PQF");

	EmailTemplates(String description){
		this.description = description;
	}
	
	EmailTemplates(String description, String className){
		this.description = description;
		this.className = className;
	}
	
	private String description;
	public String getDescription(){
		return description;
	}
	private String className;
	
	public String getClassName() {
		return className;
	}
	
}
