package com.picsauditing.access;

public enum OpPerms {
	FormsAndDocs("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms."),
	
	// Adding/removing Contractors
	SearchContractors("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you see the view the contractor detail page.", false, false),
	AddContractors("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", false, false),
	RemoveContractors("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", false, false),

	ContractorApproval("Contractors Approval for Work", "Allows a user to change a Contractor's work status", true, false),
	ViewNonWorking("Non-Working Contractors", "Allows users to view contractors who are NOT working for a facility. Note: the facility must track that information", false, false),

	StatusOnly("Can Only View Statuses", "Restricts users to only view a list of contractors and their flag color", true, false, false), // deprecated
	
	// Red Flag
	EditFlagCriteria("Red Flag Criteria", "Allows users to edit the criteria for flagging contractors as Red or Amber. Read access grants the ability to Edit the criteria", false, false),
	EditForcedFlags("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently", true, false),
	EditNotes("Contractor Notes", "Allows users to add notes to a contractor's account. Currently, Read accoess is sufficient to Add and Delete notes too.", true, true),
	
	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	// These are usually used in tandem with another permission
	AllContractors("All Contractors", "Can view all contractors in the database", false, false),
	AllOperators("All Operators", "Can view all operators in the database", false, false),

	// Account Management
	ManageOperators("Manage Operator Accounts", "Allows PICS employees to view/edit/delete Operator accounts"),
	ManageCorporate("Manage Corporate Accounts", "Allows PICS employees to view/edit/delete Corporate accounts"),
	ContractorAccounts("Administer Contractor Accounts", "Allow PICS employees to create/update/delete contractor accounts"),
	EditUsers("Manage User Accounts", "Allows administrators to view/add/update/delete users for their account"),
	
	// Audits
	ManageAudits("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, false),
	AssignAudits("Audit Assignment", "Allows users to assign office, desktop, and other audits to PICS auditors", true, false),
	NCMS("NCMS Administration", "Can view and assign NCMS audits", true, false),
	OSHAVerification("OSHA Verification", "Not used yet", false, false),
	OfficeAuditCalendar("Office Audit Calendar", "Allows users to view the Office Audit Calendar", false, false),
	ViewFullPQF("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", false, false),
	
	// Insurance
	InsuranceCerts("Insurance Certificates", "Allows users to view and manage the Insurance Certification process"),
	//InsuranceCertsAllOperators("Manage Insurance Certificates for All Operators"),
	InsuranceVerification("Insurance Verification"),
	InsuranceApproval("Insurance Approval"),
	
	BillingUpgrades("Billing Upgrades", "Upgrade contractor subscriptions and send them invoices", true, false),
	
	EmailAnnualUpdate("Email Annual Updates", "Send contractors reminder emails to update their PICS info. Edit permission is required to send the emails.", true, false),
	EmailTemplates("Email Templates", "Allows PICS employees to edit the templates used to automatically send emails", true, false),
	
	SwitchUser("Switch to User", "Can auto login as another user", false, false);

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
