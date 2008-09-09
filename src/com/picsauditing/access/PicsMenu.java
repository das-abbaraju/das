package com.picsauditing.access;

import java.util.Iterator;

public class PicsMenu {
	
	/**
	 * 
	 * @param menu
	 * @return the url in the first menu item on a menu
	 */
	static public String getHomePage(MenuComponent menu, Permissions permissions) {
		if (permissions.hasPermission(OpPerms.Dashboard))
			return "Home.action";
		
		String url = null;
		if (menu == null || menu.getChildren() == null || menu.getChildren().size() == 0)
			return url;
		
		for(MenuComponent subMenu : menu.getChildren()) {
			url = subMenu.getUrl();
			if (url != null && url.length() > 0)
				return url;
			
			for(MenuComponent subSubMenu : subMenu.getChildren()) {
				url = subSubMenu.getUrl();
				if (url != null && url.length() > 0)
					return url;
			}
		}
		return url;
	}
	
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
			// Don't show a menu for Contractors, they will use their sub menu
			// for now
			subMenu = menu.addChild("Home", "Home.action");
			return menu;
		}

		subMenu = menu.addChild("Contractors");
		if (permissions.isPicsEmployee())
			subMenu.addChild("Search", "ContractorListAdmin.action");
		if (permissions.isOperator() || permissions.isCorporate())
			subMenu.addChild("Contractor List", "ContractorListOperator.action");
		

		if (permissions.hasPermission(OpPerms.ContractorDetails))
			subMenu.addChild("Contact Info", "report_contactInfo.jsp?changed=1");

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			subMenu.addChild("Search For New", "NewContractorSearch.action");
		}
		if (permissions.isAdmin())
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.SearchContractors))
			subMenu.addChild("By Operator", "report_operatorContractor.jsp?searchCorporate=Y");
		if(permissions.hasPermission(OpPerms.DelinquentAccounts))
			subMenu.addChild("Delinquent Accounts", "ArchivedContractorAccounts.action");
//		if(permissions.seesAllContractors())
//			subMenu.addChild("Search By Question","QuestionAnswerSearch.action");

		subMenu = menu.addChild("Auditing");
		if (permissions.isAuditor()) {
			subMenu.addChild("My Audits", "AuditListAuditor.action");
			subMenu.addChild("My Audit History", "MyAuditHistory.action");
		}

		if (permissions.hasPermission(OpPerms.ContractorDetails))
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
			subMenu.addChild("Assign Contractors", "ContractorAssigned.action");
		}
		if (permissions.hasPermission(OpPerms.EmailAnnualUpdate))
			subMenu.addChild("Annual Updates", "report_annualUpdate.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Pending PQF", "ReportCompletePQF.action");

		
		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.BillingUpgrades))
			subMenu.addChild("Contractor Payments", "report_payment.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.BillingUpgrades))
			subMenu.addChild("Upgrade Payments", "report_upgradePayment.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.DelinquentAccounts))
			subMenu.addChild("Delinquent Accounts", "DelinquentContractorAccounts.action");

		subMenu = menu.addChild("InsureGuard");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild("Insurance Approval", "report_certificates.jsp?changed=1");
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("Verification", "verify_insurance.jsp");
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("Expired Certificates", "report_expiredCertificates.jsp?changed=1");

		subMenu = menu.addChild("Management");
		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild("Approve Contractors", "con_approvals.jsp");
		if (permissions.hasPermission(OpPerms.EditUsers))
			subMenu.addChild("Users", "UsersManage.action");
		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild("Forms &amp Docs", "manage_forms.jsp");
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			subMenu.addChild("Audit Definitions", "ManageAuditType.action");
			subMenu.addChild("Desktop Matrix", "ManageDesktopMatrix.action");
			subMenu.addChild("PQF Matrix", "ManagePQFMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates))
			subMenu.addChild("Email Templates", "email_templates.jsp");

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperator())
			subMenu.addChild("Flag Criteria", "op_editFlagCriteria.jsp");

		if (permissions.hasPermission(OpPerms.EditProfile))
			subMenu.addChild("Edit Profile", "ProfileEdit.action");

		subMenu = menu.addChild("Operators");
		if (permissions.hasPermission(OpPerms.ManageCorporate))
			subMenu.addChild("Edit Corporate", "ReportAccountList.action?accountType=Corporate");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Edit Operators", "ReportAccountList.action?accountType=Operator");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Assign Audit/Operator", "AuditOperator.action");

		subMenu = menu.addChild("Reports");
		if (permissions.hasPermission(OpPerms.EditUsers))
			subMenu.addChild("User Search","ReportUsersAccount.action");
		if(permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login","MultiLoginUser.action");
		if (permissions.hasPermission(OpPerms.EMRReport)) {
			subMenu.addChild("EMR Rates (Graph)", "GraphEmrRates.action");
			subMenu.addChild("EMR Rates", "ReportEmrRates.action");
		}
		if (permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Fatalities", "ReportFatalities.action");
		if (permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Incidence Rates", "ReportIncidenceRate.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		
		// Convert the first submenu into a menu if only one exists
		Iterator<MenuComponent> iterator = menu.getChildren().iterator();
		while (iterator.hasNext()) {
			MenuComponent nextMenu = iterator.next();
			if (nextMenu.getChildren().size() == 0)
				iterator.remove();
		}
		if (menu.getChildren().size() == 1)
			return menu.getChildren().get(0);
		// End of conversion
		
		return menu;
	}
}
