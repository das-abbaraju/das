package com.picsauditing.access;

public enum OpPerms {
	FormsAndDocs("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms."),
	
	// Adding/removing Contractors
	SearchContractors("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you to view the contractor detail page.", false, false),
	AddContractors("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", false, false),
	RemoveContractors("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", false, false),

	// Approving Contractors
	ContractorApproval("Approve Contractors", "Allows a user to change a Contractor's work status", true, false),
	ViewUnApproved("View UnApproved Contractors", "Allows users to view contractors who are NOT yet approved for work for a facility. Note: the facility must track that information for this option to apply", false, false),

	StatusOnly("Can Only View Statuses", "Restricts users to only view a list of contractors and their flag color", true, false, false), // deprecated
	
	// Red Flag
	EditFlagCriteria("Red Flag Criteria", "Allows users to edit the criteria for flagging contractors as Red or Amber. Read access grants the ability to Edit the criteria", false, false),
	EditForcedFlags("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently", true, false),
	EditNotes("Contractor Notes", "Allows users to add notes to a contractor's account. Currently, Read accoess is sufficient to Add and Delete notes too.", true, true),
	
	// Reports
	ContractorLicenseReport("Contractor License Report", "List all contractor licenses", false, false),
	FatalitiesReport("Fatalities Report", "List contractors with fatalities by year", false, false),
	EMRReport("EMR Report", "List contractor EMRs by year", false, false),
	
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
	ManageAudits("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, true),
	AssignAudits("Audit Assignment", "Allows users to assign office, desktop, and other audits to PICS auditors", true, false),
	NCMS("NCMS Administration", "Can view and assign NCMS audits", true, false),
	AuditVerification("Audit Verification", "Verify answers to OSHA, EMR, and other Audit data ", true, false),
	OfficeAuditCalendar("Office Audit Calendar", "Allows users to view the Office Audit Calendar", false, false),
	ViewFullPQF("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", false, false),
	AuditEdit("Contractor Audit (System)", "Can view and edit all fields related to an audit"),
	
	// Insurance
	InsuranceCerts("Insurance Certificates", "Allows users to view (Read), upload (Edit), and delete insurance certificates"),
	InsuranceVerification("Insurance Verification","Allows users to view the 'Insurance Verification' and 'Expired Insurance Certificates' reports (Read), and verify (Edit) the certificates",true,false),
	InsuranceApproval("Insurance Approval","Allows users to view the Insurance Approval report (Read), and approve/reject (Edit) the insurance certificates",true,false),

	BillingUpgrades("Billing Upgrades", "Upgrade contractor subscriptions and send them invoices", true, false),
	DelinquentAccounts("DelinquentAccounts", "Allows users to view Delinquent Contractors Accounts", false, false),
	
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
