package com.picsauditing.access;

public class PicsMenu {
	static public MenuComponent getMenu(Permissions permissions) {
		MenuComponent menu = new MenuComponent();

		MenuComponent subMenu;

		if (!permissions.isLoggedIn()) {
			subMenu = menu.addChild("Home", "index.jsp");
			subMenu = menu.addChild("Register", "contractor_new_instructions.jsp");
			subMenu = menu.addChild("Services", "services.jsp");
			subMenu = menu.addChild("Clients", "clients.jsp");
			subMenu = menu.addChild("Contact", "contact.jsp");
			return menu;
		}

		if (permissions.isContractor()) {
			// Don't show a menu for Contractors, they will use their sub menu for now
			subMenu = menu.addChild("Home", "Home.action");
			return menu;
		}

		if (permissions.hasPermission(OpPerms.StatusOnly)) {
			menu.addChild("Contractor List", "ContractorOperatorLimited.action");
			return menu;
		}

		subMenu = menu.addChild("Contractors");
		if (permissions.isPicsEmployee())
			subMenu.addChild("Search", "ContractorListAdmin.action");
		if (permissions.isOperator() || permissions.isCorporate())
			subMenu.addChild("Contractor List", "ContractorListOperator.action");

		if (!permissions.isOnlyAuditor())
			subMenu.addChild("Contact Info", "report_contactInfo.jsp?changed=1");

		if (permissions.hasPermission(OpPerms.SearchContractors))
			subMenu.addChild("Search For New", "contractorsSearch.jsp");
		if (permissions.isAdmin())
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.SearchContractors))
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?searchCorporate=Y");

		subMenu = menu.addChild("Auditing");
		if (permissions.isAuditor()) {
			subMenu.addChild("My Audits", "AuditListAuditor.action");
			subMenu.addChild("My Audit History", "MyAuditHistory.action");
		}
		
		subMenu.addChild("Audit List", "ReportAuditList.action");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign", "AuditAssignments.action?visible=Y");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			subMenu.addChild("Audit Calendar", "audit_calendar.jsp");
		if (permissions.hasPermission(OpPerms.NCMS))
			subMenu.addChild("NCMS Audits", "ReportNCMS.action");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("PQF Verification", "PqfVerification.action?auditStatus=Submitted&filtered=false");

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			subMenu.addChild("Activation", "report_activation.jsp?changed=1");
			subMenu.addChild("Assign Contractors","ContractorAssigned.action");
		}
		if (permissions.hasPermission(OpPerms.EmailAnnualUpdate))
			subMenu.addChild("Annual Updates", "report_annualUpdate.jsp?changed=1");

		subMenu = menu.addChild("Accounting");
		if (permissions.isAdmin() && permissions.hasPermission(OpPerms.BillingUpgrades))
			subMenu.addChild("Contractor Payments", "report_payment.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.BillingUpgrades))
			subMenu.addChild("Upgrade Payments", "report_upgradePayment.jsp?changed=1");

		subMenu = menu.addChild("InsureGuard");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild("Insurance Approval", "report_certificates.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("Verification", "verify_insurance.jsp");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild("Expired Certificates", "report_expiredCertificates.jsp?changed=1");

		subMenu = menu.addChild("Management");
		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild("Approve Contractors", "con_approvals.jsp");
		if (permissions.hasPermission(OpPerms.EditUsers))
			subMenu.addChild("Users", "UsersManage.action");

		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild("Forms &amp Docs", "manage_forms.jsp");
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			subMenu.addChild("Audit Definitions (beta)", "ManageAuditType.action");
			subMenu.addChild("Audit Definitions", "AuditTypeChoose.action");
			subMenu.addChild("Desktop Matrix", "pqf_desktopMatrix.jsp?auditType=Desktop");
			subMenu.addChild("PQF Matrix", "pqf_Matrix.jsp?auditType=PQF");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates))
			subMenu.addChild("Email Templates", "email_templates.jsp");

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperator())
			subMenu.addChild("Flag Criteria", "op_editFlagCriteria.jsp");

		subMenu.addChild("Edit Profile", "ProfileEdit.action");

		subMenu = menu.addChild("Operators");
		if (permissions.hasPermission(OpPerms.ManageCorporate))
			subMenu.addChild("Edit Corporate", "report_accounts.jsp?type=Corporate");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Edit Operators", "report_accounts.jsp?type=Operator");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Assign Audit/Operator", "AuditOperator.action");

		subMenu = menu.addChild("Reports");
		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.EMRReport)) {
			subMenu.addChild("EMR Rates (Graph)", "GraphEmrRates.action");
			subMenu.addChild("EMR Rates", "ReportEmrRates.action");
		}
		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Fatalities", "ReportFatalities.action");
		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Incidence Rates", "ReportIncidenceRate.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");

		return menu;
	}
}
