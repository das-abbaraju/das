package com.picsauditing.access;

import java.util.ArrayList;
import java.util.List;

public enum OpPerms {
	// Development Permission
	DevelopmentEnvironment("Development Environment", "Allow PICS employees to test.", true, true, true, false, false),

	// Default User Permissions
	EditProfile("Edit Profile", "Allow users to Edit their own Profile.", true, false, true, true, true),
	EditAccountDetails("Edit Account Details", "Allow users to Edit their own Account Information.", true, false, false, true, true),
	RiskRank("Edit Contractor Risk Level", "Allows PICS users to manually edit the contractor's Risk Level.", true, true, true, false, false),
	Dashboard("Dashboard", "Allows users to view and customize (future) their home page/dashboard.", true, false, true, true, true),
	ContractorDetails("Contractor Details", "Allows users to view the details page along with sub nav bar, also enables search criteria other than name on reports. Allows access to the contractor contact info report and the audit list report.", false, false, true, true, true),

	FormsAndDocs("Manage Forms and Docs", "Allow users to Edit and Delete Forms and Documents. Edit is required to Add or Update forms. Delete is required to delete forms.", true, true, true, false, true),

	// Adding/removing Contractors
	SearchContractors("Search For New Contractors", "Allows operator and corporate account users to search for contractors for their facility's database. Also allows you to view the contractor detail page.", true, true, true, false, true),
	AddContractors("Add Contractors", "Allows operator and corporate account users to add new contractors to their facility's database. Requires the [Search For New Contractors] permission.", false, false, true, false, true),
	RemoveContractors("Remove Contractors", "Allows operator and corporate account users to remove existing contractors from their facility's database", false, false, true, false, true),

	// Approving Contractors
	ContractorApproval("Approve Contractors", "Allows a user to change a Contractor's work status", true, false, true, false, true),
	ViewUnApproved("View UnApproved Contractors", "Allows users to view contractors who are NOT yet approved for work for a facility. Note: the facility must track that information for this option to apply", false, false, true, false, true),
	ViewTrialAccounts("View Bid Only Account Contractors", "Allows users to approve or reject contractors who are bidding for work at their facility.", true, false, true, false, true),

	// Red Flag
	EditFlagCriteria("Manage Flag Criteria", "Allows users to view or edit the criteria for flagging contractors Red or Amber.", true, false, true, false, true),
	EditForcedFlags("Force Flag Color", "Allows users to force a contractor's flag color for a given period of time. Only Read access is used currently.", true, false, true, false, true),

	// TODO - consider removing this completely
	EditNotes("Contractor Notes", "Allows users to add notes to a contractor's account. Allows Users to view/add/edit/delete notes.", true, true, true, false, true),

	// Reports
	ContractorLicenseReport("Contractor License Report", "List all contractor licenses", false, false, true, false, true),
	FatalitiesReport("Fatalities Report", "List contractors with fatalities by year", false, false, true, false, true),
	EMRReport("EMR Report", "List contractor EMRs by year", false, false, true, false, true),
	TRIRReport("Incidence Rate Report", "List contractor incidence rates by year", false, false, true, false, true),
	ForcedFlagsReport("Forced Flags Report", "List contractors with Forced Flags.", false, false, true, false, true),

	// Can view/edit/delete all contractors in the database ... replaces isAdmin
	// These are usually used in tandem with another permission
	AllContractors("All Contractors", "Can view all contractors in the database", false, false, true, false, false),
	AllOperators("All Operators", "Can view all operators in the database", false, false, true, false, false),

