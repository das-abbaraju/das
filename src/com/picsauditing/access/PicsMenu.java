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
		
		if (permissions.hasPermission(OpPerms.StatusOnly)) {
			menu.addChild("Contractor List", "ContractorOperatorLimited.action");
			return menu;
		}

		subMenu = menu.addChild("Contractors");
		if (permissions.isPicsEmployee())
			subMenu.addChild("Search", "ContractorListAdmin.action");
		if (permissions.isOperator() || permissions.isCorporate())
			subMenu.addChild("Contractor List", "ContractorListOperator.action");

		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild("Contact Info", "report_contactInfo.jsp?changed=1");

		if (permissions.hasPermission(OpPerms.SearchContractors))
			subMenu.addChild("Search For New", "contractorsSearch.jsp");
		if (permissions.isAdmin())
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.SearchContractors))
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?searchCorporate=Y");

		subMenu = menu.addChild("Auditing");
		if (permissions.isAuditor())
			subMenu.addChild("My Audits", "AuditListAuditor.action");
		
		subMenu.addChild("Audit List", "ReportAuditList.action");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign", "AuditAssignments.action");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			subMenu.addChild("Audit Calendar", "audit_calendar.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.NCMS))
			subMenu.addChild("NCMS Audits", "ReportNCMS.action");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("PQF Verification", "pqf_verification.jsp");

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin())
			subMenu.addChild("Activation", "report_activation.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.EmailAnnualUpdate))
			subMenu.addChild("Annual Updates", "report_annualUpdate.jsp?changed=1");

		subMenu = menu.addChild("Accounting");
		if (permissions.isAdmin())
			subMenu.addChild("Contractor Payments", "report_payment.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.BillingUpgrades))
			subMenu.addChild("Upgrade Payments", "report_upgradePayment.jsp?changed=1");

		subMenu = menu.addChild("InsureGuard");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild("Insurance Certificates", "report_certificates.jsp?changed=1");
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
			subMenu.addChild("Audit Definitions", "AuditTypeChoose.action");
			subMenu.addChild("Desktop Matrix", "pqf_desktopMatrix.jsp");
			subMenu.addChild("Re-gen PQF Cats", "pqf_regeneratePQFCategories.jsp");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates))
			subMenu.addChild("Email Templates", "email_templates.jsp");

		if (permissions.hasPermission(OpPerms.EditFlagCriteria))
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
		if (!permissions.isContractor())
			subMenu.addChild("EMR Rates", "report_EMRRates.jsp?changed=1");
		if (!permissions.isContractor())
			subMenu.addChild("Fatalities", "report_fatalities.jsp?changed=1");
		if (!permissions.isContractor())
			subMenu.addChild("Incidence Rates", "report_incidenceRates.jsp?changed=1");

		// TODO get rid of these before we go live
		if (permissions.isAdmin()) {
			subMenu = menu.addChild("Obsolete");
			subMenu.addChild("Audit Dates Report", "report_audits.jsp?changed=1");
			subMenu.addChild("Expired Audits Report", "report_expiredAudits.jsp?changed=1");
			subMenu.addChild("Incomplete Audits Report", "report_incompleteAudits.jsp?incompleteAfter=3&changed=1");
			subMenu.addChild("Reschedule Audits", "report_scheduleAudits.jsp?changed=1&which="
					+ com.picsauditing.PICS.SearchBean.RESCHEDULE_AUDITS);
			subMenu.addChild("Schedule D&amp;A Audits", "report_daAudit.jsp");
			subMenu.addChild("Schedule Desktop Audits", "report_desktop.jsp");
			subMenu.addChild("Schedule Office Audits", "report_scheduleAudits.jsp?changed=1");
		}

		return menu;
	}
}
