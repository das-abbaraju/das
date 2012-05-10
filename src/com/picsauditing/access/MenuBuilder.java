package com.picsauditing.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * This is a rewrite of PicsMenu.java for the version 7.0
 */
public class MenuBuilder {
	private I18nCache i18nCache = I18nCache.getInstance();
	private MenuComponent menu = new MenuComponent();

	public String getHomePage(MenuComponent menu, Permissions permissions) {
		if (permissions.isContractor())
			return "ContractorView.action";
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

	public void buildNotLoggedIn() {
		menu.addChild(getText("global.Home"), "index.jsp");
	}

	public void buildContractorMenu(Permissions permissions) {
		MenuComponent subMenu;
		// Don't show a menu for Contractors, they will use their sub menu for now
		if (!permissions.getAccountStatus().isActiveDemo()) {
			subMenu = menu.addChild(getText("Registration.CompanyDetails.heading"), "ContractorEdit.action");
			addSupportLink(menu);
			return;
		}

		subMenu = menu.addChild(getText("global.Home"), "ContractorView.action");

		// Don't show for insurance only users
		if (!permissions.isInsuranceOnlyContractorUser()) {
			subMenu = menu.addChild(getText("global.Company"));
			subMenu.addChild(getText("menu.Contractor.WhereWeWork"), "ContractorFacilities.action");
			subMenu.addChild(getText("menu.Contractor.ActivityLog"), "ContractorNotes.action");
		}

		if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
			subMenu = menu.addChild(getText("button.Edit"));
			subMenu.addChild(getText("menu.Contractor.CompanyAccount"), "ContractorEdit.action");
			subMenu.addChild(getText("global.Users"), "UsersManage.action");
			subMenu.addChild(getTitle("ContractorTrades"), "ContractorTrades.action");
			if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
				subMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action");
			if (permissions.isRequiresCompetencyReview()) {
				subMenu = menu.addChild(getText("global.HSECompetencies"));
				subMenu.addChild(getTitle("ManageJobRoles"), "ManageJobRoles.action");
				subMenu.addChild(getTitle("EmployeeCompetencies"), "EmployeeCompetencies.action");
			}
			if (permissions.isRequiresOQ()) {
				subMenu = menu.addChild(getText("global.OperatorQualification"));
				subMenu.addChild(getTitle("ReportOQEmployees"),
						"ReportOQEmployees.action?orderBy=e.lastName,e.firstName");
				subMenu.addChild(getTitle("ReportOQChanges"), "ReportOQChanges.action");
				subMenu.addChild(getTitle("ReportNewProjects"), "ReportNewProjects.action");
			}
		}

		if (permissions.hasPermission(OpPerms.ContractorBilling)) {
			subMenu = menu.addChild(getText("menu.Billing"));
			subMenu.addChild(getText("menu.Contractor.BillingDetails"), "BillingDetail.action");
			subMenu.addChild(getText("menu.Contractor.PaymentOptions"), "ContractorPaymentOptions.action");
		}

		menu.addChild(getText("global.Resources"), "ContractorForms.action");

		subMenu = addSupportLink(menu);
		addChildAction(subMenu, "ProfileEdit");
	}

	public void buildAssessmentCenter() {
		MenuComponent subMenu;
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
	}