	// Account Management
	ManageOperators("Manage Operator Accounts", "Allows PICS employees to view/edit/delete Operator accounts", true, true, true, false, false),
	EmailOperators("Send Operator Emails", "Allows PICS employees to send emails to operators accounts", false, false, true, false, false),
	ManageCorporate("Manage Corporate Accounts", "Allows PICS employees to view/edit/delete Corporate accounts", true, true, true, false, false),
	ManageAssessment("Manage Assessment Center Accounts", "Allows PICS employees to view/edit/delete Assessment Center accounts", true, true, true, false, false),
	ContractorAccounts("Administer Contractor Accounts", "Allow PICS employees to create/update/delete contractor accounts", true, true, true, false, false),
	ContractorActivation("Activate Contractor Accounts", "Allow PICS employees to view/activate/remove contractor accounts during the activation phase", true, true, true, false, false),
	EditUsers("Manage User Accounts", "Allows administrators to view/add/update/delete users for their account", true, true, true, true, true),
	EditUsersPics("Manage PICS User Accounts", "Edit user accounts on the PICS Admin account", true, true, true, false, false),
	// Audits
	ManageAudits("Manage Audits and Matrices", "Allow PICS employees view and edit the Audit questions and matrix", true, true, true, false, false),
	AssignAudits("Audit Assignment", "Allows users to assign implementation Audit, manual Audit, and other audits to PICS Safety Professionals", true, false, true, false, false),
	AuditVerification("Audit Verification", "Verify answers to OSHA, EMR, and other Audit data ", true, false, true, false, false),
	OfficeAuditCalendar("Implementation Audit Calendar", "Allows users to view the Implementation Audit Calendar", false, false, true, false, true),
	ViewFullPQF("View PQF Work History", "Can view the PQF category Work History, which contains financial and other sensitive information", false, false, true, false, false),
	AuditEdit("Contractor Audit (System)", "Can view and edit all fields related to an audit", true, true, true, false, false),
	AuditCopy("Audit Copy", "Can Copy Audit from one Contractor to another Contractor", false, false, true, false, false),
	MyCalendar("My Calendar", "View and Edit your Calendar", true, true, true, false, false),
	Holidays("Holidays", "Manage the Holiday schedule for PICS", false, false, true, false, false),
	ManageCalendars("Manage Safety Professional Calendars", "Allows user to edit all Safety Professional calendars", true, true, true, false, false),
	ManageCategoryRules("Manage Category Rules", "Allows user to view and edit Category Rules for Audits and other documents", true, true, true, false, false),
	ManageAuditTypeRules("Manage AuditType Rules", "Allows user to view and edit Audit Type Rules for Audits and other documents", true, true, true, false, false),

	// Insurance
	InsuranceCerts("Insurance Certificates", "Allows users to view (Read), upload (Edit), and delete insurance certificates", true, true, true, false, true),
	InsuranceVerification("Insurance Verification", "Allows users to view the 'Insurance Verification' and 'Expired Insurance Certificates' reports (Read), and verify (Edit) the certificates", true, false, true, false, false),
	InsuranceApproval("Insurance Approval", "Allows users to view the Insurance Approval report (Read), and approve/reject (Edit) the insurance certificates", true, false, true, false, true),

	Billing("Billing", "View Billing Details and Process Invoices", true, true, true, false, false),
	// Will be replaced with billing; remove from Accounting Group
	BillingUpgrades("Billing Upgrades", "Upgrade contractor subscriptions and send them invoices", true, false, true, false, false),
	DelinquentAccounts("Delinquent Accounts", "Allows users to view Delinquent Contractor Accounts", false, false, true, false, true),
	InvoiceEdit("Edit Invoice", "Can view and edit all fields related to an invoice", true, true, true, false, false),

	ContractorTags("Contractor Tags", "Allow operators to tag and categorize their contractors into searchable groups.", true, true, true, false, true),
	EmailAnnualUpdate("Email Annual Updates", "Send contractors reminder emails to update their PICS info. Edit permission is required to send the emails.", true, false, true, false, true),
	EmailTemplates("Email Templates", "Allows PICS employees to edit the templates used to automatically send emails", true, true, true, false, false),
	EmailQueue("Email Queue", "Allow PICS Employees to view the report for Email Queue", true, true, true, false, true),
	SwitchUser("Switch to User", "Can auto login as another user", false, false, true, false, false),

