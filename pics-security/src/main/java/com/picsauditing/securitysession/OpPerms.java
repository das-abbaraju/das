
package com.picsauditing.securitysession;

/**
 * This Enum defines the User Permissions.
 * 
 * OpPerms stands for Operator Permissions, but it is actually a misnomer.
 */
public enum OpPerms /*implements Translatable*/ {
	None(false,false,false)/*("No Permission", "Default permission if a permission is required but no privileges should be granted.", false, false)*/,
	
	// Development Permission
	DevelopmentEnvironment(true,true,false)/*("Development Environment", "Allow PICS employees to test.", true, false)*/,
	Debug(false,false,false)/*("Debug", "Allows PICS users to see debugging information", true, false)*/,
	CreateTestUser(false,false,false)/*("Create Test User", "Allows PICS master test user to create new users", false, false)*/,

	// Default User Permissions
	EditProfile(true,false,true)/*("Edit Profile", "Allow users to Edit their own Profile.", true, true)*/,
	EditAccountDetails(true,false,true)/*("Edit Account Details", "Allow users to Edit their own Account Information.", false, true)*/,
	RiskRank(true,false,false)/*("Edit Contractor Risk Level", "Allows PICS users to manually edit the contractor's Risk Level.", false, false)*/,
	Dashboard(true,false,true)/*("Dashboard", "Allows users to view and customize (future) their home page/dashboard.", true, true)*/,
	ContractorDetails(false,false,true)/*("Contractor Details", "Allows users to view the details page along with sub nav bar, also enables search criteria other than name on reports. Allows access to the contractor contact info report and the audit list report.", true, true)*/,
	EditContractorSafeHarbor(true,false,false)/*("Edit Contractor Safe Harbor options", "Allows users to view/edit the 'Automatically Add My Account.' option.", true, false)*/,

	FormsAndDocs(true,true,false)/*("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms.", true, true)*/,
	Translator(true,true,false)/*("Translator", "Allow users translate PICS Organizer", true, false)*/,

	// Adding/removing Contractors
	SearchContractors(true,true,false)/*("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you to view the contractor detail page.", true, true)*/,
	AddContractors(false,false,false)/*("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", true, true)*/,
	RemoveContractors(false,false,false)/*("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", true, true)*/,

	// Approving Contractors
	ContractorApproval(true,false,false)/*("Approve Contractors", "Allows a user to change a Contractor's work status", true, true)*/,
	ViewUnApproved(false,false,false)/*("View UnApproved Contractors", "Allows users to view contractors who are NOT yet approved for work for a facility. Note: the facility must track that information for this option to apply", true, true)*/,
	ViewTrialAccounts(true,false,false)/*("View Bid Only Contractors", "Allows users to approve or reject contractors who are bidding for work at their facility.", true, true)*/,

	// Red Flag
	EditFlagCriteria(true,false,false)/*("Manage Flag Criteria", "Allows users to view or edit the criteria for flagging contractors Red or Amber.", true, true)*/,
	EditForcedFlags(true,false,false)/*("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently.", true, true)*/,

	// TODO - consider removing this completely
	EditNotes(true,true,false)/*("Contractor Notes", "Allows users to add notes to a contractor's account. Allows Users to view/add/edit/delete notes.", true, true)*/,

	// Reports
	ContractorLicenseReport(false,false,false)/*("Contractor License Report", "List all contractor licenses", true, true)*/,
	FatalitiesReport(false,false,false)/*("Fatalities Report", "List contractors with fatalities by year", true, true)*/,
	EMRReport(false,false,false)/*("EMR Report", "List contractor EMRs by year", true, true)*/,
	TRIRReport(false,false,false)/*("Incidence Rate Report", "List contractor incidence rates by year", true, true)*/,
	ForcedFlagsReport(false,false,false)/*("Forced Flags Report", "List contractors with Forced Flags.", true, true)*/,

	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	// These are usually used in tandem with another permission
	AllContractors(false,false,false)/*("All Contractors", "Can view all contractors in the database", true, false)*/,
	AllOperators(false,false,false)/*("All Operators", "Can view all operators in the database", true, false)*/,

