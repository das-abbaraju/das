package com.picsauditing.access;

import java.util.Iterator;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class PicsMenu {

	static private I18nCache i18nCache = I18nCache.getInstance();

	static private String getText(String key) {
		return i18nCache.getText(key, TranslationActionSupport.getLocaleStatic());
	}

	static private String getTitle(String key) {
		return i18nCache.getText(key + ".title", TranslationActionSupport.getLocaleStatic());
	}

	/**
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
			subMenu = menu.addChild(getText("global.Home"), "index.jsp");
			return menu;
		}

		if (permissions.isContractor()) {
			// Don't show a menu for Contractors, they will use their sub menu
			// for now
			if (permissions.getAccountStatus().isActiveDemo()) {
				subMenu = menu.addChild(getText("global.Home"), "Home.action");

				// Don't show for insurance only users
				if (!permissions.isInsuranceOnlyContractorUser()) {
					subMenu = menu.addChild("Company");
					subMenu.addChild("Our Profile", "ContractorView.action");
					subMenu.addChild("Where We Work", "ContractorFacilities.action");
					subMenu.addChild("Activity Log", "ContractorNotes.action");
				}

				if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
					subMenu = menu.addChild("Edit");
					subMenu.addChild("Company Account", "ContractorEdit.action");
					subMenu.addChild("Users", "UsersManage.action");
					if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
						subMenu.addChild("Employees", "ManageEmployees.action");
					if (permissions.isRequiresCompetencyReview()) {
						subMenu = menu.addChild("HSE Competencies");
						subMenu.addChild("Job Roles", "ManageJobRoles.action");
						subMenu.addChild("Employee Competencies", "EmployeeCompetencies.action");
					}
					if (permissions.isRequiresOQ()) {
						subMenu = menu.addChild("Operator Qualification");
						subMenu.addChild("OQ by Employee", "ReportOQEmployees.action?orderBy=e.lastName,e.firstName");
						subMenu.addChild("Recent OQ Changes", "ReportOQChanges.action");
						subMenu.addChild("Manage/Find New Projects", "ReportNewProjects.action");
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

		if (permissions.isAssessment()) {
			subMenu = menu.addChild("Management");
			subMenu.addChild("Imported Data", "ManageImportData.action");
			subMenu.addChild("Assessment Tests", "ManageAssessmentTests.action");
			subMenu.addChild("Test Mapping", "ManageUnmappedTests.action");
			subMenu.addChild("Assessment Results", "ManageAssessmentResults.action");
			subMenu.addChild("Companies", "ManageMappedCompanies.action");
			subMenu.addChild("Company Mapping", "ManageUnmappedCompanies.action");

			subMenu = menu.addChild("Edit");
			subMenu.addChild("Account", "AssessmentCenterEdit.action");
			subMenu.addChild("Users", "UsersManage.action");

			addSupportLink(menu);
			return menu;
		}

		subMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.hasPermission(OpPerms.AllContractors) || permissions.isOperatorCorporate()) {
			subMenu.addChild(getTitle("ContractorList"), "ContractorList.action?filter.performedBy=Self Performed");
		}

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			subMenu.addChild(getTitle("NewContractorSearch"), url);
		}
		if (permissions.hasPermission(OpPerms.RequestNewContractor))
			addChildAction(subMenu, "ReportNewRequestedContractor");
		if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
			subMenu.addChild(getTitle("BiddingContractorSearch"), "BiddingContractorSearch.action?filter.status=Active");
		}
		if (permissions.isCorporate())
			addChildAction(subMenu, "ReportContractorOperatorFlag");
		if (permissions.isCorporate() || permissions.getCorporateParent().size() > 0)
			addChildAction(subMenu, "ReportContractorOperatorFlagMatrix");
		if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.OperatorFlagMatrix))
			addChildAction(subMenu, "OperatorFlagMatrix");
		if (permissions.hasPermission(OpPerms.DelinquentAccounts)) {
			addChildAction(subMenu, "ArchivedContractorAccounts");
			addChildAction(subMenu, "DelinquentContractorAccounts");
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			addChildAction(subMenu, "QuestionAnswerSearch");

		if (permissions.hasPermission(OpPerms.ContractorWatch))
			addChildAction(subMenu, "ReportActivityWatch");
		if (permissions.hasPermission(OpPerms.WatchListManager))
			addChildAction(subMenu, "WatchListManager");

		subMenu = menu.addChild(getText("global.AuditGUARD"));
		if (permissions.isAuditor()) {
			subMenu.addChild("My Audits",
					"AuditListAuditor.action?filter.auditStatus=Pending&filter.auditStatus=Submitted");
			subMenu.addChild("My Audit History", "MyAuditHistory.action");
		}

		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			subMenu.addChild("Safety Pro Invoices", "AuditorInvoices.action");
			subMenu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action");
		}

		if (permissions.isAdmin())
			subMenu.addChild("Audit List Compress", "ReportAuditList.action");
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			addChildAction(subMenu, "ReportCAOList");
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			addChildAction(subMenu, "ReportCAOByStatusList");

		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign",
					"AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu
					.addChild("Close Assigned Audits",
							"ReportCloseAuditAssignments.action?filter.auditStatus=Submitted");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			subMenu.addChild("Audit Calendar", "AuditCalendar.action");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Answer Updates", "AuditDataUpdates.action");
		if (permissions.isAuditor()) {
			subMenu.addChild("Close Open Reqs", "ReportOpenRequirements.action");
		}
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			subMenu.addChild("Auditor Assignment", "AuditorAssignmentMatrix.action");

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			subMenu.addChild("Assign Contractors", "ContractorAssigned.action");
		}

		if (permissions.hasPermission(OpPerms.ManageWebcam)) {
			subMenu.addChild("Manage Webcams", "ManageWebcams.action?button=out");
			subMenu.addChild("Assign Webcams", "AssignWebcams.action");
		}
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Pending PQF", "ReportCompletePQF.action?filter.auditStatus=Pending");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("PQF Verification", "PqfVerification.action?filter.status=Active"
					+ (permissions.hasGroup(User.GROUP_CSR) ? "&filter.conAuditorId=" + permissions.getShadowedUserID()
							: ""));
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			subMenu.addChild("CSR Assignment", "CSRAssignmentMatrix.action");

		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			subMenu.addChild("Billing Report", "ReportBilling.action?filter.status=Active&filter.status=Pending");
			subMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action");
			subMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action");
			subMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active");
			subMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action");
			subMenu.addChild("QuickBooks Sync", "QBSyncList.action");
			subMenu.addChild("QuickBooks Sync Canada", "QBSyncListCanada.action");
		}

		subMenu = menu.addChild(getText("global.InsureGUARD"));
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
			addChildAction(subMenu, "ReportPolicyList");
		}
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild("Policy Verification", "PolicyVerification.action"
					+ (permissions.hasGroup(User.GROUP_CSR) ? "?filter.conAuditorId=" + permissions.getShadowedUserID()
							: ""));
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild(getTitle("ReportInsuranceApproval"),
							"ReportInsuranceApproval.action?filter.auditStatus=Complete");

		subMenu = menu.addChild(getText("menu.Management"));
		if (permissions.hasPermission(OpPerms.ContractorApproval))
			subMenu.addChild(getTitle("ContractorApproval"), "ContractorApproval.action?filter.workStatus=P");
		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			addChildAction(subMenu, "OperatorTags");
		if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
			addChildAction(subMenu, "UsersManage");
		}

		if (permissions.hasPermission(OpPerms.EditUsers)) {
			addChildAction(subMenu, "UsersManage");
			addChildAction(subMenu, "ReportUserPermissionMatrix");
		}
		
		if (permissions.isAdmin()) {
			subMenu.addChild("Service Taxonomy", "ServiceTaxonomy.action");
		}

		if (permissions.hasPermission(OpPerms.Translator)) {
			subMenu.addChild("Manage Translations", "ManageTranslations.action");
		}

		if (permissions.hasPermission(OpPerms.ManageEmployees))
			subMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action?id=" + permissions.getAccountId());

		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild(getTitle("ManageForms"), "manage_forms.jsp");
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			subMenu.addChild("Audit Definitions", "ManageAuditType.action");
			subMenu.addChild("Flag Criteria", "ManageFlagCriteria.action");
		}
		if (permissions.hasPermission(OpPerms.ContractorSimulator))
			subMenu.addChild("Contractor Simulator", "ContractorSimulator.action");
		if (permissions.hasPermission(OpPerms.ManageAuditTypeRules, OpType.Edit)) {
			subMenu.addChild("Audit Type Rules", "AuditTypeRuleSearch.action");
		}
		if (permissions.hasPermission(OpPerms.ManageCategoryRules, OpType.Edit)) {
			subMenu.addChild("Category Rules", "CategoryRuleSearch.action");
		}
		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)) {
			subMenu.addChild("Audit Category Matrix", "AuditCategoryMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.ManageAuditWorkFlow))
			subMenu.addChild("Manage Workflow", "ManageAuditWorkFlow.action");

		if (permissions.seesAllContractors()) {
			subMenu.addChild("Email Subscriptions", "ReportEmailSubscription.action");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			addChildAction(subMenu, "EmailWizard");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates, OpType.Edit)) {
			addChildAction(subMenu, "EditEmailTemplate");
		}

		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			subMenu.addChild(getTitle("EmailQueueList"), "EmailQueueList.action?filter.status=Pending");
		}

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			subMenu.addChild("Flag Criteria", "ManageFlagCriteriaOperator.action");
			if (permissions.isCanSeeInsurance())
				subMenu.addChild("Insurance Criteria", "ManageInsuranceCriteriaOperator.action");
		}

		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety))
			subMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			subMenu.addChild("Sales Report", "ReportSalesReps.action");
		}
		if (permissions.hasPermission(OpPerms.EditProfile)) {
			addChildAction(subMenu, "ProfileEdit");
		}
		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			subMenu.addChild(getTitle("FacilitiesEdit"), "FacilitiesEdit.action?id=" + permissions.getAccountId());

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
			subMenu.addChild("Server Information", "ServerInfo.action");
			subMenu.addChild("Audit Schedule Builder", "AuditScheduleBuilderCron.action");
			subMenu.addChild("Huntsman Sync", "ContractorCron.action");
			subMenu.addChild("CSS Style Guide", "css.jsp");
			subMenu.addChild("Manage App Properties", "ManageAppProperty.action");
			subMenu.addChild("Exception Log", "ReportExceptions.action");
			subMenu.addChild("Service Indexer", "ServiceIndexer.action");
		}

		subMenu = menu.addChild("Operators");
		if (permissions.hasPermission(OpPerms.ManageCorporate) || permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment))
			subMenu.addChild("Manage Accounts",
					"ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending");

		subMenu = menu.addChild(getText("menu.Reports"));
		
		// TODO - remove these hacks
		if (permissions.getAccountId() == 6228) {
			subMenu.addChild("Site Orientation Report", "report_orientation.jsp");
		}
		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		if (permissions.hasPermission(OpPerms.ManageCategoryRules)
				|| permissions.hasPermission(OpPerms.ManageAuditTypeRules))
			subMenu.addChild("Audit Rule History", "ReportRuleHistory.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		if (permissions.hasPermission(OpPerms.RiskRank))
			subMenu.addChild("Contractor Risk Level", "ReportContractorRiskLevel.action");
		if (permissions.isAdmin() || (permissions.isOperatorCorporate() && permissions.getCorporateParent().contains(10566)))
			subMenu.addChild("Contractor Score", "ReportContractorScore.action");
		if (permissions.hasPermission(OpPerms.EMRReport)) {
			subMenu.addChild(getTitle("GraphEmrRates"), "GraphEmrRates.action?years=2010");
			subMenu.addChild(getTitle("ReportEmrRates"), "ReportEmrRates.action?filter.auditFor=2010");
		}
		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportFatalities.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild(getTitle("ReportFatalities"), url);
		}
		if (permissions.hasPermission(OpPerms.ForcedFlagsReport))
			addChildAction(subMenu, "ReportContractorsWithForcedFlags");

		if (permissions.hasPermission(OpPerms.TRIRReport)) {
			addChildAction(subMenu, "GraphTrirRates");
			final String url = "ReportIncidenceRate.action?filter.auditFor=2009&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild(getTitle("ReportIncidenceRate"), url);
		}

		if (permissions.getAccountName().startsWith("Tesoro"))
			subMenu.addChild("Background Check", "QuestionAnswerSearchByAudit.action");

		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			addChildAction(subMenu, "ReportUntaggedContractors");

		if (permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login", "MultiLoginUser.action");
		if (permissions.hasPermission(OpPerms.EditUsers))
			addChildAction(subMenu, "UserList");
		if (permissions.getAccountId() == 1813) // Hardcode to BP Cherry point
			subMenu.addChild("Washington Audit",
					"ReportWashingtonStateAudit.action?filter.riskLevel=3&filter.waAuditTypes=176");
		if (permissions.hasPermission(OpPerms.EmployeeList))
			addChildAction(subMenu, "EmployeeList");

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR))
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();
			if (permissions.hasGroup(User.GROUP_MARKETING))
				custom = "?filter.accountManager=" + permissions.getUserId();
			subMenu.addChild("Flag Changes", "ReportFlagChanges.action" + custom);
		}

		if (permissions.isRequiresCompetencyReview()) {
			subMenu = menu.addChild("HSE Competencies");

			if (permissions.hasPermission(OpPerms.DefineCompetencies))
				subMenu.addChild("HSE Competencies", "DefineCompetencies.action?id=" + permissions.getAccountId());

			subMenu.addChild("Competency by Account", "ReportCompetencyByAccount.action");
			subMenu.addChild("Competency by Employee", "ReportCompetencyByEmployee.action");
			subMenu.addChild("Employee Turnover", "ReportEmployeeTurnover.action");
		}

		if (permissions.isRequiresOQ()) {
			subMenu = menu.addChild("Operator Qualification");

			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				subMenu.addChild("Job Tasks", "ManageJobTasksOperator.action?id=" + permissions.getAccountId());
			if (permissions.hasPermission(OpPerms.ManageProjects))
				subMenu.addChild("Projects", "ManageProjects.action?id=" + permissions.getAccountId());

			subMenu.addChild("OQ by Company/Site", "ReportOQ.action");
			subMenu.addChild("OQ by Employee", "ReportOQEmployees.action");
		}

		if ((permissions.isOperatorCorporate() && permissions.isRequiresOQ()) || permissions.isAdmin())
			subMenu.addChild("Assessment Tests", "ReportAssessmentTests.action");

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
		MenuComponent subMenu = menu.addChild(getText("menu.Support"));
		subMenu.addChild(getTitle("Contact"), "Contact.action");
		return subMenu;
	}
	
	static private void addChildAction(MenuComponent menu, String actionName) {
		menu.addChild(getTitle(actionName), actionName + ".action");
	}


}
