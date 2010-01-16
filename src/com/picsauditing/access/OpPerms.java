package com.picsauditing.access;

public enum OpPerms {
	// Development Permission
	DevelopmentEnvironment("Development Environment", "Allow PICS employees to test."),
	
	// Default User Permissions
	EditProfile("Edit Profile", "Allow users to Edit their own Profile.", true, false),
	EditAccountDetails("Edit Account Details", "Allow users to Edit their own Account Information.", true, false),
	RiskRank("Edit Contractor Risk Level"),
	//BasicReporting("Basic Reporting", "Allows access to various reports including: Contractor Contact Info, Audit List, EMR, Fatalities, and Incident Rates.", false, false),
	Dashboard("Dashboard", "Allows users to view and customize (future) their home page/dashboard.", true, false),
	ContractorDetails("Contractor Details", "Allows users to view the details page along with sub nav bar, also enables search criteria other than name on reports. Allows access to the contractor contact info report and the audit list report.", false, false),
	
	FormsAndDocs("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms."),
	
	// Adding/removing Contractors
	SearchContractors("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you to view the contractor detail page.", true, true),
	AddContractors("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", false, false),
	RemoveContractors("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", false, false),

	// Approving Contractors
	ContractorApproval("Approve Contractors", "Allows a user to change a Contractor's work status", true, false),
	ViewUnApproved("View UnApproved Contractors", "Allows users to view contractors who are NOT yet approved for work for a facility. Note: the facility must track that information for this option to apply", false, false),
	ViewTrialAccounts("View Bid Only Account Contractors", "Allows users to approve or reject contractors who are bidding for work at their facility.", true, false),
	
	// Red Flag
	EditFlagCriteria("Red Flag Criteria", "Allows users to edit the criteria for flagging contractors as Red or Amber. Read access grants the ability to Edit the criteria", false, false),
	EditForcedFlags("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently", true, false),
	EditNotes("Contractor Notes", "Allows users to add notes to a contractor's account. Allows Users to view/add/edit/delete notes."),
	
	// Reports
	ContractorLicenseReport("Contractor License Report", "List all contractor licenses", false, false),
	FatalitiesReport("Fatalities Report", "List contractors with fatalities by year", false, false),
	EMRReport("EMR Report", "List contractor EMRs by year", false, false),
	ForcedFlagsReport("Forced Flags Report", "List contractors with Forced Flags.", false, false),
	
	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	// These are usually used in tandem with another permission
	AllContractors("All Contractors", "Can view all contractors in the database", false, false),
	AllOperators("All Operators", "Can view all operators in the database", false, false),

	// Account Management
	ManageOperators("Manage Operator Accounts", "Allows PICS employees to view/edit/delete Operator accounts"),
	EmailOperators("Send Operator Emails", "Allows PICS employees to send emails to operators accounts", false, false),
	ManageCorporate("Manage Corporate Accounts", "Allows PICS employees to view/edit/delete Corporate accounts"),
	ContractorAccounts("Administer Contractor Accounts", "Allow PICS employees to create/update/delete contractor accounts"),
	ContractorActivation("Activate Contractor Accounts", "Allow PICS employees to view/activate/remove contractor accounts during the activation phase"),
	EditUsers("Manage User Accounts", "Allows administrators to view/add/update/delete users for their account"),
	ChangePassword("Change User Password", "Allows administrators to change passwords on user accounts"),
	
	// Audits
	ManageAudits("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, true),
	AssignAudits("Audit Assignment", "Allows users to assign office, desktop, and other audits to PICS auditors", true, false),
	AuditVerification("Audit Verification", "Verify answers to OSHA, EMR, and other Audit data ", true, false),
	OfficeAuditCalendar("Office Audit Calendar", "Allows users to view the Office Audit Calendar", false, false),
	ViewFullPQF("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", false, false),
	AuditEdit("Contractor Audit (System)", "Can view and edit all fields related to an audit"),
	AuditCopy("Audit Copy","Can Copy Audit from one Contractor to another Contractor",false,false),
	MyCalendar("My Calendar", "View and Edit your Calendar"),
	Holidays("Holidays", "Manage the Holiday schedule for PICS", false, false),
	ManageCalendars("Manage Auditor Calendars", "Allows user to edit all auditor calendars"), 
	
	// Insurance
	InsuranceCerts("Insurance Certificates", "Allows users to view (Read), upload (Edit), and delete insurance certificates"),
	InsuranceVerification("Insurance Verification","Allows users to view the 'Insurance Verification' and 'Expired Insurance Certificates' reports (Read), and verify (Edit) the certificates",true,false),
	InsuranceApproval("Insurance Approval","Allows users to view the Insurance Approval report (Read), and approve/reject (Edit) the insurance certificates",true,false),
	
	
	Billing("Billing", "View Billing Details and Process Invoices", true, true),
	// Will be replaced with billing; remove from Accounting Group
	BillingUpgrades("Billing Upgrades", "Upgrade contractor subscriptions and send them invoices", true, false),
	DelinquentAccounts("Delinquent Accounts", "Allows users to view Delinquent Contractor Accounts", false, false),
	CreditCard("Credit Card", "Allow users to perform add, edit and delete Credit Card functions for Accounts added to Braintree.", true, true),
	InvoiceEdit("Edit Invoice", "Can view and edit all fields related to an invoice"),
	
	ContractorTags("Contractor Tags", "Allow operators to tag and categorize their contractors into searchable groups."),
	EmailAnnualUpdate("Email Annual Updates", "Send contractors reminder emails to update their PICS info. Edit permission is required to send the emails.", true, false),
	EmailTemplates("Email Templates", "Allows PICS employees to edit the templates used to automatically send emails"),
	EmailQueue("Email Queue","Allow PICS Employees to view the report for Email Queue"),
	SwitchUser("Switch to User", "Can auto login as another user", false, false),

	ManageWebcam("Webcam Management", "Can view and edit PICS' inventory of web cameras"),
	AuditorPayments("Auditor Payments", "Create and manage independent contractor payments for audits."),

	//Manage User Account Roles
	UserRolePicsOperator("User Roles PICS Operator", "Allow PICS users to assign the users to a Role.", true, true, true);

	
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
