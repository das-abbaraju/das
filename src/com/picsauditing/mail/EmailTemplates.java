package com.picsauditing.mail;

public enum EmailTemplates {
	welcome("Account Activation", "EmailContractorBean"),
	annual_update("Annual Update", "EmailContractorBean"),
	certificate_expire("Certificate Expired", "EmailContractorBean"),
	dasubmit("D&A Audit Submitted", "EmailContractorBean"),
	desktopsubmit("Desktop Audit Submitted", "EmailContractorBean"),
	audits_thankyou("All Audits Submitted", "EmailAuditBean"),
	contractoradded("Facility added Contractor", "EmailUserBean"),
	password("Forgot Password", "EmailUserBean"),
	newuser("Welcome New User", "EmailUserBean"),
	verifyPqf("Verify PQF", "EmailContractorBean"),
	contractorconfirm("Confirm Audit Contractor", "EmailAuditBean"),
	openRequirements("Open Requirements Reminder", "EmailAuditBean"),
	auditorconfirm("Confirm Audit Auditor", "EmailUserBean"),
	pendingPqf("Pending PQF", "EmailAuditBean");

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
