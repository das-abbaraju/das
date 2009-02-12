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
			return menu;
		}

		if (permissions.isContractor()) {
			// Don't show a menu for Contractors, they will use their sub menu
			// for now
			if (permissions.isAccountActive())
				subMenu = menu.addChild("Home", "Home.action");
			else
				subMenu = menu.addChild("Edit Account", "ContractorEdit.action");
			return menu;
		}

		subMenu = menu.addChild("Contractors");
		subMenu.addChild("Contractor List", "ContractorList.action");

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			subMenu.addChild("Search For New", "NewContractorSearch.action?filter.performedBy=Self Performed");
		}
		if (permissions.isCorporate() || permissions.getCorporateParent().size() > 0)
			subMenu.addChild("By Operator", "ReportContractorOperatorFlagMatrix.action");
		if(permissions.hasPermission(OpPerms.DelinquentAccounts)) {
			subMenu.addChild("Archived Accounts", "ArchivedContractorAccounts.action");
			subMenu.addChild("Delinquent Accounts", "DelinquentContractorAccounts.action");
		}	
		if(permissions.hasPermission(OpPerms.ContractorDetails))
			subMenu.addChild("Search By Question","QuestionAnswerSearch.action");

		subMenu = menu.addChild("Auditing");
		if (permissions.isAuditor()) {
			subMenu.addChild("My Audits", "AuditListAuditor.action");
			subMenu.addChild("My Audit History", "MyAuditHistory.action");
		}

		if (permissions.hasPermission(OpPerms.ContractorDetails))
			subMenu.addChild("Audit List", "ReportAuditList.action");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign", "AuditAssignments.action?filter.visible=Y");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			subMenu.addChild("Audit Calendar", "audit_calendar.jsp");
		if (permissions.hasPermission(OpPerms.NCMS))
			subMenu.addChild("NCMS Audits", "ReportNCMS.action");
		if(permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Answer Updates", "AuditDataUpdates.action");

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			subMenu.addChild("Assign Contractors", "ContractorAssigned.action");
		}
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Pending PQF", "ReportCompletePQF.action");
		if (permissions.hasPermission(OpPerms.AuditVerification)) 
			subMenu.addChild("PQF Verification", "PqfVerification.action?filter.visible=Y");

		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			subMenu.addChild("Billing Report", "ReportBilling.action");
			subMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action");
		}

		subMenu = menu.addChild("InsureGuard");
		if(permissions.hasPermission(OpPerms.InsuranceCerts))
			subMenu.addChild("Contractor Policies", "ReportPolicyList.action");
		if(permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("InsureGuard Verification", "PolicyVerification.action?filter.visible=Y&filter.auditStatus=Submitted&filter.auditStatus=Resubmitted");
		if (permissions.hasPermission(OpPerms.InsuranceApproval)) {
			subMenu.addChild("Policies Awaiting Decision", "ReportInsuranceApproval.action?filter.caoStatus=Awaiting");
			subMenu.addChild("Recently Updated Policies", "ReportUpdatedPolicies.action?filter.hasClosedDate=true&filter.caoStatus=Approved&filter.caoStatus=Rejected&filter.auditStatus=Resubmitted");
		}
		
		subMenu = menu.addChild("Management");
		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild("Approve Contractors", "con_approvals.jsp");
		if (permissions.hasPermission(OpPerms.EditUsers)) {
			subMenu.addChild("Users", "UsersManage.action");
			subMenu.addChild("User Permissions Matrix", "ReportUserPermissionMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild("Forms &amp Docs", "manage_forms.jsp");
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			subMenu.addChild("Audit Definitions", "ManageAuditType.action");
			subMenu.addChild("Desktop Matrix", "ManageDesktopMatrix.action");
			subMenu.addChild("PQF Matrix", "ManagePQFMatrix.action");
		}
		
		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			subMenu.addChild("Email Wizard", "EmailWizard.action");
		}
		
		if (permissions.hasPermission(OpPerms.EmailQueue))
			subMenu.addChild("Email Queue", "EmailQueueList.action");		

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperator())
			subMenu.addChild("Flag Criteria", "op_editFlagCriteria.jsp");

		if (permissions.hasPermission(OpPerms.EditProfile))
			subMenu.addChild("Edit Profile", "ProfileEdit.action");

		subMenu = menu.addChild("Operators");
		if (permissions.hasPermission(OpPerms.ManageCorporate))
			subMenu.addChild("Edit Corporate", "ReportAccountList.action?accountType=Corporate");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Edit Operators", "ReportAccountList.action?accountType=Operator");
		if (permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit))
			subMenu.addChild("Assign Audit/Operator", "AuditOperator.action");
		
		subMenu = menu.addChild("Reports");
		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		if (permissions.hasPermission(OpPerms.EMRReport)) {
			subMenu.addChild("EMR Rates (Graph)", "GraphEmrRates.action");
			subMenu.addChild("EMR Rates", "ReportEmrRates.action?filter.auditFor=2008&filter.auditStatus=Active");
		}
		if (permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Fatalities", "ReportFatalities.action?filter.auditStatus=Active");
		subMenu.addChild("Operator Flag Criteria", "ReportOperatorCriteria.action?filter.flagStatus=Red");
		if (permissions.hasPermission(OpPerms.ForcedFlagsReport))
			subMenu.addChild("Forced Flags", "ReportContractorsWithForcedFlags.action");
		if (permissions.hasPermission(OpPerms.FatalitiesReport))
			subMenu.addChild("Incidence Rates", "ReportIncidenceRate.action?filter.auditFor=2008&filter.auditStatus=Active");
		if(permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login","MultiLoginUser.action");
		if (permissions.hasPermission(OpPerms.EditUsers))
			subMenu.addChild("User Search","UserList.action");
		
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
