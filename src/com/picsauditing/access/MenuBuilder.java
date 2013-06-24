package com.picsauditing.access;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class MenuBuilder {

	private static final String SEARCH_FOR_NEW_URL = "NewContractorSearch.action?filter.performedBy=Self%20Performed&filter.primaryInformation=true&filter.tradeInformation=true";
	private static final Logger logger = LoggerFactory.getLogger(MenuBuilder.class);
	private static TranslationService translationService = TranslationServiceFactory.getTranslationService();

	private MenuBuilder() {
	}

	public static MenuComponent buildMenubar(Permissions permissions) {
		return buildMenubar(permissions, Collections.<ReportUser>emptyList());
	}

	public static MenuComponent buildMenubar(Permissions permissions, List<ReportUser> favoriteReports) {
		MenuComponent menubar = new MenuComponent();

		if (permissions == null || !permissions.isLoggedIn()) {
			buildNotLoggedInMenubar(menubar);
		} else if (permissions.isContractor()) {
			buildContractorMenubar(menubar, permissions);
		} else if (permissions.isAssessment()) {
			buildAssessmentMenubar(menubar, permissions);
		} else if (permissions.isOperatorCorporate()) {
			buildOperatorCorporateMenubar(menubar, permissions, favoriteReports);
		} else {
			buildGeneralMenubar(menubar, permissions, favoriteReports);
		}

		handleSingleChildMenu(menubar);

		return menubar;
	}

	// For Operators, Corporate users, and PICS employees
	private static void buildGeneralMenubar(MenuComponent menubar, Permissions permissions,
	                                        List<ReportUser> favoriteReports) {
		addDashboard(menubar);
		addReportsMenu(menubar, favoriteReports, permissions);
		addConfigureMenu(menubar, permissions);
		addManageMenu(menubar, permissions);
		addDevelopmentMenu(menubar, permissions);
		addSupportMenu(menubar, permissions);
		addUserMenu(menubar, permissions);
	}

	private static void buildNotLoggedInMenubar(MenuComponent menubar) {
		menubar.addChild(getText("global.Home"), "index.jsp", "index");
	}

	private static void buildContractorMenubar(MenuComponent menubar, Permissions permissions) {
		// Don't show a menu for Contractors, they will use their sub menu for
		// now
		if (!permissions.getAccountStatus().isActiveOrDemo()) {
			menubar.addChild(getText("Registration.CompanyDetails.heading"), "ContractorEdit.action", "contractor_edit");
			addSupportMenu(menubar, permissions);
			return;
		}

		addDashboard(menubar);

		// Don't show for insurance only users
		if (!permissions.isInsuranceOnlyContractorUser()) {
			MenuComponent companyMenu = menubar.addChild(getText("global.Company"));
			companyMenu.addChild(getText("menu.Contractor.WhereWeWork"), "ContractorFacilities.action",
					"contractor_facilities");
			companyMenu.addChild(getText("menu.Contractor.ActivityLog"), "ContractorNotes.action", "contractor_notes");
		}

		if (permissions.has(OpPerms.ContractorAdmin)) {
			MenuComponent editMenu = menubar.addChild(getText("button.Edit"));
			editMenu.addChild(getText("menu.Contractor.CompanyAccount"), "ContractorEdit.action", "contractor_edit");
			editMenu.addChild(getText("global.Users"), "UsersManage.action", "users_manage");
			editMenu.addChild(getText("ContractorTrades.title"), "ContractorTrades.action", "contractor_trades");

			if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview()) {
				editMenu.addChild(getText("ManageEmployees.title"), "ManageEmployees.action", "manage_employees");
			}

			if (permissions.isRequiresCompetencyReview()) {
				MenuComponent hseMenu = menubar.addChild(getText("global.HSECompetencies"));
				hseMenu.addChild(getText("ManageJobRoles.title"), "ManageJobRoles.action", "manage_job_roles");
				hseMenu.addChild(getText("EmployeeCompetencies.title"), "EmployeeCompetencies.action",
						"employee_competencies");
			}
		}

		if (permissions.has(OpPerms.ContractorBilling)) {
			MenuComponent billingMenu = menubar.addChild(getText("menu.Billing"));
			billingMenu.addChild(getText("menu.Contractor.BillingDetails"), "BillingDetail.action", "billing_detail");
			billingMenu.addChild(getText("menu.Contractor.PaymentOptions"), "ContractorPaymentOptions.action",
					"contractor_payment_options");
		}

		menubar.addChild(getText("global.Resources"), "ContractorForms.action", "contractor_forms");

		addSupportMenu(menubar, permissions);
		addUserMenu(menubar, permissions);
	}

	private static void buildAssessmentMenubar(MenuComponent menubar, Permissions permissions) {
		MenuComponent assessmentMenu = menubar.addChild(getText("menu.Assessment"));
		assessmentMenu.addChild(getText("menu.Assessment.ImportedData"), "ManageImportData.action",
				"manage_import_data");
		assessmentMenu.addChild(getText("menu.Assessment.AssessmentTests"), "ManageAssessmentTests.action",
				"manage_assessment_tests");
		assessmentMenu.addChild(getText("menu.Assessment.TestMapping"), "ManageUnmappedTests.action",
				"manage_unmapped_tests");
		assessmentMenu.addChild(getText("menu.Assessment.AssessmentResults"), "ManageAssessmentResults.action",
				"manage_assessment_results");
		assessmentMenu.addChild(getText("menu.Assessment.Companies"), "ManageMappedCompanies.action",
				"manage_mapped_companies");
		assessmentMenu.addChild(getText("menu.Assessment.CompanyMapping"), "ManageUnmappedCompanies.action",
				"manage_unmapped_companies");

		addSupportMenu(menubar, permissions);
	}

	private static void buildOperatorCorporateMenubar(MenuComponent menubar, Permissions permissions,
	                                                  List<ReportUser> favoriteReports) {
		addDashboard(menubar);
		addReportsMenu(menubar, favoriteReports, permissions);
		addManageMenu(menubar, permissions);
		addSupportMenu(menubar, permissions);
		addUserMenu(menubar, permissions);
	}

	private static void addConfigureMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent configureMenu = menubar.addChild(getText("menu.Configure"));

		if (permissions.has(OpPerms.Translator)) {
			MenuComponent translationMenu = configureMenu.addChild(getText("global.Translations"));
			translationMenu.addChild(getText("menu.Configure.ManageTranslations"), "ManageTranslations.action",
					"manage_translations");

			if (permissions.has(OpPerms.DevelopmentEnvironment)) {
				translationMenu.addChild(getText("menu.Configure.ImportExportTranslations"), "TranslationETL.action",
						"im_ex_trans");

				try {
					String databaseName = Database.getDatabaseName();

					if (databaseName.contains("alpha")) {
						translationMenu.addChild(getText("menu.Configure.UnsyncedTranslations"),
								"UnsyncedTranslations.action", "unsynced_translations");
					}
				} catch (Exception e) {
					// Don't show menu item
				}
			}

			translationMenu.addChild(getText("menu.Configure.ViewTracedTranslations"),
					"ManageTranslations.action?showDoneButton=true", "traced_translations").setTarget("_BLANK");
		}

		addAuditsSubmenu(configureMenu, permissions);

		if (permissions.has(OpPerms.ManageAudits)) {
			configureMenu.addChild(getText("menu.Configure.FlagCriteria"), "ManageFlagCriteria.action",
					"manage_flag_criteria");
		}

		if (permissions.has(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			if (permissions.isCanSeeInsurance()) {
				MenuComponent flagCriteria = configureMenu.addChild(getText("menu.FlagCriteria"));
				flagCriteria.addChild(getText("ManageFlagCriteriaOperator.title"), "ManageFlagCriteriaOperator.action",
						"manage_flag_criteria_operator");
				flagCriteria.addChild(getText("ManageInsuranceCriteriaOperator.title"),
						"ManageInsuranceCriteriaOperator.action", "manage_insurance_criteria_operator");
			} else {
				configureMenu.addChild(getText("ManageFlagCriteriaOperator.title"),
						"ManageFlagCriteriaOperator.action", "manage_flag_criteria_operator");
			}
		}

		if (permissions.has(OpPerms.ContractorSimulator)) {
			configureMenu.addChild(getText("menu.Configure.ContractorSimulator"), "ContractorSimulator.action",
					"contractor_simulator");
		}
	}

	private static void addDashboard(MenuComponent menubar) {
		menubar.addChild(getText("OpPerms.Dashboard.description"), "Home.action", "logo");
	}

	private static void addDevelopmentMenu(MenuComponent menubar, Permissions permissions) {
		if (!permissions.isDeveloperEnvironment()) {
			return;
		}

		MenuComponent devMenu = menubar.addChild(getText("menu.Dev"));

		MenuComponent loggingSubmenu = devMenu.addChild(getText("menu.Dev.Logging"));
		loggingSubmenu.addChild(getText("menu.Dev.SystemLogging"), "LoggerConfig.action", "system_logging");
		loggingSubmenu.addChild(getText("menu.Dev.PageLogging"), "PageLogger.action", "page_logging");

		MenuComponent cacheSubmenu = devMenu.addChild(getText("menu.Dev.Caching"));
		cacheSubmenu.addChild(getText("menu.Dev.ClearCache"), "ClearCache.action", "clear_cache");
		cacheSubmenu.addChild(getText("menu.Dev.CacheStatistics"), "CacheStatistics.action", "cache_stats");

		buildCronSubmenu(devMenu);

		devMenu.addChild(getText("menu.Dev.ServerInformation"), "ServerInfo.action", "server_info");
		devMenu.addChild(getText("menu.Dev.AppProperties"), "ManageAppProperty.action", "manage_app_properties");
		devMenu.addChild(getText("menu.Dev.CssStyleGuide"), "PicsStyleGuide.action", "pics_style_guide");
		devMenu.addChild(getText("menu.Dev.ConOpFlagDifferences"), "ContractorFlagDifference.action",
				"flag_differences");
		devMenu.addChild(getText("menu.Dev.AuditScheduleBuilder"), "AuditScheduleBuilderCron.action",
				"audit_schedule_builder");

		devMenu.addChild(getText("menu.Dev.ConfigChanges"), "ConfigChanges.action", "config_changes");

		devMenu.addChild(getText("menu.Dev.Debug"), "#", "debug-menu");
		devMenu.addChild("Front-End Development Guide", "FrontendDevelopmentGuide.action", "front-end-dev-guide");

	}

	private static void buildCronSubmenu(MenuComponent devMenu) {
		MenuComponent cronSubmenu = devMenu.addChild(getText("menu.Dev.Crons"));
		cronSubmenu.addChild(getText("menu.Dev.ContractorCron"), "ContractorCron.action", "contractor_cron");
		cronSubmenu.addChild(getText("menu.Dev.MailCron"), "MailCron.action", "mail_cron");
		cronSubmenu.addChild(getText("menu.Dev.SubscriptionCron"), "SubscriptionCron.action", "subscription_cron");
	}

	private static void addManageMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent manageMenu = menubar.addChild(getText("menu.Manage"));

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			manageMenu.addChild(getText("NewContractorSearch.title"), SEARCH_FOR_NEW_URL, "NewContractorSearch");
		}
		if (permissions.hasPermission(OpPerms.ManageCorporate) || permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment)) {
			manageMenu.addChild("Client Accounts",
					"ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending",
					"ManageAccounts");
		}

		if (permissions.has(OpPerms.EditUsers)) {
			manageMenu.addChild(getText("menu.UserAccounts"), "UsersManage.action", "users_manage");
		}

		if (permissions.has(OpPerms.UserZipcodeAssignment)) {
			manageMenu.addChild(getText("global.AuditorAssignments"), "AuditorAssignmentMatrix.action",
					"auditor_assignment_matrix");
		}

		if (permissions.has(OpPerms.OfficeAuditCalendar)) {
			manageMenu.addChild(getText("AuditCalendar.title"), "AuditCalendar.action", "audit_calendar");
		}

		if (permissions.has(OpPerms.EditUsers)) {
			manageMenu.addChild(getText("ReportUserPermissionMatrix.title"), "ReportUserPermissionMatrix.action",
					"report_user_permission_matrix");
		}

		if (permissions.has(OpPerms.ContractorWatch)) {
			manageMenu.addChild(getText("ReportActivityWatch.title"), "ReportActivityWatch.action",
					"report_activity_watch");
		}

		if (permissions.has(OpPerms.WatchListManager)) {
			manageMenu.addChild(getText("WatchList.title"), "WatchListManager.action", "watch_list");
		}

		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate()) {
			manageMenu.addChild(getText("OperatorTags.title"), "OperatorTags.action", "OperatorTags");
		}

		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			manageMenu.addChild(getText("FlagCriteriaOperator.title"), "ManageFlagCriteriaOperator.action",
					"FlagCriteriaOperator");
			if (permissions.isCanSeeInsurance()) {
				manageMenu.addChild(getText("InsuranceCriteriaOperator.title"),
						"ManageInsuranceCriteriaOperator.action", "ManageInsuranceCriteriaOperator");
			}
		}

		if (permissions.has(OpPerms.FormsAndDocs)) {
			manageMenu.addChild(getText("Resources.title"), "Resources.action", "resources");
		}

		if (permissions.has(OpPerms.EditAccountDetails)) {
			String url = "FacilitiesEdit.action?operator=" + permissions.getAccountId();
			manageMenu.addChild(getText("global.Facilities"), url, "facilities_edit");
		}

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR)) {
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();
			}

			if (permissions.hasGroup(User.GROUP_MARKETING)) {
				custom = "?filter.accountManager=" + permissions.getUserId();
			}

			manageMenu.addChild(getText("global.FlagChanges"), "ReportFlagChanges.action" + custom,
					"report_flag_changes");
		}

		addEmailSubmenu(manageMenu, permissions);

		if (permissions.hasPermission(OpPerms.Billing)) {
			manageMenu.addChild("QuickBooks Sync", "QBSyncList.action?currency=USD", "QuickBooksSync_USD");
			manageMenu.addChild("QuickBooks Sync Edit", "QBSyncEdit.action", "QuickBooksSyncEdit");
		}
	}

	private static void addReportsMenu(MenuComponent menubar, List<ReportUser> favoriteReports, Permissions permissions) {
		MenuComponent reportsMenu = menubar.addChild(getText("menu.Reports"));

		reportsMenu.addChild(getText("menu.ManageReports"), ManageReports.LANDING_URL, "manage_reports");

		if (permissions.getAccountStatus() != AccountStatus.Demo)
			addLegacyReports(permissions, reportsMenu);

		if (permissions.has(OpPerms.Report)) {
			MenuComponent adminMenu = reportsMenu.addChild("Administration");
			adminMenu.addChild("Create Report", "CreateReport.action", "CreateReport");
			adminMenu.addChild("Report Tester", "ReportTester.action", "ReportTester");
		}

		FeatureToggle featureToggle = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		if (CollectionUtils.isNotEmpty(favoriteReports)) {
			reportsMenu.addChild("separator", null);
			MenuComponent favoriteLabel = buildUniqueFavoritesMenuComponent();

			reportsMenu.addChild(favoriteLabel);
		}

		for (ReportUser reportUser : favoriteReports) {
			Report report = reportUser.getReport();
			reportsMenu.addChild(report.getName(), "Report.action?report=" + report.getId(),
					"report_" + report.getId());
		}
	}

	private static void addLegacyReports(Permissions permissions, MenuComponent reportsMenu) {
		MenuComponent legacyMenu = reportsMenu.addChild("Legacy Reports");

		// BILLING
		if (permissions.hasPermission(OpPerms.Billing)) {
			legacyMenu.addChild("Billing Report", "ReportBilling.action?filter.status=Active&filter.status=Pending",
					"BillingReport");
			legacyMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action", "UnpaidInvoices");
			legacyMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action", "InvoiceSearch");
			legacyMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active",
					"ExpiredCCs");
			legacyMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action", "LifetimeMembers");
		}

		// CONTRACTORS
		FeatureToggle featureToggleChecker = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		if (featureToggleChecker != null
				&& featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
			legacyMenu.addChild(getText("ReportNewRequestedContractor.title"), "ReportRegistrationRequests.action",
					"ReportNewRequestedContractor");
		} else {
			legacyMenu.addChild(getText("ReportNewRequestedContractor.title"), "ReportNewRequestedContractor.action",
					"ReportNewRequestedContractor");
		}

		if (permissions.hasPermission(OpPerms.DelinquentAccounts)) {
			legacyMenu.addChild(getText("ArchivedContractorAccounts.title"), "ArchivedContractorAccounts.action",
					"ArchivedContractorAccounts");
			legacyMenu.addChild(getText("DelinquentContractorAccounts.title"), "DelinquentContractorAccounts.action",
					"DelinquentContractorAccounts");
		}

        if (permissions.hasPermission(OpPerms.AssignAudits)) {
            if (permissions.isOperatorCorporate()) {
                legacyMenu.addChild(getText("ScheduleAndAssign.title"),
                        "AuditAssignments.action?filter.status=Active",
                        "AuditAssignments");
            } else {
                legacyMenu.addChild(getText("ScheduleAndAssign.title"),
                        "AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17",
                        "AuditAssignments");
            }
        }

        if (permissions.hasPermission(OpPerms.ContractorApproval)) {
			legacyMenu.addChild(getText("ContractorApproval.title"), "ContractorApproval.action?filter.workStatus=P",
					"subMenu_ApproveContractors");
		}

		if (permissions.hasPermission(OpPerms.ContractorLicenseReport)) {
			legacyMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action", "ContractorLicenses");
		}

		if (permissions.isAdmin()
				|| (permissions.isOperatorCorporate() && permissions.getCorporateParent().contains(10566))) {
			legacyMenu.addChild(getText("ReportContractorScore.title"), "ReportContractorScore.action",
					"ReportContractorScore");
		}

		// USER
		if (permissions.hasPermission(OpPerms.EmployeeList)) {
			legacyMenu.addChild(getText("EmployeeList.title"), "EmployeeList.action");
		}

		if (permissions.seesAllContractors()) {
			legacyMenu.addChild("User Multi-Login", "MultiLoginUser.action", "MultiLogin");
		}

		// FLAGS
		if (permissions.isCorporate()) {
			legacyMenu.addChild(getText("ReportContractorOperatorFlag.title"), "ReportContractorOperatorFlag.action",
					"ReportContractorOperatorFlag");
		}

		if ((permissions.isCorporate() || permissions.getCorporateParent().size() > 0) && !permissions.isSecurity()) {
			legacyMenu.addChild(getText("ReportContractorOperatorFlagMatrix.title"),
					"ReportContractorOperatorFlagMatrix.action", "ReportContractorOperatorFlagMatrix");
		}

		if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.OperatorFlagMatrix)) {
			legacyMenu.addChild(getText("OperatorFlagMatrix.title"), "OperatorFlagMatrix.action", "OperatorFlagMatrix");
		}

		if (permissions.hasPermission(OpPerms.InsuranceVerification)) {
			legacyMenu.addChild(
					getText("PolicyVerification.title"),
					"PolicyVerification.action"
							+ (permissions.hasGroup(User.GROUP_CSR) ? "?filter.conAuditorId="
							+ permissions.getShadowedUserID() : ""), "PolicyVerification");
		}
		if (permissions.hasPermission(OpPerms.InsuranceApproval)) {
			legacyMenu.addChild(getText("ReportInsuranceApproval.title"),
					"ReportInsuranceApproval.action?filter.auditStatus=Complete", "RepInsApproval");
		}

		// OTHER
		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			legacyMenu.addChild("Sales Report", "ReportSalesReps.action", "SalesReport");
		}

		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			legacyMenu.addChild(getText("QuestionAnswerSearch.title"), "QuestionAnswerSearch.action",
					"QuestionAnswerSearch");
		}

		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			legacyMenu.addChild(getText("EmailQueueList.title"), "EmailQueueList.action?filter.status=Pending",
					"EmailQueue");
		}

		if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
			// legacyMenu.addChild(getText("GraphEmrRates.title"),
			// "GraphEmrRates.action?years=2010", "GraphEmrRates");
			legacyMenu.addChild(getText("ReportEmrRates.title"), "ReportEmrRates.action?filter.auditFor=2012",
					"ReportEmrRates");
			if (permissions.isAuditor()) {
				legacyMenu
						.addChild("Auditor Emr Rates Report", "ReportAuditorEmrRates.action", "ReportAuditorEmrRates");
			}
		}

		if (permissions.hasPermission(OpPerms.TRIRReport)) {
			// legacyMenu.addChild(getText("GraphTrirRates.title"),
			// "GraphTrirRates.action", "GraphTrirRates");
			legacyMenu.addChild(getText("ReportIncidenceRate.title"),
					"ReportIncidenceRate.action?filter.shaType=OSHA&filter.shaLocation=Corporate",
					"ReportIncidenceRate");
		}

		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			legacyMenu.addChild(getText("ReportFatalities.title"),
					"ReportFatalities.action?filter.auditFor=2012&filter.shaType=OSHA&filter.shaLocation=Corporate",
					"ReportFatalities");
		}
	}

	private static void addSupportMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));
		String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&os_username=admin&os_password=ad9870mins";
		supportMenu.addChild(getText("Header.HelpCenter"), helpUrl, "help_center");

		String mibewUrl = "#";
		try {
			mibewUrl = getMibewURL(permissions.getLocale(), permissions);
		} catch (UnsupportedEncodingException uee) {
			logger.warn("Unsupported encoding trying to get mibew url.");
		}

		supportMenu.addChild(getText("Registration.Error.LiveChat"), mibewUrl, "live_chat").setTarget("_blank");
		supportMenu.addChild(getText("global.ContactPICS"), "Contact.action", "contact_action");
		supportMenu.addChild(getText("global.AboutPICS"), "About.action", "about_pics");

		MenuComponent referenceMenu = supportMenu.addChild("Reference");
		if (permissions.hasPermission(OpPerms.ManageTrades)) {
			referenceMenu.addChild(getText("TradeTaxonomy.title"), "TradeTaxonomy.action", "TradeTaxonomy");
		}

		referenceMenu.addChild("Navigation Menu", "Reference!navigationMenu.action", "navigation_menu");
		referenceMenu.addChild("Navigation Restructure", "Reference!navigationRestructure.action",
				"navigation_restructure");
		referenceMenu.addChild("Dynamic Reports", "Reference!dynamicReport.action", "dynamic_report");
		referenceMenu.addChild("Reports Manager", "Reference!reportsManager.action", "reports_manager");
	}

	private static void addUserMenu(MenuComponent menu, Permissions permissions) {
		MenuComponent userMenu = menu.addChild(permissions.getName(), null, "user_menu");

		if (permissions.hasPermission(OpPerms.EditProfile)) {
			userMenu.addChild(getText("Account"), "ProfileEdit.action", "profile_edit");
		}

		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			userMenu.addChild("My Schedule", "MySchedule.action", "my_schedule");
		}

		FeatureToggle featureToggleChecker = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		if (featureToggleChecker != null && featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_V7MENUS)) {
			userMenu.addChild(getText("Menu.SwitchToVersion6"), "ProfileEdit!version6Menu.action?u=" + permissions.getUserId(), "switch_to_v6");
		}

		if (permissions.getAdminID() > 0) {
			userMenu.addChild("Switch Back", "Login.action?button=switchBack", "switch_back");
		}

		userMenu.addChild(getText("Header.Logout"), "Login.action?button=logout", "logout");
	}

	private static void addAuditsSubmenu(MenuComponent parentMenu, Permissions permissions) {
		MenuComponent auditsMenu = parentMenu.addChild(getText("global.Audits"));

		if (permissions.has(OpPerms.ManageAudits)) {
			auditsMenu.addChild(getText("menu.Audits.AuditDefinition"), "ManageAuditType.action", "manage_audit_type");
			auditsMenu.addChild(getText("menu.Audits.ManageAuditOptions"), "ManageOptionGroup.action",
					"manage_option_group");
		}

		if (permissions.has(OpPerms.ManageAudits, OpType.Edit)) {
			auditsMenu.addChild(getText("AuditCategoryMatrix.title"), "AuditCategoryMatrix.action",
					"audit_category_matrix");
		}

		if (permissions.has(OpPerms.ManageAuditTypeRules, OpType.Edit)) {
			auditsMenu.addChild(getText("menu.Audits.AuditTypeRules"), "AuditTypeRuleSearch.action",
					"audit_type_rule_search");
		}

		if (permissions.has(OpPerms.ManageCategoryRules, OpType.Edit)) {
			auditsMenu.addChild(getText("menu.Audits.CategoryRules"), "CategoryRuleSearch.action",
					"category_rule_search");
		}

		if (permissions.has(OpPerms.ManageAuditWorkFlow)) {
			auditsMenu.addChild(getText("menu.Audits.Workflows"), "ManageAuditWorkFlow.action",
					"manage_audit_work_flow");
		}

		if (!auditsMenu.hasChildren()) {
			boolean removed = parentMenu.removeChild(auditsMenu);

			if (!removed) {
				logger.warn("Unable to remove audit menu with no children.");
			}
		}
	}

	private static void addEmailSubmenu(MenuComponent parentMenu, Permissions permissions) {
		MenuComponent emailMenu = parentMenu.addChild(getText("global.Email"));

		if (permissions.seesAllContractors()) {
			emailMenu.addChild(getText("ProfileEdit.label.EmailSubscriptions"), "ReportEmailSubscription.action",
					"report_email_subscription");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			emailMenu.addChild(getText("EmailWizard.title"), "EmailWizard.action", "EmailWizard");
		}

		if (permissions.has(OpPerms.EmailTemplates, OpType.Edit)) {
			emailMenu.addChild(getText("EditEmailTemplate.title"), "EditEmailTemplate.action", "edit_email_template");

			if (permissions.isPicsEmployee()) {
				emailMenu.addChild(getText("menu.Email.EmailExclusions"), "EditEmailExclusions.action",
						"edit_email_exclusions");
			}
		}

		if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			emailMenu.addChild("New Year Mailer", "NewYearMailer.action", "NewYearMailer");
		}

		if (!emailMenu.hasChildren()) {
			boolean removed = parentMenu.removeChild(emailMenu);

			if (!removed) {
				logger.warn("Unable to remove email menu with no children.");
			}
		}
	}

	private static void handleSingleChildMenu(MenuComponent menu) {
		if (menu.getChildren().size() == 1) {
			menu = menu.getChildren().get(0);
		}
	}

	public static String getHomePage(MenuComponent menu, Permissions permissions) {
		if (permissions.isContractor()) {
			return "ContractorView.action";
		}

		if (permissions.has(OpPerms.Dashboard)) {
			return "Home.action";
		}

		if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty()) {
			return "Home.action";
		}

		String url = null;
		for (MenuComponent subMenu : menu.getChildren()) {
			url = subMenu.getUrl();
			if (!Strings.isEmpty(url)) {
				return url;
			}

			for (MenuComponent subSubMenu : subMenu.getChildren()) {
				url = subSubMenu.getUrl();
				if (!Strings.isEmpty(url)) {
					return url;
				}
			}
		}

		return url;
	}

	private static String getText(String key) {
		return translationService.getText(key, TranslationActionSupport.getLocaleStatic());
	}

	private static MenuComponent buildUniqueFavoritesMenuComponent() {
		MenuComponent menuComponent = new MenuComponent();
		menuComponent.setName("Favorites"); // this should be i18n
		// This should always be in English because the front-end is using it to
		// determine what to replace
		menuComponent.setHtmlId("Favorites");
		menuComponent.setCssClass("label");
		return menuComponent;
	}

	// TODO find out where these menus should go
	private static void orphanedMenus(MenuComponent menu, Permissions permissions) {
		// We're trying to get rid of this
		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			menu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action", "create_auditor_invoices");
		}

		// From dev menu
		menu.addChild("Exception Log", "ReportExceptions.action", "exception_log");
		menu.addChild("Batch Insert Translations", "BatchTranslations.action", "batch_insert_trans");

		// From user menu
		menu.addChild("Schedule", "MySchedule.action", "my_schedule");
	}

	public static String getMibewURL(Locale locale, Permissions permissions) throws UnsupportedEncodingException {
		String mibew_language_code = translationService.getText("Mibew.LanguageCode", locale);

		StringBuilder mibewURL = new StringBuilder();
		mibewURL.append("https://chat.picsorganizer.com/client.php?");
		mibewURL.append("locale=");
		mibewURL.append(URLEncoder.encode(mibew_language_code, "UTF-8"));
		mibewURL.append("&style=PICS&name=");
		mibewURL.append(URLEncoder.encode(permissions.getName(), "UTF-8"));
		mibewURL.append("&accountName=");
		mibewURL.append(URLEncoder.encode(permissions.getAccountName(), "UTF-8"));
		mibewURL.append("&accountId=");
		mibewURL.append(permissions.getAccountId());
		mibewURL.append("&userId=");
		mibewURL.append(permissions.getUserId());
		mibewURL.append("&email=");
		mibewURL.append(URLEncoder.encode(permissions.getEmail(), "UTF-8"));
		return mibewURL.toString();
	}
}
