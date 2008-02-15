package com.picsauditing.access;

public enum OpPerms {
	// TODO search the code for each permission type and document what each one is for
	ViewFullPQF("View Financial Info"),
	EditForms("Edit Forms"),
	SearchContractors("Search For New Contractors"),
	AddContractors("Add Contractors"),
	RemoveContractors("Remove Contractors"),
	InsuranceCerts("Manage Insurance Certificate"),
	InsuranceCertsAllOperators("Manage Insurance Certificates for All Operators"),
	OfficeAuditCalendar("View Office Audit Calendar"),
	EditFlagCriteria("Edit Red Flag Report Criteria"),
	EditForcedFlags("Edit Forced Flags"),
	EditNotes("Edit Contractor Notes"),
	EditUsers("Add/Edit User Accounts"),
	EditAllUsers("Add/Edit All User Accounts"),
	StatusOnly("Can Only View Statuses"),
	
	// New Permissions based on the new Model since 2007
	ContractorAccounts("Administer Contractor Accounts"),
	AssignAudits("Audit Assignment"),
	OSHAVerification("OSHA Verification"),
	InsuranceVerification("Insurance Certificate Verification"),
	NCMS("NCMS Administration"),
	SwitchUser("Switch to User"),
	EmailTemplates("Email Templates"),
	ManageOperators("Manage Operator Accounts"),
	ManageCorporate("Manage Corporate Accounts"),
	AllContractors("All Contractors"), // Can view/edit/delete all contractors in the database
	ManageAudits("Manage Audits and Matrices");

	private String description;
	public String getDescription(){
		return description;
	}
	OpPerms(String description){
		// TJA do we need this??
		this.description = description;
	}
}
