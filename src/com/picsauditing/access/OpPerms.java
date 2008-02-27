package com.picsauditing.access;

public enum OpPerms {
	// TODO search the code for each permission type and document what each one is for
	ViewFullPQF("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", false, false),
	FormsAndDocs("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms."),
	SearchContractors("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you see the view the contractor detail page.", false, false),
	AddContractors("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", false, false),
	RemoveContractors("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", false, false),
	OfficeAuditCalendar("Office Audit Calendar", "Allows users to view the Office Audit Calendar", false, false),
	EditFlagCriteria("Red Flag Criteria", "Allows users to edit the criteria for flagging contractors as Red or Amber. Read access grants the ability to Edit the criteria", false, false),
	EditForcedFlags("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently", true, false),
	EditNotes("Contractor Notes", "Allows users to add notes to a contractor's account. Currently, Read accoess is sufficient to Add and Delete notes too.", true, true),
	EditUsers("Manage User Accounts", "Allows administrators to view/add/update/delete users for their account"),
	EditAllUsers("Manage All Users", "Allows PICS admins to view/add/update/delete users for ALL accounts"),
	StatusOnly("Can Only View Statuses", "Restricts users to only view a list of contractors and their flag color", true, false, false), // deprecated
	
	// New Permissions based on the new Model since 2007
	ContractorAccounts("Administer Contractor Accounts", "Allow PICS employees to create/update/delete contractor accounts"),
	AssignAudits("Audit Assignment", "Allows users to assign office, desktop, and other audits to PICS auditors", true, false),
	OSHAVerification("OSHA Verification", "Not used yet", false, false),
	InsuranceCerts("Insurance Certificates", "Allows users to view and manage the Insurance Certification process"),
	//InsuranceCertsAllOperators("Manage Insurance Certificates for All Operators"),
	//InsuranceVerification("Insurance Verification"),
	//InsuranceApproval("Insurance Approval"),
	NCMS("NCMS Administration", "Can view and assign NCMS audits", true, false),
	SwitchUser("Switch to User", "Can auto login as another user", false, false),
	EmailAnnualUpdate("Email Annual Updates", "Send contractors reminder emails to update their PICS info. Edit permission is required to send the emails.", true, false),
	EmailTemplates("Email Templates", "Allows PICS employees to edit the templates used to automatically send emails", true, false),
	ManageOperators("Manage Operator Accounts", "Allows PICS employees to view/edit/delete Operator accounts"),
	ManageCorporate("Manage Corporate Accounts", "Allows PICS employees to view/edit/delete Corporate accounts"),
	ContractorApproval("Operator Approval of Contractors", "Allows operator users to approve contractors. This approval may factor into their Red Flag calculation.", true, false),
	ViewRedFlagged("Red-Flagged Contractors", "Users can only view contractors who are NOT red flagged unless this permission is granted.", false, false),
	AllContractors("All Contractors", "Can view all contractors in the database", false, false),
	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	ManageAudits("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, false);

	private String description;
	private String helpText;
	private boolean usesView=true;
	private boolean usesEdit=true;
	private boolean usesDelete=true;
	OpPerms(String description) {
		this.description = description;
		this.helpText = "";
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
	OpPerms(String description, String help, boolean view, boolean edit, boolean delete) {
		this.description = description;
		this.helpText = help;
		this.usesView = view;
		this.usesEdit = edit;
		this.usesDelete = delete;
	}
	
	public String getDescription(){
		return description;
	}

	public String getHelpText() {
		return helpText;
	}

	public boolean usesView() {
		return usesView;
	}

	public boolean usesEdit() {
		return usesEdit;
	}

	public boolean usesDelete() {
		return usesDelete;
	}
}
