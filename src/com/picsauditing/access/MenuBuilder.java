package com.picsauditing.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
// TODO Verify that all active menus are being translated
public class MenuBuilder {

	private static I18nCache i18nCache = I18nCache.getInstance();

	// We don't want people to instanciate this class
	private MenuBuilder() {
	}

	public static MenuComponent buildMenubar(Permissions permissions) {
		return buildMenubar(permissions, null);
	}

	public static MenuComponent buildMenubar(Permissions permissions, List<Report> reports) {
		MenuComponent menu = new MenuComponent();

		if (!permissions.isLoggedIn()) {
			buildNotLoggedInMenubar(menu);
		} else if (permissions.isContractor()) {
			buildContractorMenubar(menu, permissions);
		} else if (permissions.isAssessment()) {
			buildAssessmentMenubar(menu);
		} else if (permissions.isOperatorCorporate()) {
			buildOperatorCorporateMenu(menu, permissions);
		} else {
			buildGeneralMenubar(menu, permissions, reports);
		}

		handleSingleChildMenu(menu);

		return menu;
	}

	// For Operators, Corporate users, and PICS employees
	private static void buildGeneralMenubar(MenuComponent menu, Permissions permissions, List<Report> reports) {
		addPicsLogo(menu);

		addDashboardSubmenu(menu);

		addReportsSubmenu(menu, reports);

		//addContractorSubmenu(menu, permissions);

		addManagementSubmenu(menu, permissions);

		addConfigurationSubmenu(menu, permissions);

		addDevelopmentSubmenu(menu, permissions);

		addHelpSubmenu(menu);

		addFill(menu);

		addSearchBox(menu);

		addUserMenu(menu, permissions);
	}

	private static void buildNotLoggedInMenubar(MenuComponent menu) {
		if (menu == null)
			return;

		menu.addChild(getText("global.Home"), "index.jsp");
	}

	private static void buildContractorMenubar(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		// Don't show a menu for Contractors, they will use their sub menu for now
		if (!permissions.getAccountStatus().isActiveDemo()) {
			menu.addChild(getText("Registration.CompanyDetails.heading"), "ContractorEdit.action");
			addHelpSubmenu(menu);
			return;
		}

		menu.addChild(getText("global.Home"), "ContractorView.action");

		// Don't show for insurance only users
		if (!permissions.isInsuranceOnlyContractorUser()) {
			MenuComponent companyMenu = menu.addChild(getText("global.Company"));
			companyMenu.addChild(getText("menu.Contractor.WhereWeWork"), "ContractorFacilities.action");
			companyMenu.addChild(getText("menu.Contractor.ActivityLog"), "ContractorNotes.action");
		}

		if (permissions.has(OpPerms.ContractorAdmin)) {
			MenuComponent editMenu = menu.addChild(getText("button.Edit"));
			editMenu.addChild(getText("menu.Contractor.CompanyAccount"), "ContractorEdit.action");
			editMenu.addChild(getText("global.Users"), "UsersManage.action");
			editMenu.addChild(getTitle("ContractorTrades"), "ContractorTrades.action");

			if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
				editMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action");

			if (permissions.isRequiresCompetencyReview()) {
				MenuComponent hseMenu = menu.addChild(getText("global.HSECompetencies"));
				hseMenu.addChild(getTitle("ManageJobRoles"), "ManageJobRoles.action");
				hseMenu.addChild(getTitle("EmployeeCompetencies"), "EmployeeCompetencies.action");
			}

			if (permissions.isRequiresOQ()) {
				//MenuComponent operatorQualMenu = menu.addChild(getText("global.OperatorQualification"));
				//String url = "ReportOQEmployees.action?orderBy=e.lastName,e.firstName";
				//operatorQualMenu.addChild(getTitle("ReportOQEmployees"), url);
				//operatorQualMenu.addChild(getTitle("ReportOQChanges"), "ReportOQChanges.action");
				//operatorQualMenu.addChild(getTitle("ReportNewProjects"), "ReportNewProjects.action");
			}
		}

		if (permissions.has(OpPerms.ContractorBilling)) {
			MenuComponent billingMenu = menu.addChild(getText("menu.Billing"));
			billingMenu.addChild(getText("menu.Contractor.BillingDetails"), "BillingDetail.action");
			billingMenu.addChild(getText("menu.Contractor.PaymentOptions"), "ContractorPaymentOptions.action");
		}

		menu.addChild(getText("global.Resources"), "ContractorForms.action");

		MenuComponent helpMenu = addHelpSubmenu(menu);

		if (permissions.hasPermission(OpPerms.EditProfile))
			addChildAction(helpMenu, "ProfileEdit");
	}