	// Account Management
	ManageOperators(true,true,false)/*("Manage Operator Accounts", "Allows PICS employees to view/edit/delete Operator accounts", true, false)*/,
	EmailOperators(false,false,false)/*("Send Operator Emails", "Allows PICS employees to send emails to operators accounts", true, false)*/,
	ManageCorporate(true,true,false)/*("Manage Corporate Accounts", "Allows PICS employees to view/edit/delete Corporate accounts", true, false)*/,
	ManageAssessment(true,true,false)/*("Manage Assessment Center Accounts", "Allows PICS employees to view/edit/delete Assessment Center accounts", true, false)*/,
	ContractorAccounts(true,true,false)/*("Administer Contractor Accounts", "Allow PICS employees to create/update/delete contractor accounts", true, false)*/,
	ContractorActivation(true,true,false)/*("Activate Contractor Accounts", "Allow PICS employees to view/activate/remove contractor accounts during the activation phase", true, false)*/,
	EditUsers(true,true,true)/*("Manage User Accounts", "Allows administrators to view/add/update/delete users for their account", true, true)*/,
	EditUsersPics(true,true,false)/*("Manage PICS User Accounts", "Edit user accounts on the PICS Admin account", true, false)*/,
	// Audits
	ManageAudits(true,true,false)/*("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, false)*/,
	AssignAudits(true,false,false)/*("Audit Assignment", "Allows users to assign implementation Audit, manual Audit, and other audits to PICS Safety Professionals", true, true)*/,
	AuditVerification(true,false,false)/*("Audit Verification", "Verify answers to OSHA, EMR, and other Audit data ", true, false)*/,
	AuditDocumentReview(true,false,false)/*("Audit Document Review", "Mark Uploaded Audit Documents as reviewed", true, true)*/,
	OfficeAuditCalendar(false,false,false)/*("Implementation Audit Calendar", "Allows users to view the Implementation Audit Calendar", true, true)*/,
	ViewFullPQF(false,false,false)/*("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", true, false)*/,
	AuditEdit(true,true,false)/*("Contractor Audit (System)", "Can view and edit all fields related to an audit", true, true)*/,
	CaoEdit(false,false,false)/*("CAO Edit", "Can change the cao statuses directly from the audit page.", true, true)*/,
	AuditCopy(false,false,false)/*("Audit Copy", "Can Copy Audit from one Contractor to another Contractor", true, false)*/,
	MyCalendar(true,true,false)/*("My Calendar", "View and Edit your Calendar", true, false)*/,
	Holidays(false,false,false)/*("Holidays", "Manage the Holiday schedule for PICS", true, false)*/,
	ManageCalendars(true,true,false)/*("Manage Safety Professional Calendars", "Allows user to edit all Safety Professional calendars", true, false)*/,
	AuditRuleAdmin(true,false,false)/*("Audit Rule Admin", "Allows user to Administer Rules created or modified by other users", false, false)*/,
	ManageCategoryRules(true,true,false)/*("Manage Category Rules", "Allows user to view and edit Category Rules for Audits and other documents", true, false)*/,
	// We may want to combine ManageCategoryRules and ManageAuditTypeRules
	ManageAuditTypeRules(true,true,false)/*("Manage AuditType Rules", "Allows user to view and edit Audit Type Rules for Audits and other documents", true, false)*/,
	ManageAuditWorkFlow(true,true,false)/*("Manage Audit Workflow", "Allows users to view and edit work flows used with Audit Types", true, false)*/,
	ManageCsrAssignment(false,false,false)/*("Manage CSR Assignments", "Allows admins to accept or reject recommended csr assignments", true, false)*/,
	
	// Insurance
	InsuranceCerts(true,true,false)/*("Insurance Certificates", "Allows users to view (Read), upload (Edit), and delete insurance certificates", true, true)*/,
	InsuranceVerification(true,false,false)/*("Insurance Verification", "Allows users to view the 'Insurance Verification' and 'Expired Insurance Certificates' reports (Read), and verify (Edit) the certificates", true, false)*/,
	InsuranceApproval(true,false,false)/*("Insurance Approval", "Allows users to view the Insurance Approval report (Read), and approve/reject (Edit) the insurance certificates", true, true)*/,