	public void buildOperatorCorporateOrPics(Permissions permissions) {
		MenuComponent subMenu;

		if (permissions.hasPermission(OpPerms.ImportPQF)) {
			subMenu = menu.addChild(getTitle("ReportImportPQFs"));
			subMenu.addChild(getTitle("ReportImportPQFs"), "ReportImportPQFs.action");
		}

		subMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.hasPermission(OpPerms.AllContractors) || permissions.isOperatorCorporate()) {
			subMenu.addChild(getTitle("ContractorList"), "ContractorList.action");
		}

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			subMenu.addChild(getTitle("NewContractorSearch"), url);
		}
		if (permissions.hasPermission(OpPerms.RequestNewContractor))
			addChildAction(subMenu, "ReportNewRequestedContractor");

		if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
			String statusFilter = "";
			if (!permissions.getAccountStatus().isDemo())
				statusFilter = "?filter.status=Active";
			subMenu.addChild(getTitle("BiddingContractorSearch"), "BiddingContractorSearch.action" + statusFilter);
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
			subMenu.addChild("My Audits", "AuditListAuditor.action?filter.auditStatus=Pending");
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
		if (permissions.hasPermission(OpPerms.ContractorDetails) && !permissions.isOperatorCorporate())
			addChildAction(subMenu, "ReportCAOByStatusList");

		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign",
					"AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17");
		if (permissions.isAdmin())
			subMenu.addChild("Cancelled Sched. Audits", "CancelledScheduledAudits.action");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Close Assigned Audits", "ReportCloseAuditAssignments.action?filter.auditStatus=Submitted");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			addChildAction(subMenu, "AuditCalendar");
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
			subMenu.addChild(
					"PQF Verification",
					"PqfVerification.action?filter.status=Active"
							+ (permissions.hasGroup(User.GROUP_CSR) ? "&filter.conAuditorId="
									+ permissions.getShadowedUserID() : ""));
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			subMenu.addChild("CSR Assignment", "CSRAssignmentMatrix.action");

		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			subMenu.addChild("Billing Report", "ReportBilling.action?filter.status=Active&filter.status=Pending");
			subMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action");
			subMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action");
			subMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active");
			subMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action");
			subMenu.addChild("QuickBooks Sync US", "QBSyncList.action?currency=USD");
			subMenu.addChild("QuickBooks Sync Canada", "QBSyncList.action?currency=CAD");
			subMenu.addChild("QuickBooks Sync UK", "QBSyncList.action?currency=GBP");
			subMenu.addChild("QuickBooks Sync EUR", "QBSyncList.action?currency=EUR");
		}

		subMenu = menu.addChild(getText("global.InsureGUARD"));
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
			addChildAction(subMenu, "ReportPolicyList");
		}
		if (permissions.hasPermission(OpPerms.InsuranceVerification))
			subMenu.addChild(
					getTitle("PolicyVerification"),
					"PolicyVerification.action"
							+ (permissions.hasGroup(User.GROUP_CSR) ? "?filter.conAuditorId="
									+ permissions.getShadowedUserID() : ""));
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild(getTitle("ReportInsuranceApproval"),
					"ReportInsuranceApproval.action?filter.auditStatus=Complete");

		// Management
		subMenu = menu.addChild(getText("menu.Management"));
		if (permissions.hasPermission(OpPerms.ManageCorporate) || permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment))
			subMenu.addChild("Manage Accounts",
					"ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending");

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
		if (permissions.hasPermission(OpPerms.ManageEmployees))
			subMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action?id=" + permissions.getAccountId());

		if (permissions.seesAllContractors()) {
			subMenu.addChild("Email Subscriptions", "ReportEmailSubscription.action");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			addChildAction(subMenu, "EmailWizard");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates) && permissions.isPicsEmployee()) {
			addChildAction(subMenu, "ReportEmailWebinar");
		}
		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			subMenu.addChild(getTitle("EmailQueueList"), "EmailQueueList.action?filter.status=Pending");
			if (permissions.isPicsEmployee())
				subMenu.addChild("Email Error Report", "ReportEmailError.action");
		}
		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety))
			subMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action");

		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild(getTitle("Resources"), "Resources.action");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			subMenu.addChild("Sales Report", "ReportSalesReps.action");
		}
		if (permissions.hasPermission(OpPerms.EditProfile)) {
			addChildAction(subMenu, "ProfileEdit");
		}
		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			subMenu.addChild(getTitle("FacilitiesEdit"), "FacilitiesEdit.action?operator=" + permissions.getAccountId());

		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			subMenu.addChild("My Schedule", "MySchedule.action");
		}

		if (permissions.hasPermission(OpPerms.ClientSiteReferrals)) {
			subMenu.addChild("Client Site Referrals", "ReportClientSiteReferrals.action");
		}

		if (permissions.hasPermission(OpPerms.Debug)) {
			MenuComponent debug = subMenu.addChild("Debug", "#");
			debug.setHtmlId("debug-menu");
		}

		// Configuration
		subMenu = menu.addChild(getText("menu.Configuration"));
		HashMap<String, String> menuItems = new HashMap<String, String>();

		if (permissions.hasPermission(OpPerms.Translator)) {
			menuItems.put("Manage Translations", "ManageTranslations.action");
			menuItems.put("Import/Export Translations", "TranslationETL.action");
		}
		if (permissions.hasPermission(OpPerms.ManageTrades)) {
			menuItems.put(getTitle("TradeTaxonomy"), "TradeTaxonomy.action");
		}
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			menuItems.put("Audit Definition", "ManageAuditType.action");
			menuItems.put("Manage Audit Options", "ManageOptionGroup.action");
			menuItems.put("Flag Criteria", "ManageFlagCriteria.action");
		}
		if (permissions.hasPermission(OpPerms.ContractorSimulator))
			menuItems.put("Contractor Simulator", "ContractorSimulator.action");
		if (permissions.hasPermission(OpPerms.ManageAuditTypeRules, OpType.Edit)) {
			menuItems.put("Audit Type Rules", "AuditTypeRuleSearch.action");
		}
		if (permissions.hasPermission(OpPerms.ManageCategoryRules, OpType.Edit)) {
			menuItems.put("Category Rules", "CategoryRuleSearch.action");
		}
		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)) {
			menuItems.put(getTitle("AuditCategoryMatrix"), "AuditCategoryMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.ManageAuditWorkFlow))
			menuItems.put("Manage Workflow", "ManageAuditWorkFlow.action");
		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			menuItems.put(getTitle("ManageFlagCriteriaOperator"), "ManageFlagCriteriaOperator.action");
			if (permissions.isCanSeeInsurance())
				menuItems.put(getTitle("ManageInsuranceCriteriaOperator"), "ManageInsuranceCriteriaOperator.action");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates, OpType.Edit)) {
			menuItems.put(getTitle("EditEmailTemplate"), "EditEmailTemplate.action");
			if (permissions.isPicsEmployee())
				menuItems.put("Email Exclusions List", "EditEmailExclusions.action");
		}

		// add to menu in sorted order
		ArrayList<String> keys = new ArrayList<String>(menuItems.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			subMenu.addChild(key, menuItems.get(key));
		}

		if (permissions.hasPermission(OpPerms.Translator)) {
			MenuComponent tracing = subMenu.addChild("View Traced Translations",
					"ManageTranslations.action?showDoneButton=true");
			tracing.setHtmlId("tracing_open");
			tracing.setTarget("_BLANK");
			tracing.addDataField("url", "ManageTranslationsAjax.action?button=tracingOnClearAjax");
		}

		if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			subMenu = menu.addChild("Dev");
			subMenu.addChild("System Logging", "LoggerConfig.action");
			subMenu.addChild("Page Logging", "PageLogger.action");
			subMenu.addChild("Clear Cache", "ClearCache.action");
			subMenu.addChild("Cache Statistics", "CacheStatistics.action");
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
			subMenu.addChild("Batch Insert Translations", "BatchTranslations.action");
		}

		subMenu = menu.addChild(getText("menu.Reports"));

		// TODO - remove these hacks
		if (permissions.getAccountId() == 6228) {
			subMenu.addChild("Site Orientation Report", "report_orientation.jsp");
		}

		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		if (permissions.hasGroup(User.GROUP_CSR) || permissions.hasGroup(User.GROUP_MANAGER)) {
			subMenu.addChild(getTitle("ReportCsrActivity"), "ReportCsrActivity.action");
			subMenu.addChild("CSR Contractor Count", "ReportCsrContractorCount.action");
			subMenu.addChild("CSR Policies Status Count", "ReportCsrPoliciesStatusCount.action");
		}

		if (permissions.hasPermission(OpPerms.ManageCategoryRules)
				|| permissions.hasPermission(OpPerms.ManageAuditTypeRules))
			subMenu.addChild("Audit Rule History", "ReportRuleHistory.action");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		if (permissions.hasPermission(OpPerms.RiskRank))
			subMenu.addChild("Contractor Risk Level", "ReportContractorRiskLevel.action");
		if (permissions.isAdmin()
				|| (permissions.isOperatorCorporate() && permissions.getCorporateParent().contains(10566)))
			subMenu.addChild(getTitle("ReportContractorScore.title"), "ReportContractorScore.action");
		if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
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
			final String url = "ReportIncidenceRate.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild(getTitle("ReportIncidenceRate"), url);
		}

		if (permissions.getAccountName().startsWith("Tesoro"))
			subMenu.addChild("Background Check", "QuestionAnswerSearchByAudit.action");

		// Hide this menu if the operator doesn't have any required tags.
		OperatorAccountDAO operatorAccountDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate()
				&& !Strings.isEmpty(operatorAccountDAO.find(permissions.getAccountId()).getRequiredTags()))
			addChildAction(subMenu, "ReportUntaggedContractors");
		if (permissions.hasPermission(OpPerms.ManageTrades) && permissions.isAdmin())
			subMenu.addChild("Contractor Trade Conflicts", "ReportContractorTradeConflict.action");

		if (permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login", "MultiLoginUser.action");
		if (permissions.hasPermission(OpPerms.EditUsers))
			addChildAction(subMenu, "UserList");
		if (permissions.getAccountId() == 1813 || permissions.hasPermission(OpPerms.DevelopmentEnvironment)
				|| permissions.isAuditor()) // Hardcode to BP Cherry point
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
			subMenu = menu.addChild(getText("global.HSECompetencies"));

			if (permissions.hasPermission(OpPerms.DefineCompetencies))
				subMenu.addChild(getTitle("DefineCompetencies"), "DefineCompetencies.action");

			subMenu.addChild(getTitle("ReportCompetencyByAccount"), "ReportCompetencyByAccount.action");
			subMenu.addChild(getTitle("ReportCompetencyByEmployee"), "ReportCompetencyByEmployee.action");
			// subMenu.addChild("Employee Turnover",
			// "ReportEmployeeTurnover.action");
		}

		if (permissions.isRequiresOQ()) {
			subMenu = menu.addChild(getText("global.OperatorQualification"));

			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				subMenu.addChild(getTitle("ManageJobTasksOperator"), "ManageJobTasksOperator.action");
			if (permissions.hasPermission(OpPerms.ManageProjects))
				subMenu.addChild(getTitle("ManageProjects"), "ManageProjects.action");

			subMenu.addChild(getTitle("ReportOQ"), "ReportOQ.action");
			subMenu.addChild(getTitle("ReportOQEmployees"), "ReportOQEmployees.action");
		}

		if ((permissions.isOperatorCorporate() && permissions.isRequiresOQ()) || permissions.isAdmin())
			subMenu.addChild(getTitle("ReportAssessmentTests"), "ReportAssessmentTests.action");

		if (permissions.seesAllContractors())
			subMenu.addChild("Report WCB Accounts", "ReportWcbAccounts.action");

		if (permissions.isOperatorCorporate()) {
			addSupportLink(menu);
		}
	}

	public void buildNew(Permissions permissions, List<Report> reports, ContractorAccount contractor) {
		addDashboard();
		addMyReports(reports);
		addContractor(contractor);
		addHelp();
	}

	private void addDashboard() {
		menu.addChild("Dashboard", "Home.action");
	}

	private void addMyReports(List<Report> reports) {
		if (reports == null)
			return;

		MenuComponent subMenu = menu.addChild("My Reports");
		for (Report report : reports) {
			subMenu.addChild(report.getName(), "ReportDynamic.action?report=" + report.getId());
		}

		subMenu.addChild("Manage Reports", "ManageReports.action");
	}

	private void addContractor(ContractorAccount contractor) {
		if (contractor == null)
			return;

		MenuComponent subMenu = menu.addChild(contractor.getName(), "ContractorSummary.action?id=" + contractor.getId());
		subMenu.addChild("Users", "ManageReports.action?id=");
	}

	private void addHelp() {
		MenuComponent subMenu = menu.addChild("Help");
		subMenu.addChild("Contact PICS", "contact.jsp");
		subMenu.addChild("Help Center", "help.jsp");
		MenuComponent tools = subMenu.addChild("Tools", "help.jsp");
		tools.addChild("Cron", "cron.jsp");

		subMenu.addChild("About PICS Organizer", "about.jsp");
	}

	public void handleSingleChildMenu() {
		/*
		Iterator<MenuComponent> iterator = menu.getChildren().iterator();
		while (iterator.hasNext()) {
			MenuComponent nextMenu = iterator.next();
			if (nextMenu.getChildren().size() == 0)
				iterator.remove();
		}
		*/
		if (menu.getChildren().size() == 1)
			menu = menu.getChildren().get(0);
	}

	private MenuComponent addSupportLink(MenuComponent menu) {
		MenuComponent subMenu = menu.addChild(getText("menu.Support"), "Contact.action");
		// We may want to add the Contact page as a child menu after we have
		// more "Support" options
		// subMenu.addChild(getTitle("Contact"), "Contact.action");
		return subMenu;
	}

	private void addChildAction(MenuComponent menu, String actionName) {
		menu.addChild(getTitle(actionName), actionName + ".action");
	}

	private String getText(String key) {
		return i18nCache.getText(key, TranslationActionSupport.getLocaleStatic());
	}

	private String getTitle(String key) {
		return i18nCache.getText(key + ".title", TranslationActionSupport.getLocaleStatic());
	}

	public MenuComponent getMenu() {
		return menu;
	}
}
