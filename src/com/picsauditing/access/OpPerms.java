package com.picsauditing.access;

public enum OpPerms {
//This list also must be the same as the enum accessType in userAccess table
	ViewFullPQF("View Financial Info"),
	EditForms("Edit Forms"),
	SearchContractors("Search For New Contractors"),
	AddContractors("Add Contractors"),
	RemoveContractors("Remove Contractors"),
	InsuranceCerts("View Insurance Certificates Report"),
	OfficeAuditCalendar("View Office Audit Calendar"),
	EditFlagCriteria("Edit Red Flag Report Criteria"),
	EditForcedFlags("Edit Forced Flags"),
	EditNotes("Edit Contractor Notes"),
	EditUsers("Add/Edit User Accounts"),
	StatusOnly("Can Only View Statuses"),
	
	// New Permissions based on the new Model since 2007
	AssignAudits("Audit Assignment");

	private String description;
	public String getDescription(){
		return description;
	}
	OpPerms(String description){
		// TJA do we need this??
		this.description = description;
	}
}