	Billing(true,true,false)/*("Billing", "View Billing Details and Process Invoices", true, false)*/,
	// Will be replaced with billing; remove from Accounting Group
	BillingUpgrades(true,false,false)/*("Billing Upgrades", "Upgrade contractor subscriptions and send them invoices", true, false)*/,
	DelinquentAccounts(false,false,false)/*("Delinquent Accounts", "Allows users to view Delinquent Contractor Accounts", true, true)*/,
	InvoiceEdit(true,true,false)/*("Edit Invoice", "Can view and edit all fields related to an invoice", true, false)*/,
	SalesCommission(true,true,false)/*("Sales Commission", "View Sales Commission Break-Down on an invoice", false, false)*/,

    EditTags(true,true,false)/*("Edit Tags", "Allow users to edit/update/remove tags and assign/unassign tags to contractors.", true, true)*/,
	ContractorTags(true,true,false)/*("Contractor Tags", "Allow operators to tag and categorize their contractors into searchable groups.", true, true)*/,
	EmailAnnualUpdate(true,false,false)/*("Email Annual Updates", "Send contractors reminder emails to update their PICS info. Edit permission is required to send the emails.", true, true)*/,
	EmailTemplates(true,true,false)/*("Email Templates", "Allows PICS employees to edit the templates used to automatically send emails", true, false)*/,
	EmailVelocityHtml(true,true,false)/*("Edit Email Velocity and HTML attributes", "Allows PICS employees to edit the email velocity and html attributes", true, false)*/,
	EmailQueue(true,true,false)/*("Email Queue", "Allow PICS Employees to view the report for Email Queue", true, true)*/,
	SwitchUser(false,false,false)/*("Switch to User", "Can auto login as another user", true, false)*/,

	UserZipcodeAssignment(false,false,false)/*("User Zipcode Assignment", "Allow PICS employees to assign CSRs and Auditors to specific geographic locations.", true,false)*/,
	
	AuditorPayments(true,true,false)/*("Safety Professional Payments", "Create and manage independent contractor payments for audits.", true, false)*/,
	
	ManageTrades(true,true,false)/*("Manage Trades", "Allow users access to Trade taxonomy", true, false)*/,

	// Manage User Account Roles
	UserRolePicsOperator(true,true,false)/*("User Roles PICS Operator", "Allow PICS users to assign the users to a Role.", true, false)*/,

	// Client Site referrals
	ClientSiteReferrals(true,true,false)/*("Client Site Referrals", "Can view all client site referrals", true, false)*/,
	ReferNewClientSite(true,true,false)/*("Refer New Client Sites", "Can edit all client site referrals", true, false)*/,

	// All of the contractor permissions
	ContractorAdmin(false,false,true)/*("Admin", "Account, Users, Activation", false, false)*/,
	ContractorSafety(false,false,true)/*("Safety", "PQF, Annual Updates, Audits, etc", false, false)*/,
	ContractorBilling(false,false,true)/*("Billing", "Invoices, Payments, Credit Card", false, false)*/,
	ContractorInsurance(false,false,true)/*("Insurance", "Insurance Certificates", false, false)*/,
	
	// Operator Qualification
	ManageProjects(true,true,false)/*("Manage Projects", "Allows operators to view or edit their project sites.", false, true)*/,
	ManageJobTasks(true,true,false)/*("Manage Job Tasks", "Allows operators to view or edit their job tasks.", false, true)*/,

	// View the Operator Flag Matrix report
	OperatorFlagMatrix(false,false,false)/*("Operator Flag Matrix", "Allows operators to view the Operator Flag Matrix report.", false, true)*/,
	
	Report(true,true,false)/*("Reports", "Create and edit reports", true, true)*/,

	// Employee Management
	DefineRoles(true,true,true)/*("Define Roles", "Allows contractors to create job roles to assign to their employees.", true, false)*/,
	DefineCompetencies(true,true,false)/*("Define Competencies", "Allows operators to create compentency requirements for satisfying job roles.", true, true)*/,
	ManageEmployees(true,true,false)/*("Manage Employees", "Allows Contractors to view or edit their employees.", true, true)*/,
	EmployeeList(true,true,false)/*("Employee List", "Allows operators and administrators to view the employee list.", true, true)*/,
	UploadEmployeeDocumentation(false,true,false)/*("Upload Employee Documentation", "Allows operators to upload documentation for " +
			"employees.", true, true)*/,
	
