package com.picsauditing.access;

public enum OpPerms {
	// TODO search the code for each permission type and document what each one is for
	ViewFullPQF("View Financial Info"),
	EditForms("Edit Forms"),
	SearchContractors("Search For New Contractors"), // also let's you see the summary page
	AddContractors("Add Contractors"),
	RemoveContractors("Remove Contractors"),
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
	InsuranceCerts("Insurance Certificates"),
	//InsuranceCertsAllOperators("Manage Insurance Certificates for All Operators"),
	InsuranceVerification("Insurance Verification"),
	InsuranceApproval("Insurance Approval"),
	NCMS("NCMS Administration"),
	SwitchUser("Switch to User"),
	EmailTemplates("Email Templates"),
	ManageOperators("Manage Operator Accounts"),
	ManageCorporate("Manage Corporate Accounts"),
	ContractorApproval("Operator Approval of Contractors", "Allows operator users to approve contractors. This approval may factor into their Red Flag calculation.", true, false),
	ViewRedFlagged("Red-Flagged Contractors", "Users can only view contractors who are NOT red flagged unless this permission is granted.", false, false),
	AllContractors("All Contractors", "Can view all contractors in the database", false, false),
	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	ManageAudits("Manage Audits and Matrices");

	private String description;
	private String helpText;
	private boolean usesView=true;
	private boolean usesEdit=true;
	private boolean usesDelete=true;
	private boolean usesGrant=true;
	OpPerms(String description) {
		this.description = description;
	}
	OpPerms(String description, String help) {
		this.description = description;
		this.helpText = help;
	}
	OpPerms(String description, String help, boolean edit, boolean delete) {
		this.description = description;
		this.helpText = help;
		this.usesEdit = edit;
		this.usesDelete = delete;
	}
	OpPerms(String description, String help, boolean view, boolean edit, boolean delete, boolean grant) {
		this.description = description;
		this.helpText = help;
		this.usesView = view;
		this.usesEdit = edit;
		this.usesDelete = delete;
		this.usesGrant = grant;
	}
	
	public String getDescription(){
		return description;
	}

	public String getHelpText() {
		return helpText;
	}

	public boolean usesView() {
		return usesEdit;
	}

	public boolean usesEdit() {
		return usesEdit;
	}

	public boolean usesDelete() {
		return usesDelete;
	}

	public boolean usesGrant() {
		return usesEdit;
	}
}