	ManageWebcam("Webcam Management", "Can view and edit PICS' inventory of web cameras", true, true, true, false, false),
	AuditorPayments("Safety Professional Payments", "Create and manage independent contractor payments for audits.", true, true, true, false, false),

	// Manage User Account Roles
	UserRolePicsOperator("User Roles PICS Operator", "Allow PICS users to assign the users to a Role.", true, true, true, false, false),

	// All of the contractor permissions
	ContractorAdmin("Admin", "Account, Users, Activation", false, false, false, true, false),
	ContractorSafety("Safety", "PQF, Annual Updates, Audits, etc", false, false, false, true, false),
	ContractorBilling("Billing", "Invoices, Payments, Credit Card", false, false, false, true, false),
	ContractorInsurance("Insurance", "Insurance Certificates", false, false, false, true, false),
	
	// Operator Qualification
	ManageProjects("Manage Projects", "Allows operators to view or edit their project sites.", true, true, false, false, true),
	ManageJobTasks("Manage Job Tasks", "Allows operators to view or edit their job tasks.", true, true, false, false, true),
	
	// View the Operator Flag Matrix report
	OperatorFlagMatrix("Operator Flag Matrix", "Allows operators to view the Operator Flag Matrix report.", false, false, false, false, true),

	// Employee Management
	DefineRoles("Define Roles", "Allows contractors to create job roles to assign to their employees.", true, true, true, true, false),
	DefineCompetencies("Define Competencies", "Allows operators to create compentency requirements for satisfying job roles.", true, true, true, false, true),
	ManageEmployees("Manage Employees", "Allows Contractors to view or edit their employees.", true, true, true, false, true),
	EmployeeList("Employee List", "Allows operators and administrators to view the employee list.", true, true, true, false, true),
	
	// Contractor watch
	ContractorWatch("Contractor Watch", "Allows operators to view the activity of watched contractors.", true, true, true, false, true),
	WatchListManager("Watch List Manager", "Allows managers to add and remove users from watch lists.", true, true, true, false, true),
	
	// Request New Contractor
	RequestNewContractor("Registration Request", "Allows an operator to create registration requests to add new contractors.", true, false, true, false, true);
	
	private String description;
	private String helpText;
	private boolean usesView = true;
	private boolean usesEdit = true;
	private boolean usesDelete = true;

	private boolean forAdmin;
	private boolean forContractor;
	private boolean forOperator;

	OpPerms(String description, String help, boolean edit, boolean delete) {
		this.description = description;
		this.helpText = help;
		this.usesEdit = edit;
		this.usesDelete = delete;
	}

	OpPerms(String desc, String help, boolean edit, boolean delete, boolean admin, boolean con, boolean op) {
		this.description = desc;
		this.helpText = help;

		this.usesView = true;
		this.usesEdit = edit;
		this.usesDelete = delete;

		this.forAdmin = admin;
		this.forContractor = con;
		this.forOperator = op;
	}

	public String getDescription() {
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

	public boolean isForAdmin() {
		return forAdmin;
	}

	public boolean isForContractor() {
		return forContractor;
	}

	public boolean isForOperator() {
		return forOperator;
	}

	public static List<OpPerms> adminPermissions() {
		List<OpPerms> adminPermissions = new ArrayList<OpPerms>();
		for (OpPerms opPerm : values()) {
			if (opPerm.forAdmin)
				adminPermissions.add(opPerm);
		}

		return adminPermissions;
	}

	public static List<OpPerms> contractorPermissions() {
		List<OpPerms> contractorPermissions = new ArrayList<OpPerms>();
		for (OpPerms opPerm : values()) {
			if (opPerm.forContractor)
				contractorPermissions.add(opPerm);
		}

		return contractorPermissions;
	}

	public static List<OpPerms> operatorPermissions() {
		List<OpPerms> operatorPermissions = new ArrayList<OpPerms>();
		for (OpPerms opPerms : values()) {
			if (opPerms.forOperator)
				operatorPermissions.add(opPerms);
		}
		return operatorPermissions;
	}

}