	private static void buildAssessmentMenubar(MenuComponent menu) {
		if (menu == null)
			return;

		MenuComponent managementMenu = menu.addChild("Management");
		managementMenu.addChild("Imported Data", "ManageImportData.action");
		managementMenu.addChild("Assessment Tests", "ManageAssessmentTests.action");
		managementMenu.addChild("Test Mapping", "ManageUnmappedTests.action");
		managementMenu.addChild("Assessment Results", "ManageAssessmentResults.action");
		managementMenu.addChild("Companies", "ManageMappedCompanies.action");
		managementMenu.addChild("Company Mapping", "ManageUnmappedCompanies.action");

		MenuComponent editMenu = menu.addChild("Edit");
		editMenu.addChild("Account", "AssessmentCenterEdit.action");
		editMenu.addChild("Users", "UsersManage.action");

		addHelpSubmenu(editMenu);
	}

	private static void buildOperatorCorporateMenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		// TODO Flesh this out
	}

	private static void addPicsLogo(MenuComponent menu) {
		if (menu == null)
			return;

		// TODO Flesh this out
	}

	private static MenuComponent addConfigurationSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return menu;

		MenuComponent configMenu = menu.addChild(getText("menu.Configuration"));
		HashMap<String, String> menuItems = new HashMap<String, String>();

		if (permissions.has(OpPerms.Translator)) {
			menuItems.put("Manage Translations", "ManageTranslations.action");

			if (permissions.has(OpPerms.DevelopmentEnvironment)) {
				menuItems.put("Import/Export Translations", "TranslationETL.action");
				menuItems.put("Unsynced Translations", "UnsyncedTranslations.action");
			}
		}

		if (permissions.has(OpPerms.ManageTrades))
			menuItems.put(getTitle("TradeTaxonomy"), "TradeTaxonomy.action");

		if (permissions.has(OpPerms.ManageAudits)) {
			menuItems.put("Audit Definition", "ManageAuditType.action");
			menuItems.put("Manage Audit Options", "ManageOptionGroup.action");
			menuItems.put("Flag Criteria", "ManageFlagCriteria.action");
		}

		if (permissions.has(OpPerms.ContractorSimulator))
			menuItems.put("Contractor Simulator", "ContractorSimulator.action");

		if (permissions.has(OpPerms.ManageAuditTypeRules, OpType.Edit))
			menuItems.put("Audit Type Rules", "AuditTypeRuleSearch.action");

		if (permissions.has(OpPerms.ManageCategoryRules, OpType.Edit))
			menuItems.put("Category Rules", "CategoryRuleSearch.action");

		if (permissions.has(OpPerms.ManageAudits, OpType.Edit))
			menuItems.put(getTitle("AuditCategoryMatrix"), "AuditCategoryMatrix.action");

		if (permissions.has(OpPerms.ManageAuditWorkFlow))
			menuItems.put("Manage Workflow", "ManageAuditWorkFlow.action");

		if (permissions.has(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			menuItems.put(getTitle("ManageFlagCriteriaOperator"), "ManageFlagCriteriaOperator.action");
			if (permissions.isCanSeeInsurance())
				menuItems.put(getTitle("ManageInsuranceCriteriaOperator"), "ManageInsuranceCriteriaOperator.action");
		}

		if (permissions.has(OpPerms.EmailTemplates, OpType.Edit)) {
			menuItems.put(getTitle("EditEmailTemplate"), "EditEmailTemplate.action");
			if (permissions.isPicsEmployee())
				menuItems.put("Email Exclusions List", "EditEmailExclusions.action");
		}

		// add to menu in sorted order
		ArrayList<String> keys = new ArrayList<String>(menuItems.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			configMenu.addChild(key, menuItems.get(key));
		}

		if (permissions.has(OpPerms.Translator)) {
			String url = "ManageTranslations.action?showDoneButton=true";
			MenuComponent tracingMenu = configMenu.addChild("View Traced Translations", url);
			tracingMenu.setHtmlId("tracing_open");
			tracingMenu.setTarget("_BLANK");
			tracingMenu.addDataField("url", "ManageTranslationsAjax.action?button=tracingOnClearAjax");
		}

		return configMenu;
	}

	private static MenuComponent addContractorSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return menu;

		MenuComponent contractorMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.has(OpPerms.ContractorWatch))
			addChildAction(contractorMenu, "ReportActivityWatch");

		if (permissions.has(OpPerms.WatchListManager))
			addChildAction(contractorMenu, "WatchListManager");

		return contractorMenu;
	}

	private static void addDashboardSubmenu(MenuComponent menu) {
		if (menu == null)
			return;

		menu.addChild("Dashboard", "Home.action");
	}

	private static void addDevelopmentSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		if (permissions.isDeveloperEnvironment()) {
			MenuComponent devMenu = menu.addChild("Development");
			devMenu.addChild("System Logging", "LoggerConfig.action");
			devMenu.addChild("Page Logging", "PageLogger.action");
			devMenu.addChild("Clear Cache", "ClearCache.action");
			devMenu.addChild("Cache Statistics", "CacheStatistics.action");
			devMenu.addChild("Contractor Cron", "ContractorCron.action");
			devMenu.addChild("Con/Op Flag Differences", "ContractorFlagDifference.action");
			devMenu.addChild("Mail Cron", "MailCron.action");
			devMenu.addChild("Subscription Cron", "SubscriptionCron.action");
			devMenu.addChild("Server Information", "ServerInfo.action");
			devMenu.addChild("Audit Schedule Builder", "AuditScheduleBuilderCron.action");
			devMenu.addChild("Manage App Properties", "ManageAppProperty.action");
			//devMenu.addChild("Exception Log", "ReportExceptions.action");
			devMenu.addChild("Batch Insert Translations", "BatchTranslations.action");
			devMenu.addChild("Dynamic Reporting", "ReportDynamic.action?report=11");
			devMenu.addChild("CSS Style Guide", "css.jsp");
			devMenu.addChild("Debug");
		}
	}

	private static MenuComponent addHelpSubmenu(MenuComponent menu) {
		if (menu == null)
			return menu;

		MenuComponent helpMenu = menu.addChild("Help");
		helpMenu.addChild(getText("menu.Support"), "Contact.action");
		helpMenu.addChild("Contact PICS", "contact.jsp");
		helpMenu.addChild("Help Center", "help.jsp");

		MenuComponent tools = helpMenu.addChild("Tools", "help.jsp");
		tools.addChild("Cron", "cron.jsp");

		helpMenu.addChild("About PICS Organizer", "about.jsp");

		return helpMenu;
	}

	private static void addManagementSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent managementMenu = menu.addChild("Management");

		if (permissions.has(OpPerms.OfficeAuditCalendar))
			managementMenu.addChild("Audit Calendar", "AuditCalendar.action");

		if (permissions.has(OpPerms.ManageWebcam)) {
			managementMenu.addChild("Manage Webcams", "ManageWebcams.action?button=out");
			managementMenu.addChild("Assign Webcams", "AssignWebcams.action");
		}

		if (permissions.seesAllContractors())
			managementMenu.addChild("Email Subscripions", "ReportEmailSubscription.action");

		if (permissions.has(OpPerms.FormsAndDocs))
			managementMenu.addChild(getTitle("Resources"), "Resources.action");

		if (permissions.has(OpPerms.EditUsers)) {
			managementMenu.addChild("UsersManage", "UsersManage.action");
			managementMenu.addChild("ReportUserPermissionMatrix", "ReportUserPermissionsMatrix.action");
		}

		if (permissions.has(OpPerms.UserZipcodeAssignment)) {
			managementMenu.addChild("CSR Assignment", "CSRAssignmentMatrix.action");
			managementMenu.addChild("Auditor Assignment", "AuditorAssignmentMatrix.action");
		}

		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR))
			managementMenu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action");

		if (permissions.has(OpPerms.EditAccountDetails))
			managementMenu.addChild(getTitle("FacilitiesEdit"), "FacilitiesEdit.action?operator=" + permissions.getAccountId());

		if (permissions.has(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			addChildAction(managementMenu, "OperatorTags");

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR))
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();

			if (permissions.hasGroup(User.GROUP_MARKETING))
				custom = "?filter.accountManager=" + permissions.getUserId();

			managementMenu.addChild("Flag Changes", "ReportFlagChanges.action" + custom);
		}
	}

	private static void addReportsSubmenu(MenuComponent menu, List<Report> reports) {
		if (menu == null || reports == null)
			return;

		MenuComponent reportsMenu = menu.addChild("Reports");

		reportsMenu.addChild("Manage Reports", "ManageReports.action");

		for (Report report : reports) {
			reportsMenu.addChild(report.getName(), "ReportDynamic.action?report=" + report.getId());
		}
	}

	private static void addUserMenu(MenuComponent menu, Permissions permissions) {
		MenuComponent userMenu = menu.addChild(permissions.getName());
		userMenu.addChild("Edit Profile", "ProfileEdit.action");
		userMenu.addChild("Schedule", "MySchedule.action");
		userMenu.addChild("Logout", "Login.action?button=logout");
	}

	private static void addFill(MenuComponent menu) {
		if (menu == null)
			return;

		MenuComponent fill = menu.addChild("fill");
		fill.setXtype("tbfill");
	}

	private static void addSearchBox(MenuComponent menu) {
		if (menu == null)
			return;

		MenuComponent searchBox = menu.addChild("searchTerm");
		searchBox.setXtype("textfield");
	}

	private static void addChildAction(MenuComponent menu, String actionName) {
		menu.addChild(getTitle(actionName), actionName + ".action");
	}

	private static void handleSingleChildMenu(MenuComponent menu) {
		if (menu == null)
			return;

		if (menu.getChildren().size() == 1)
			menu = menu.getChildren().get(0);
	}

	public static String getHomePage(MenuComponent menu, Permissions permissions) {
		if (permissions.isContractor())
			return "ContractorView.action";

		if (permissions.has(OpPerms.Dashboard))
			return "Home.action";

		if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty())
			return null;

		String url = null;
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

	private static String getText(String key) {
		return i18nCache.getText(key, TranslationActionSupport.getLocaleStatic());
	}

	private static String getTitle(String key) {
		return getText(key + ".title");
	}

	private static void oldAuditGuardSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent auditGuardMenu = menu.addChild(getText("global.AuditGUARD"));
		if (permissions.isAuditor()) {
			//auditGuardMenu.addChild("My Audits", "AuditListAuditor.action?filter.auditStatus=Pending");
			//auditGuardMenu.addChild("My Audit History", "MyAuditHistory.action");
		}

		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			//auditGuardMenu.addChild("Safety Pro Invoices", "AuditorInvoices.action");
			auditGuardMenu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action");
		}

		if (permissions.isAdmin()) {
			//auditGuardMenu.addChild("Audit List Compress", "ReportAuditList.action");
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			//addChildAction(auditGuardMenu, "ReportCAOList");
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails) && !permissions.isOperatorCorporate()) {
			//addChildAction(auditGuardMenu, "ReportCAOByStatusList");
		}
		if (permissions.hasPermission(OpPerms.AssignAudits)) {
			String url = "AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17";
			//auditGuardMenu.addChild("Sched. &amp; Assign", url);
		}
		if (permissions.isAdmin()) {
			//auditGuardMenu.addChild("Cancelled Sched. Audits", "CancelledScheduledAudits.action");
		}
		if (permissions.hasPermission(OpPerms.AssignAudits)) {
			//auditGuardMenu.addChild("Close Assigned Audits", "ReportCloseAuditAssignments.action?filter.auditStatus=Submitted");
		}
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			addChildAction(auditGuardMenu, "AuditCalendar");
		if (permissions.hasPermission(OpPerms.AuditVerification)){
			//auditGuardMenu.addChild("Answer Updates", "AuditDataUpdates.action");
		}
		if (permissions.isAuditor()) {
			//auditGuardMenu.addChild("Close Open Reqs", "ReportOpenRequirements.action");
		}
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			auditGuardMenu.addChild("Auditor Assignment", "AuditorAssignmentMatrix.action");
	}

	private static void oldAccountingSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent accountingMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			//accountingMenu.addChild("Billing Report", "ReportBilling.action?filter.status=Active&filter.status=Pending");
			//accountingMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action");
			//accountingMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action");
			//accountingMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active");
			//accountingMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action");
			//accountingMenu.addChild("QuickBooks Sync US", "QBSyncList.action?currency=USD");
			accountingMenu.addChild("QuickBooks Sync Canada", "QBSyncList.action?currency=CAD");
			accountingMenu.addChild("QuickBooks Sync UK", "QBSyncList.action?currency=GBP");
			accountingMenu.addChild("QuickBooks Sync EUR", "QBSyncList.action?currency=EUR");
		}
	}

	private static void oldManagementSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent managementMenu = menu.addChild(getText("menu.Management"));
		if (permissions.hasPermission(OpPerms.ManageCorporate)
				|| permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment)) {
			String url = "ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending";
			//managementMenu.addChild("Manage Accounts", url);
		}

		if (permissions.hasPermission(OpPerms.ContractorApproval)) {
			//managementMenu.addChild(getTitle("ContractorApproval"), "ContractorApproval.action?filter.workStatus=P");
			if (permissions.isGcOperator()) {
				//managementMenu.addChild(getTitle("ReportSubcontractors"), "ReportSubcontractors.action", "ReportSubcontractors");
			}
		}

		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			addChildAction(managementMenu, "OperatorTags");

		if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
			addChildAction(managementMenu, "UsersManage");
		}

		if (permissions.hasPermission(OpPerms.EditUsers)) {
			addChildAction(managementMenu, "UsersManage");
			addChildAction(managementMenu, "ReportUserPermissionMatrix");
		}

		if (permissions.hasPermission(OpPerms.ManageEmployees))
			managementMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action?id=" + permissions.getAccountId());

		if (permissions.seesAllContractors()) {
			//managementMenu.addChild("Email Subscriptions", "ReportEmailSubscription.action");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			//addChildAction(managementMenu, "EmailWizard");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates) && permissions.isPicsEmployee()) {
			//addChildAction(managementMenu, "ReportEmailWebinar");
		}

		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			//managementMenu.addChild(getTitle("EmailQueueList"), "EmailQueueList.action?filter.status=Pending");
			if (permissions.isPicsEmployee()) {
				//managementMenu.addChild("Email Error Report", "ReportEmailError.action");
			}
		}

		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety)) {
			//managementMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			managementMenu.addChild(getTitle("Resources"), "Resources.action");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			//managementMenu.addChild("Sales Report", "ReportSalesReps.action");
		}

		if (permissions.hasPermission(OpPerms.EditProfile)) {
			addChildAction(managementMenu, "ProfileEdit");
		}

		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			managementMenu.addChild(getTitle("FacilitiesEdit"), "FacilitiesEdit.action?operator=" + permissions.getAccountId());

		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			managementMenu.addChild("My Schedule", "MySchedule.action");
		}

		if (permissions.hasPermission(OpPerms.ClientSiteReferrals)) {
			//managementMenu.addChild("Client Site Referrals", "ReportClientSiteReferrals.action");
		}

		if (permissions.hasPermission(OpPerms.Debug)) {
			MenuComponent debug = managementMenu.addChild("Debug", "#");
			debug.setHtmlId("debug-menu");
		}
	}

	private static void oldContractorSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent contractorMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.hasPermission(OpPerms.AllContractors) || permissions.isOperatorCorporate()) {
			//contractorMenu.addChild(getTitle("ContractorList"), "ContractorList.action");
		}

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			//contractorMenu.addChild(getTitle("NewContractorSearch"), url);
		}
		if (permissions.hasPermission(OpPerms.RequestNewContractor)) {
			//addChildAction(contractorMenu, "ReportNewRequestedContractor");
		}
		if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
			String url = "BiddingContractorSearch.action";
			if (!permissions.getAccountStatus().isDemo())
				url += "?filter.status=Active";
			//contractorMenu.addChild(getTitle("BiddingContractorSearch"), url);
		}
		if (permissions.isCorporate()) {
			//addChildAction(contractorMenu, "ReportContractorOperatorFlag");
		}
		if (permissions.isCorporate() || permissions.getCorporateParent().size() > 0) {
			//addChildAction(contractorMenu, "ReportContractorOperatorFlagMatrix");
		}
		if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.OperatorFlagMatrix)) {
			//addChildAction(contractorMenu, "OperatorFlagMatrix");
		}
		if (permissions.hasPermission(OpPerms.DelinquentAccounts)) {
			//addChildAction(contractorMenu, "ArchivedContractorAccounts");
			//addChildAction(contractorMenu, "DelinquentContractorAccounts");
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			//addChildAction(contractorMenu, "QuestionAnswerSearch");
		}
		if (permissions.hasPermission(OpPerms.ContractorWatch))
			addChildAction(contractorMenu, "ReportActivityWatch");
		if (permissions.hasPermission(OpPerms.WatchListManager))
			addChildAction(contractorMenu, "WatchListManager");
	}

	private static void oldCustomerServiceSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent customerServiceMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			//customerServiceMenu.addChild("Assign Contractors", "ContractorAssigned.action");
		}

		if (permissions.hasPermission(OpPerms.ManageWebcam)) {
			customerServiceMenu.addChild("Manage Webcams", "ManageWebcams.action?button=out");
			customerServiceMenu.addChild("Assign Webcams", "AssignWebcams.action");
		}

		if (permissions.hasPermission(OpPerms.AuditVerification)) {
			//customerServiceMenu.addChild("Pending PQF", "ReportCompletePQF.action?filter.auditStatus=Pending");
		}

		if (permissions.hasPermission(OpPerms.AuditVerification)) {
			String url = "PqfVerification.action?filter.status=Active";
			if (permissions.hasGroup(User.GROUP_CSR))
				url += "&filter.conAuditorId=" + permissions.getShadowedUserID();

			//customerServiceMenu.addChild("PQF Verification", url);
		}

		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			customerServiceMenu.addChild("CSR Assignment", "CSRAssignmentMatrix.action");
	}

	private static void oldHseCompetencySubmenu(MenuComponent menu, Permissions permissions) {
		if (permissions.isRequiresCompetencyReview()) {
			MenuComponent hseCompetencyMenu = menu.addChild(getText("global.HSECompetencies"));

			if (permissions.hasPermission(OpPerms.DefineCompetencies))
				hseCompetencyMenu.addChild(getTitle("DefineCompetencies"), "DefineCompetencies.action");

			//hseCompetencyMenu.addChild(getTitle("ReportCompetencyByAccount"), "ReportCompetencyByAccount.action");
			//hseCompetencyMenu.addChild(getTitle("ReportCompetencyByEmployee"), "ReportCompetencyByEmployee.action");
		}
	}

	private static void oldInsureGuardSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent insureGuardMenu = menu.addChild(getText("global.InsureGUARD"));
		if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
			addChildAction(insureGuardMenu, "ReportPolicyList");
		}

		if (permissions.hasPermission(OpPerms.InsuranceVerification)) {
			String url = "PolicyVerification.action";
			if (permissions.hasGroup(User.GROUP_CSR))
				url += "?filter.conAuditorId=" + permissions.getShadowedUserID();
			//insureGuardMenu.addChild(getTitle("PolicyVerification"), url);
		}

		if (permissions.hasPermission(OpPerms.InsuranceApproval)) {
			String url = "ReportInsuranceApproval.action?filter.auditStatus=Complete";
			//insureGuardMenu.addChild(getTitle("ReportInsuranceApproval"), url);
		}
	}

	private static void oldOperatorQualificationSubmenu(MenuComponent menu, Permissions permissions) {
		if (permissions.isRequiresOQ()) {
			MenuComponent operatorQualMenu = menu.addChild(getText("global.OperatorQualification"));

			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				operatorQualMenu.addChild(getTitle("ManageJobTasksOperator"), "ManageJobTasksOperator.action");

			if (permissions.hasPermission(OpPerms.ManageProjects))
				operatorQualMenu.addChild(getTitle("ManageProjects"), "ManageProjects.action");

			//operatorQualMenu.addChild(getTitle("ReportOQ"), "ReportOQ.action");
			//operatorQualMenu.addChild(getTitle("ReportOQEmployees"), "ReportOQEmployees.action");
		}
	}

	private static void oldReportsSubmenu(MenuComponent menu, Permissions permissions) {
		MenuComponent reportsMenu = menu.addChild(getText("menu.Reports"));

		// remove this hack
		if (permissions.getAccountId() == 6228) {
			//reportsMenu.addChild("Site Orientation Report", "report_orientation.jsp");
		}

		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			//reportsMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action");
		}
		if (permissions.hasGroup(User.GROUP_CSR) || permissions.hasGroup(User.GROUP_MANAGER)) {
			//reportsMenu.addChild(getTitle("ReportCsrActivity"), "ReportCsrActivity.action");
			//reportsMenu.addChild("CSR Contractor Count", "ReportCsrContractorCount.action");
			//reportsMenu.addChild("CSR Policies Status Count", "ReportCsrPoliciesStatusCount.action");
		}

		if (permissions.hasPermission(OpPerms.ManageCategoryRules)
				|| permissions.hasPermission(OpPerms.ManageAuditTypeRules)) {
			//reportsMenu.addChild("Audit Rule History", "ReportRuleHistory.action");
		}

		if (permissions.hasPermission(OpPerms.ContractorLicenseReport)) {
			//reportsMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action");
		}

		if (permissions.hasPermission(OpPerms.RiskRank)) {
			//reportsMenu.addChild("Contractor Risk Level", "ReportContractorRiskLevel.action");
		}

		if (permissions.isAdmin()
				|| (permissions.isOperatorCorporate()
				&& permissions.getCorporateParent().contains(10566))) {
			//reportsMenu.addChild(getTitle("ReportContractorScore.title"), "ReportContractorScore.action");
		}

		if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
			//reportsMenu.addChild(getTitle("GraphEmrRates"), "GraphEmrRates.action?years=2010");
			//reportsMenu.addChild(getTitle("ReportEmrRates"), "ReportEmrRates.action?filter.auditFor=2010");
			if (permissions.isAuditor()) {
				//reportsMenu.addChild("Auditor Emr Rates Report", "ReportAuditorEmrRates.action", "ReportAuditorEmrRates");
			}
		}

		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportFatalities.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			//reportsMenu.addChild(getTitle("ReportFatalities"), url);
		}

		if (permissions.hasPermission(OpPerms.ForcedFlagsReport)) {
			//addChildAction(reportsMenu, "ReportContractorsWithForcedFlags");
		}

		if (permissions.hasPermission(OpPerms.TRIRReport)) {
			//addChildAction(reportsMenu, "GraphTrirRates");
			final String url = "ReportIncidenceRate.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			//reportsMenu.addChild(getTitle("ReportIncidenceRate"), url);
		}

		if (permissions.getAccountName().startsWith("Tesoro")) {
			//reportsMenu.addChild("Background Check", "QuestionAnswerSearchByAudit.action");
		}

		// Hide this menu if the operator doesn't have any required tags.
		OperatorAccountDAO operatorAccountDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		if (permissions.hasPermission(OpPerms.ContractorTags)
				&& permissions.isOperatorCorporate()
				&& !Strings.isEmpty(operatorAccountDAO.find(permissions.getAccountId()).getRequiredTags())) {
			//addChildAction(reportsMenu, "ReportUntaggedContractors");
		}

		if (permissions.hasPermission(OpPerms.ManageTrades) && permissions.isAdmin()) {
			//reportsMenu.addChild("Contractor Trade Conflicts", "ReportContractorTradeConflict.action");
		}

		if (permissions.seesAllContractors()) {
			//reportsMenu.addChild("User Multi-Login", "MultiLoginUser.action");
		}

		if (permissions.hasPermission(OpPerms.EditUsers))
			addChildAction(reportsMenu, "UserList");

		// Hardcode to BP Cherry point
		if (permissions.getAccountId() == 1813
				|| permissions.hasPermission(OpPerms.DevelopmentEnvironment)
				|| permissions.isAuditor()) {
			String url = "ReportWashingtonStateAudit.action?filter.riskLevel=3&filter.waAuditTypes=176";
			//reportsMenu.addChild("Washington Audit", url);
		}

		if (permissions.hasPermission(OpPerms.EmployeeList))
			addChildAction(reportsMenu, "EmployeeList");

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR))
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();

			if (permissions.hasGroup(User.GROUP_MARKETING))
				custom = "?filter.accountManager=" + permissions.getUserId();

			reportsMenu.addChild("Flag Changes", "ReportFlagChanges.action" + custom);
		}

		if ((permissions.isOperatorCorporate() && permissions.isRequiresOQ()) || permissions.isAdmin()) {
			//reportsMenu.addChild(getTitle("ReportAssessmentTests"), "ReportAssessmentTests.action");
		}
		if (permissions.seesAllContractors()) {
			//reportsMenu.addChild("Report WCB Accounts", "ReportWcbAccounts.action");
		}
	}

	private static void oldImportPqfSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		if (permissions.hasPermission(OpPerms.ImportPQF)) {
			//MenuComponent importPqfMenu = menu.addChild(getTitle("ReportImportPQFs"));
			//importPqfMenu.addChild(getTitle("ReportImportPQFs"), "ReportImportPQFs.action");
		}
	}
}