	// Contractor watch
	ContractorWatch(true,true,false)/*("Contractor Watch", "Allows operators to view the activity of watched contractors.", true, true)*/,
	WatchListManager(true,true,false)/*("Watch List Manager", "Allows managers to add and remove users from watch lists.", true, true)*/,
	
	// Request New Contractor
	RequestNewContractor(true,false,false)/*("Registration Request", "Allows an operator to create registration requests to add new contractors.", true, true)*/,
	
	// PICS Score
	PicsScore(false,false,false)/*("PICS Score", "Shows the Contractor's calculated PicsScore", true, true)*/,
	// Contractor Simulator
	ContractorSimulator(false,false,false)/*("Contractor Simulator", "Allows users to preview Audit configurations for operators", true, false)*/,
	ImportPQF(false,false,false)/*("Import PQF", "Users can only view a page listing contractor Import PQFs and PICS PQFs.", true, false)*/,
	
	// REST API
	RestApi(false,false,false)/*("REST API","Only special 'API' users have access to REST API URLs", true, false)*/,

    // SELENIUM TEST Specific
    SeleniumTest(false,false,false)/*("SELENIUM TEST","The user is a Selenium Test",true, false)*/;
//
//	private String description;
//	private String helpText;
	private boolean usesView = true;
	private boolean usesEdit = true;
	private boolean usesDelete = true;
//
//	private boolean forAdmin;
	private boolean forContractor;
//	private boolean forOperator;
//
//	OpPerms(String description, String help, boolean edit, boolean delete) {
//		this.description = description;
//		this.helpText = help;
//		this.usesEdit = edit;
//		this.usesDelete = delete;
//	}
//
	OpPerms(/*String desc, String help, */boolean edit, boolean delete, /*boolean admin, */boolean con/*, boolean op*/) {
//		this.description = desc;
//		this.helpText = help;
//
		this.usesView = true;
		this.usesEdit = edit;
		this.usesDelete = delete;

//		this.forAdmin = admin;
		this.forContractor = con;
//		this.forOperator = op;
	}

//	public static Comparator<OpPerms> PermissionComparator = new Comparator<OpPerms>() {
//		public int compare(OpPerms o1, OpPerms o2) {
//			return o1.getDescription().compareTo(o2.getDescription());
//		}
//	};
//
//	public String getDescription() {
//		return description;
//	}
//
//	public String getHelpText() {
//		return helpText;
//	}
//
	public boolean usesView() {
		return usesView;
	}

	public boolean usesEdit() {
		return usesEdit;
	}

	public boolean usesDelete() {
		return usesDelete;
	}

//	public boolean isForAdmin() {
//		return forAdmin;
//	}
//
	public boolean isForContractor() {
		return forContractor;
	}

//	public boolean isForOperator() {
//		return forOperator;
//	}
//
//	public static List<OpPerms> adminPermissions() {
//		List<OpPerms> adminPermissions = new ArrayList<OpPerms>();
//		for (OpPerms opPerm : values()) {
//			if (opPerm.forAdmin)
//				adminPermissions.add(opPerm);
//		}
//
//		return adminPermissions;
//	}
//
//	public static List<OpPerms> contractorPermissions() {
//		List<OpPerms> contractorPermissions = new ArrayList<OpPerms>();
//		for (OpPerms opPerm : values()) {
//			if (opPerm.forContractor)
//				contractorPermissions.add(opPerm);
//		}
//
//		return contractorPermissions;
//	}
//
//	public static List<OpPerms> operatorPermissions() {
//		List<OpPerms> operatorPermissions = new ArrayList<OpPerms>();
//		for (OpPerms opPerms : values()) {
//			if (opPerms.forOperator)
//				operatorPermissions.add(opPerms);
//		}
//		return operatorPermissions;
//	}
//
//	public String getI18nKey() {
//		return this.getClass().getSimpleName() + "." + this.name();
//	}
//
//	public String getI18nKey(String property) {
//		return getI18nKey() + "." + property;
//	}
//
//	public boolean isNone() {
//		return this == None;
//	}
}
