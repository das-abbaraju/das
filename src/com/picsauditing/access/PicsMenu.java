package com.picsauditing.access;

import java.util.Iterator;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

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

		for (MenuComponent subMenu : menu.getChildren()) {
			url = subMenu.getUrl();
			if (!Strings.isEmpty(url))
				return url;

			for (MenuComponent subSubMenu : subMenu.getChildren()) {
				url = subSubMenu.getUrl();
				if (!Strings.isEmpty(url))
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
			if (permissions.getAccountStatus().isActiveDemo()) {
				subMenu = menu.addChild("Home", "Home.action");

				subMenu = menu.addChild("Company");
				subMenu.addChild("Our Profile", "ContractorView.action");
				subMenu.addChild("Where We Work", "ContractorFacilities.action");
				subMenu.addChild("Activity Log", "ContractorNotes.action");

				if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
					subMenu = menu.addChild("Edit");
					subMenu.addChild("Company Account", "ContractorEdit.action");
					subMenu.addChild("Users", "UsersManage.action");
					if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
						subMenu.addChild("Employees", "ManageEmployees.action");
					if (permissions.isRequiresCompetencyReview()) {
						subMenu.addChild("Job Roles", "ManageJobRoles.action");
						subMenu.addChild("Employee Competencies", "EmployeeCompetencies.action");
					}
				}

				if (permissions.hasPermission(OpPerms.ContractorBilling)) {
					subMenu = menu.addChild("Billing");
					subMenu.addChild("Billing Details", "BillingDetail.action");
					subMenu.addChild("Payment Options", "ContractorPaymentOptions.action");
				}

				menu.addChild("Forms &amp; Docs", "ContractorForms.action");

				subMenu = addSupportLink(menu);
				subMenu.addChild("Edit Profile", "ProfileEdit.action");

			} else {
				subMenu = menu.addChild("Edit Account", "ContractorEdit.action");
				addSupportLink(menu);
			}
			return menu;
		}

		subMenu = menu.addChild("Contractors");
		subMenu.addChild("Contractor List", "ContractorList.action?filter.performedBy=Self Performed");

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			subMenu.addChild("Search For New", url);
		}
		if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
			subMenu.addChild("Bid Only Account", "BiddingContractorSearch.action");
		}
		if (permissions.isCorporate())
			subMenu.addChild("By Flag", "ReportContractorOperatorFlag.action");
		if (permissions.isCorporate() || permissions.getCorporateParent().size() > 0)
			subMenu.addChild("By Operator", "ReportContractorOperatorFlagMatrix.action");
		if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.OperatorFlagMatrix))
			subMenu.addChild("Flag Criteria by Contractor", "OperatorFlagMatrix.action");
		if (permissions.hasPermission(OpPerms.DelinquentAccounts)) {
			subMenu.addChild("Archived Accounts", "ArchivedContractorAccounts.action");
			subMenu.addChild("Delinquent Accounts", "DelinquentContractorAccounts.action");
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			subMenu.addChild("Search By Question", "QuestionAnswerSearch.action");

		if (permissions.isPicsEmployee()) // ||
			// permissions.isOperatorCorporate()
			subMenu.addChild("Request For New", "ReportNewRequestedContractor.action");

		subMenu = menu.addChild("Auditing");
		if (permissions.isAuditor()) {
			subMenu.addChild("My Audits", "AuditListAuditor.action");
			subMenu.addChild("My Audit History", "MyAuditHistory.action");
		}

		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			subMenu.addChild("Safety Pro Invoices", "AuditorInvoices.action");
			subMenu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action");
		}

		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			final String url = "ReportAuditList.action?filter.auditStatus=Submitted&filter.auditStatus=Resubmitted&filter.auditStatus=Active";
			subMenu.addChild("Audit List", url);
		}
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign", "AuditAssignments.action?filter.status=Active");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Close Assigned Audits", "ReportCloseAuditAssignments.action");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			subMenu.addChild("Audit Calendar", "AuditCalendar.action");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Answer Updates", "AuditDataUpdates.action");
		if (permissions.isAuditor()) {
			subMenu.addChild("Close Open Reqs", "ReportOpenRequirements.action");
		}

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			subMenu.addChild("Assign Contractors", "ContractorAssigned.action");
		}

		if (permissions.hasPermission(OpPerms.ManageWebcam)) {
			subMenu.addChild("Manage Webcams", "ManageWebcams.action");
			subMenu.addChild("Assign Webcams", "AssignWebcams.action");
		}
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Pending PQF", "ReportCompletePQF.action");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("PQF Verification", "PqfVerification.action?filter.status=Active");

		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			subMenu.addChild("Billing Report", "ReportBilling.action");
			subMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action");
			subMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action");
			subMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active");
			subMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action");
			subMenu.addChild("QuickBooks Sync", "QBSyncList.action");
		}

		subMenu = menu.addChild("InsureGUARD&trade;");
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
			final String url = "ReportPolicyList.action?filter.caoStatus=Approved&filter.caoStatus=Rejected&filter.caoStatus=Verified&filter.caoStatus=Submitted";
			subMenu.addChild("Contractor Policies", url);
		}
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("Policy Verification", "PolicyVerification.action");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild("Policies Awaiting Decision", "ReportInsuranceApproval.action?filter.caoStatus=Verified");

		subMenu = menu.addChild("Management");
		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild("Approve Contractors", "ContractorApproval.action?filter.workStatus=P");
		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			subMenu.addChild("Contractor Tags", "OperatorTags.action");
		if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
			subMenu.addChild("Users", "UsersManage.action");
		}

		if (permissions.hasPermission(OpPerms.EditUsers)) {
			subMenu.addChild("Users", "UsersManage.action");
			subMenu.addChild("User Permissions Matrix", "ReportUserPermissionMatrix.action");
		}
		
		if (permissions.isOperatorCorporate()) {
			if (permissions.hasPermission(OpPerms.DefineCompetencies))
				subMenu.addChild("Define Competencies", "DefineCompetencies.action?id=" + permissions.getAccountId());

			if (permissions.hasPermission(OpPerms.ManageJobSites))
				subMenu.addChild("Job Sites", "ManageJobSites.action?id=" + permissions.getAccountId());
			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				subMenu.addChild("Job Tasks", "ManageJobTasksOperator.action?id=" + permissions.getAccountId());
		}

		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild("Forms &amp; Docs", "manage_forms.jsp");
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			subMenu.addChild("Audit Definitions", "ManageAuditType.action");
			subMenu.addChild("Manual Audit Matrix", "ManageDesktopMatrix.action");
			subMenu.addChild("PQF Matrix", "ManagePQFMatrix.action");
			subMenu.addChild("Flag Criteria", "ManageFlagCriteria.action");
		}

		if (permissions.seesAllContractors()) {
			subMenu.addChild("Email Subscriptions", "ReportEmailSubscription.action");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			subMenu.addChild("Email Wizard", "EmailWizard.action");
		}

		if (permissions.hasPermission(OpPerms.EmailQueue))
			subMenu.addChild("Email Queue", "EmailQueueList.action");

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			subMenu.addChild("Flag Criteria", "ManageFlagCriteriaOperator.action");
			// Visible CAOs uses visible inherited insurance criteria
			if (permissions.getVisibleCAOs().size() > 0)
				subMenu.addChild("Insurance Criteria", "ManageInsuranceCriteriaOperator.action");
		}

		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety))
			subMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			subMenu.addChild("Sales Report", "ReportSalesReps.action");
		}
		if (permissions.hasPermission(OpPerms.EditProfile)) {
			subMenu.addChild("Edit Profile", "ProfileEdit.action");
		}
		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			subMenu.addChild("Edit Account", "FacilitiesEdit.action?id=" + permissions.getAccountId());

		// Add a new permission for this
		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			subMenu.addChild("My Schedule", "MySchedule.action");
		}

		if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			subMenu = menu.addChild("Dev");
			subMenu.addChild("System Logging", "LoggerConfig.action");
			subMenu.addChild("Page Logging", "PageLogger.action");
			subMenu.addChild("Clear Cache", "ClearCache.action");
			subMenu.addChild("Cron", "Cron.action");
			subMenu.addChild("Contractor Cron", "ContractorCron.action");
			subMenu.addChild("Con/Op Flag Differences", "ContractorFlagDifference.action");
			subMenu.addChild("Mail Cron", "MailCron.action");
			subMenu.addChild("Subscription Cron", "SubscriptionCron.action");
			subMenu.addChild("Audit Schedule Builder", "AuditScheduleBuilderCron.action");
			subMenu.addChild("Huntsman Sync", "ContractorCron.action");
			subMenu.addChild("CSS Style Guide", "css.jsp");
			subMenu.addChild("Manage App Properties", "ManageAppProperty.action");
		}

		subMenu = menu.addChild("Operators");
		if (permissions.hasPermission(OpPerms.ManageCorporate))
			subMenu.addChild("Edit Corporate", "ReportAccountList.action?accountType=Corporate");
		if (permissions.hasPermission(OpPerms.ManageOperators))
			subMenu.addChild("Edit Operators", "ReportAccountList.action?accountType=Operator");
		if (permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit))
			subMenu.addChild("Assign Audit/Operator", "AuditOperator.action");

		subMenu = menu.addChild("Reports");
		// TODO - remove these hacks
		if (permissions.getAccountId() == 10569 || permissions.getAccountId() == 10565) {
			subMenu.addChild("Orientation Video Report", "report_orientation.jsp");
		}
		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		if (permissions.hasPermission(OpPerms.RiskRank))
			subMenu.addChild("Contractor Risk Level", "ReportContractorRiskLevel.action");
		if (permissions.hasPermission(OpPerms.EMRReport)) {
			subMenu.addChild("EMR Rates (Graph)", "GraphEmrRates.action?years=2009");
			subMenu.addChild("EMR Rates", "ReportEmrRates.action?filter.auditFor=2009");
		}
		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportFatalities.action?filter.auditFor=2009&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild("Fatalities", url);
		}
		if (permissions.hasPermission(OpPerms.ForcedFlagsReport))
			subMenu.addChild("Forced Flags", "ReportContractorsWithForcedFlags.action");
		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportIncidenceRate.action?filter.auditFor=2009&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild("Incidence Rates", url);
		}

		if (permissions.getAccountName().startsWith("Tesoro"))
			subMenu.addChild("Background Check", "QuestionAnswerSearchByAudit.action");

		if (permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login", "MultiLoginUser.action");
		if (permissions.hasPermission(OpPerms.EditUsers))
			subMenu.addChild("User Search", "UserList.action");

		if (permissions.isRequiresCompetencyReview()) {
			subMenu.addChild("Competency by Account", "ReportCompetencyByAccount.action");
			subMenu.addChild("Competency by Employee", "ReportCompetencyByEmployee.action");
		}

		if (permissions.isRequiresOQ()) {
			subMenu.addChild("OQ by Contractor/Site", "ReportOQ.action");
			subMenu.addChild("OQ by Employee", "ReportOQEmployees.action");
		}

		if (permissions.isOperatorCorporate()) {
			addSupportLink(menu);
		}
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

	static public MenuComponent addSupportLink(MenuComponent menu) {
		MenuComponent subMenu = menu.addChild("Support");
		subMenu.addChild("Contact Us", "Contact.action");
		return subMenu;
	}
}
