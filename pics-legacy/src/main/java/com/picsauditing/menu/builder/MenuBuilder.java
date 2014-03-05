package com.picsauditing.menu.builder;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserService;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.search.Database;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.URLUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
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

	public static ReportUserDAO reportUserDAO;

	private MenuBuilder() {
	}

	public static MenuComponent buildMenubar(Permissions permissions) {
		return buildMenubar(permissions, Collections.<ReportUser>emptyList());
	}

	public static MenuComponent buildMenubar(Permissions permissions, List<ReportUser> favoriteReports) {
		MenuComponent menubar = new MenuComponent();

		if (permissions == null || !permissions.isLoggedIn()) {
			buildNotLoggedInMenubar(menubar);
		} else {
			if (permissions.getUserId() > 0) {
				if (inEmployeeMode()) {
					buildEGMenubar(menubar, permissions);
				} else {
					buildPICSMenu(permissions, favoriteReports, menubar);
				}
			} else if (permissions.getAppUserID() > 0) {
				buildEGMenubar(menubar, permissions);
			}

			addUserMenu(menubar, permissions);
		}

		handleSingleChildMenu(menubar);

		return menubar;
	}

	private static void buildPICSMenu(Permissions permissions, List<ReportUser> favoriteReports, MenuComponent menubar) {
		if (permissions.isContractor()) {
			buildContractorMenubar(menubar, permissions);
		} else if (permissions.isAssessment()) {
			buildAssessmentMenubar(menubar, permissions);
		} else if (permissions.isOperatorCorporate()) {
			buildOperatorCorporateMenubar(menubar, permissions, favoriteReports);
		} else {
			buildGeneralMenubar(menubar, permissions, favoriteReports);
		}
	}

	private static void buildEGMenubar(MenuComponent menubar, Permissions permissions) {
		ProfileService profileService = SpringUtils.getBean("ProfileService");
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		int id = profile == null ? 1 : profile.getId();

		menubar.addChild("Skills", "/employee-guard/employee/skills", "employee_skills");
		menubar.addChild("Profile", "/employee-guard/employee/profile/" + id, "employee_profile");
	}

	// For Operators, Corporate users, and PICS employees
	private static void buildGeneralMenubar(MenuComponent menubar, Permissions permissions,
	                                        List<ReportUser> favoriteReports) {
		addCompanyMenu(menubar, permissions);
		addReportsMenu(menubar, favoriteReports, permissions);
		addManageMenu(menubar, permissions);
		addConfigureMenu(menubar, permissions);
		addDevelopmentMenu(menubar, permissions);
		addSupportMenu(menubar, permissions);
	}

	private static void buildNotLoggedInMenubar(MenuComponent menubar) {
		menubar.addChild(getText("global.Home"), "index.jsp", "index");
	}

	private static void buildContractorMenubar(MenuComponent menubar, Permissions permissions) {
		addCompanyMenu(menubar, permissions);

		ContractorAccountDAO contractorAccountDAO = SpringUtils.getBean(SpringUtils.CONTRACTOR_ACCOUNT_DAO);
		ContractorSubmenuBuilder contractorSubmenuBuilder = SpringUtils.getBean(SpringUtils.CONTRACTOR_SUBMENU_BUILDER);
		ContractorAccount contractor = contractorAccountDAO.find(permissions.getAccountId());

		try {
			MenuComponent menu = contractorSubmenuBuilder.buildMenubar(contractor, permissions, false);
			for (MenuComponent component : menu.getChildren()) {
				menubar.addChild(component);
			}
		} catch (Exception e) {
			logger.error("Unable to build menu for contractor {}", permissions.getAccountId());
		}

		addSupportMenu(menubar, permissions);
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

        removeMenuIfEmpty(menubar, assessmentMenu);

		addSupportMenu(menubar, permissions);
	}

	private static void buildOperatorCorporateMenubar(MenuComponent menubar, Permissions permissions,
	                                                  List<ReportUser> favoriteReports) {
		addCompanyMenu(menubar, permissions);
		addReportsMenu(menubar, favoriteReports, permissions);
		addManageMenu(menubar, permissions);
		addSupportMenu(menubar, permissions);
	}

	private static void addCompanyMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent companyMenu = menubar.addChild(getText("global.Company"));
		URLUtils urlUtils = new URLUtils();

		if (!permissions.isContractor()) {
			companyMenu.addChild(getText("ContractorSubmenu.MenuItem.Dashboard"), "Home.action", "dashboard");

			if (permissions.isOperatorCorporate()) {
				String operatorNotes = urlUtils.getActionUrl("OperatorNotes", "id", permissions.getAccountId());
				companyMenu.addChild(getText("global.Notes"), operatorNotes, "operator_notes");
			}

			if (permissions.has(OpPerms.EditUsers)) {
				companyMenu.addChild(getText("global.Users"), "UsersManage.action", "users_manage");
			}

			if (permissions.has(OpPerms.FormsAndDocs)) {
				companyMenu.addChild(getText("Resources.title"), "Resources.action", "resources");
			}

			if (permissions.isOperatorCorporate() && permissions.has(OpPerms.EditAccountDetails)) {
				String operatorEdit = urlUtils.getActionUrl("FacilitiesEdit", "operator", permissions.getAccountId());
				companyMenu.addChild(getText("menu.CompanyProfile"), operatorEdit, "facilities_edit");
			}
		}

		if (permissions.isContractor() && !permissions.isInsuranceOnlyContractorUser()) {
			addCompanyMenuLinksFor(permissions.getAccountId(), !permissions.getAccountStatus().isDemo(), companyMenu, permissions);
		}
	}

	public static MenuComponent getCompanyMenuFor(ContractorAccount contractor, Permissions permissions) {
		MenuComponent companyMenu = new MenuComponent(getText("global.Company"));
		addCompanyMenuLinksFor(contractor.getId(), !contractor.getStatus().isDemo(), companyMenu, permissions);
		return companyMenu;
	}

	private static void addCompanyMenuLinksFor(int accountId, boolean showBillingMenu, MenuComponent companyMenu, Permissions permissions) {
		URLUtils urlUtils = new URLUtils();

		String contractorTrades = urlUtils.getActionUrl("ContractorTrades", "id", accountId);
		String contractorView = urlUtils.getActionUrl("ContractorView", "id", accountId);
		String contractorNotes = urlUtils.getActionUrl("ContractorNotes", "id", accountId);

		companyMenu.addChild(getText("ContractorSubmenu.MenuItem.Dashboard"), contractorView, "contractor_dashboard");
		companyMenu.addChild(getText("global.Notes"), contractorNotes, "contractor_notes");
		companyMenu.addChild(getText("ContractorTrades.title"), contractorTrades, "contractor_trades");

		if (permissions.isContractor() || permissions.isPicsEmployee()) {
			if (permissions.isShowClientSitesLink()) {
				String contractorFacilities = urlUtils.getActionUrl("ContractorFacilities", "id", accountId);
				companyMenu.addChild(getText("global.Facilities"), contractorFacilities, "contractor_facilities");
			}

			String contractorEdit = urlUtils.getActionUrl("ContractorEdit", "id", accountId);
			String contractorUsers = urlUtils.getActionUrl("UsersManage", "account", accountId);

			companyMenu.addChild(getText("global.Users"), contractorUsers, "users_manage");

			if (permissions.isContractor()) {
				companyMenu.addChild(getText("global.Resources"), "ContractorForms.action", "contractor_forms");
			}

			if (showBillingMenu) {
				String billingDetail = urlUtils.getActionUrl("BillingDetail", "id", accountId);
				String paymentOptions = urlUtils.getActionUrl("ContractorPaymentOptions", "id", accountId);

				companyMenu.addChild(getText("BillingDetail.title"), billingDetail, "billing_detail");
				companyMenu.addChild(getText("ContractorPaymentOptions.header"), paymentOptions, "payment_options");
			}

			companyMenu.addChild(getText("menu.CompanyProfile"), contractorEdit, "contractor_edit");
		}
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

		if (permissions.has(OpPerms.DevelopmentEnvironment)) {
			configureMenu.addChild("Manage Countries", "Report.action?report=1010",
					"manage_countries");
		}

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

        removeMenuIfEmpty(menubar, configureMenu);
    }

	private static void addDevelopmentMenu(MenuComponent menubar, Permissions permissions) {
		if (!permissions.isDeveloperEnvironment()) {
			return;
		}

		MenuComponent devMenu = menubar.addChild(getText("menu.Dev"));
		devMenu.addChild(getText("menu.Dev.AppProperties"), "ManageAppProperty.action", "manage_app_properties");

		MenuComponent loggingSubmenu = devMenu.addChild(getText("menu.Dev.Logging"));
		loggingSubmenu.addChild(getText("menu.Dev.SystemLogging"), "LoggerConfig.action", "system_logging");
		loggingSubmenu.addChild(getText("menu.Dev.PageLogging"), "PageLogger.action", "page_logging");
		loggingSubmenu.addChild(getText("menu.Exceptions"), "ReportExceptions.action", "report_exceptions");

		MenuComponent cacheSubmenu = devMenu.addChild(getText("menu.Dev.Caching"));
		cacheSubmenu.addChild(getText("menu.Clear"), "ClearCache.action", "clear_cache");
		cacheSubmenu.addChild(getText("global.Statistics"), "CacheStatistics.action", "cache_stats");

		buildCronSubmenu(devMenu);

		devMenu.addChild(getText("menu.Dev.ServerInformation"), "ServerInfo.action", "server_info");
		devMenu.addChild(getText("menu.Dev.ConOpFlagDifferences"), "ContractorFlagDifference.action",
				"flag_differences");

		devMenu.addChild(getText("menu.Dev.ConfigChanges"), "ConfigChanges.action", "config_changes");

		devMenu.addChild(getText("menu.Dev.Debug"), "#", "debug-menu");
		devMenu.addChild("PICS Style Guide", "FrontendDevelopmentGuide.action", "front-end-dev-guide");

		if (permissions.hasPermission(OpPerms.Billing)) {
			devMenu.addChild("Process QB XML", "ProcessQBResponseXML.action", "process_qb_response_xml");
		}

		buildEmployeeGUARD(devMenu);

        removeMenuIfEmpty(menubar, devMenu);
	}

	private static void buildEmployeeGUARD(MenuComponent devMenu) {
		MenuComponent employeeGUARD = devMenu.addChild("EmployeeGUARD");

		employeeGUARD.addChild("Operator Dashboard", "/employee-guard/operators/dashboard");
		employeeGUARD.addChild("Contractor Dashboard", "/employee-guard/contractor/dashboard");
		employeeGUARD.addChild("Employee Dashboard", "/employee-guard/employee/dashboard");

        removeMenuIfEmpty(devMenu, employeeGUARD);
	}

	private static void buildCronSubmenu(MenuComponent devMenu) {
		MenuComponent cronSubmenu = devMenu.addChild(getText("menu.Dev.Crons"));
		cronSubmenu.addChild(getText("global.Contractor"), "ContractorCron.action", "contractor_cron");
		cronSubmenu.addChild(getText("menu.Mail"), "MailCron.action", "mail_cron");
		cronSubmenu.addChild(getText("menu.Dev.AuditScheduleBuilder"), "AuditScheduleBuilderCron.action",
				"audit_schedule_builder");

        removeMenuIfEmpty(devMenu, cronSubmenu);
	}

	private static void addManageMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent manageMenu = menubar.addChild(getText("menu.Manage"));

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			manageMenu.addChild(getText("NewContractorSearch.title"), SEARCH_FOR_NEW_URL, "NewContractorSearch");
		}

		addEmailSubmenu(manageMenu, permissions);

		if (permissions.hasPermission(OpPerms.ManageCorporate) || permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment)) {
			manageMenu.addChild("Client Accounts",
					"ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending",
					"ManageAccounts");
		}

		if (permissions.hasPermission(OpPerms.ManageCsrAssignment)) {
			manageMenu.addChild("Recommended CSR Assignment", "ManageRecommendedCSRAssignment.action",
					"RecommendedCsrAssignment");
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

		if (permissions.hasPermission(OpPerms.InsuranceApproval)) {
			manageMenu.addChild(getText("ReportInsuranceApproval.title"),
					"ReportInsuranceApproval.action?filter.auditStatus=Complete", "RepInsApproval");
		}

		if (permissions.isPicsEmployee() && permissions.has(OpPerms.EditAccountDetails)) {
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

		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			manageMenu.addChild(getText("QuestionAnswerSearch.title"), "QuestionAnswerSearch.action",
					"QuestionAnswerSearch");
		}

		if (permissions.hasPermission(OpPerms.Billing)) {
			manageMenu.addChild("QuickBooks Sync", "QBSyncList.action?currency=USD", "QuickBooksSync_USD");
			manageMenu.addChild("QuickBooks Sync Edit", "QBSyncEdit.action", "QuickBooksSyncEdit");
		}

		if (permissions.isOperatorCorporate()) {
			ProductSubscriptionService productSubscriptionService = SpringUtils.getBean(SpringUtils.PRODUCT_SUBSCRIPTION_SERVICE);
			if (productSubscriptionService.hasEmployeeGUARD(permissions.getAccountId())) {
				manageMenu.addChild("EmployeeGUARD", "/employee-guard/operators/dashboard");
			}
		}

		// FIXME Remove when no longer needed!
		if (permissions.isOperatorCorporate() && permissions.getAccountStatus().isDemo()) {
			manageMenu.addChild(getText("RequestCompany.title"), "RequestNewContractorAccount.action", "RequestNewContractorAccount");
		}

        removeMenuIfEmpty(menubar, manageMenu);
    }

	private static void addReportsMenu(MenuComponent menubar, List<ReportUser> favoriteReports, Permissions permissions) {
		MenuComponent reportsMenu = menubar.addChild(getText("menu.Reports"));

		if (favoriteReports.isEmpty()) {
			reportsMenu.addChild(getText("menu.ReportsManager.GettingStarted"), "Reference!reportsManager.action?from=ReportsMenu", "getting_started");
		} else {
			reportsMenu.addChild(getText("menu.ManageReports"), ManageReports.LANDING_URL, "manage_reports");

			if (permissions.getAccountStatus() != AccountStatus.Demo)
				addLegacyReports(permissions, reportsMenu);

			if (permissions.has(OpPerms.Report)) {
				MenuComponent adminMenu = reportsMenu.addChild("Administration");
				adminMenu.addChild("Create Report", "CreateReport.action", "CreateReport");
				adminMenu.addChild("Report Tester", "ReportTester.action", "ReportTester");
				adminMenu.addChild("Report Field Directory", "ReportTester!directory.action", "ReportFieldDirectory");
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

        removeMenuIfEmpty(menubar, reportsMenu);
	}

	private static void addLegacyReports(Permissions permissions, MenuComponent reportsMenu) {
		MenuComponent legacyMenu = reportsMenu.addChild("Legacy Reports");

		if (permissions.isOperatorCorporate() && permissions.getLinkedGeneralContractors().size() > 0) {
			legacyMenu.addChild(getText("GeneralContractorList.title"), "GeneralContractorsList.action",
					"GeneralContractorsList");
			legacyMenu.addChild(getText("SubcontractorFlagMatrix.title"), "SubcontractorFlagMatrix.action",
					"SubcontractorFlagMatrix");
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

		if (permissions.hasPermission(OpPerms.AssignAudits)) {
			if (permissions.isOperatorCorporate()) {
				legacyMenu.addChild("Sched. &amp; Assign",
						"AuditAssignments.action?filter.status=Active",
						"AuditAssignments");
			} else {
				legacyMenu.addChild("Sched. &amp; Assign",
						"AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17",
						"AuditAssignments");
			}
		}

		if (permissions.hasPermission(OpPerms.RiskRank)) {
			legacyMenu.addChild("Contractor Risk Assessment", "ReportContractorRiskLevel.action", "ContractorRiskLevel");
		}

        if (permissions.isOperatorCorporate() && permissions.getLinkedGeneralContractors().size() > 0) {
            legacyMenu.addChild(getText("GeneralContractorList.title"), "GeneralContractorsList.action",
                    "GeneralContractorsList");
            legacyMenu.addChild(getText("SubcontractorFlagMatrix.title"), "SubcontractorFlagMatrix.action",
                    "SubcontractorFlagMatrix");
        }

        if (permissions.hasPermission(OpPerms.ContractorApproval)) {
			legacyMenu.addChild(getText("ContractorApproval.title"), "ContractorApproval.action?filter.workStatus=P",
					"subMenu_ApproveContractors");
		}

		if (permissions.isAdmin()
				|| (permissions.isOperatorCorporate() && permissions.getCorporateParent().contains(10566))) {
			legacyMenu.addChild(getText("ReportContractorScore.title"), "ReportContractorScore.action",
					"ReportContractorScore");
		}

		// FLAGS
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

		// OTHER
		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			legacyMenu.addChild("Sales Report", "ReportSalesReps.action", "SalesReport");
		}

        removeMenuIfEmpty(reportsMenu, legacyMenu);
	}

	private static void addSupportMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));
		supportMenu.addChild(getText("Header.HelpCenter"), "HelpCenter.action", "help_center");

		String mibewUrl = "#";
		try {
			mibewUrl = getMibewURL(permissions.getLocale(), permissions);
		} catch (UnsupportedEncodingException uee) {
			logger.warn("Unsupported encoding trying to get mibew url.");
		}

		supportMenu.addChild(getText("Registration.Error.LiveChat"), mibewUrl, "live_chat").setTarget("_blank");
		supportMenu.addChild(getText("global.ContactPICS"), "ContactUs.action", "contact_action");
		supportMenu.addChild(getText("global.AboutPICS"), "About.action", "about_pics");

        addReferenceMenu(permissions, supportMenu);

        removeMenuIfEmpty(menubar, supportMenu);
	}

    private static void addReferenceMenu(Permissions permissions, MenuComponent supportMenu) {
        MenuComponent referenceMenu = supportMenu.addChild(getText("menu.Reference"));
        if (permissions.hasPermission(OpPerms.ManageTrades)) {
            referenceMenu.addChild(getText("TradeTaxonomy.title"), "TradeTaxonomy.action", "TradeTaxonomy");
        }

        referenceMenu.addChild(getText("NavigationMenu.title"), "Reference!navigationMenu.action", "navigation_menu");
        referenceMenu.addChild(getText("NavigationRestructure.title"), "Reference!navigationRestructure.action",
                "navigation_restructure");

        if (!permissions.isContractor()) {
            referenceMenu.addChild(getText("DynamicReports.title"), "Reference!dynamicReport.action", "dynamic_report");
            referenceMenu.addChild(getText("ReportsManager.title"), "Reference!reportsManager.action", "reports_manager");
        }

        removeMenuIfEmpty(supportMenu, referenceMenu);
    }

    private static void addUserMenu(MenuComponent menu, Permissions permissions) {
		MenuComponent userMenu = menu.addChild(permissions.getName(), null, "user_menu");

		if (permissions.hasPermission(OpPerms.EditProfile)) {
			userMenu.addChild(getText("menu.Profile"), "ProfileEdit.action", "profile_edit");
		}

		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			userMenu.addChild("My Schedule", "MySchedule.action", "my_schedule");
		}

		FeatureToggle featureToggleChecker = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		if (featureToggleChecker != null && featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_V7MENUS)) {
			userMenu.addChild(getText("Menu.SwitchToVersion6"), "ProfileEdit!version6Menu.action?u=" + permissions.getUserId(), "switch_to_v6");

			if (isBothEGandPOUser(permissions)) {
				if (inAdminMode()) {
					userMenu.addChild(getText("Menu.SwitchToEmployeeMode"), "employee-guard/employee/dashboard", "");
				}
			}
		}

		if (switchedToAnotherUser(permissions)) {
			userMenu.addChild(getText("SwitchBack.title"), "Login.action?button=switchBack", "switch_back");
		}

		userMenu.addChild(getText("Header.Logout"), "Login.action?button=logout", "logout");

        removeMenuIfEmpty(menu, userMenu);
    }

    public static void removeMenuIfEmpty(MenuComponent menu, MenuComponent submenu) {
        if (!submenu.hasChildren()) {
            boolean removed = menu.removeChild(submenu);

            if (!removed) {
                logger.warn("Unable to remove email menu with no children.");
            }
        }
    }

    private static boolean isBothEGandPOUser(Permissions permissions) {
		int appUserID = permissions.getAppUserID();
		return permissions.getUserId() > 0 && isEmployeeGUARDUser(appUserID);
	}

	private static boolean isPicsOrgUser(int appUserID) {
		UserService userService = SpringUtils.getBean("UserService");
		return userService.findByAppUserId(appUserID) != null;
	}

	private static boolean isPicsOrgUser(Permissions permissions) {
		return permissions.getUserId() > 0;
	}

	private static boolean isEmployeeGUARDUser(int appUserID) {
		ProfileService profileService = SpringUtils.getBean("ProfileService");
		return profileService.findByAppUserId(appUserID) != null;
	}

	private static boolean inAdminMode() {
		return !ServletActionContext.getRequest().getRequestURI().contains("employee-guard");
	}

	private static boolean inEmployeeMode() {
		return ServletActionContext.getRequest().getRequestURI().contains("employee-guard/employee");
	}

	private static boolean switchedToAnotherUser(Permissions permissions) {
		return permissions.getAdminID() > 0 && permissions.getAdminID() != permissions.getUserId();
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

        removeMenuIfEmpty(parentMenu, auditsMenu);
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

		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			emailMenu.addChild(getText("EmailQueueList.title"), "EmailQueueList.action?filter.status=Pending",
					"EmailQueue");
		}

		if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			emailMenu.addChild("New Year Mailer", "NewYearMailer.action", "NewYearMailer");
		}

        removeMenuIfEmpty(parentMenu, emailMenu);
    }

	private static void handleSingleChildMenu(MenuComponent menu) {
		if (menu.getChildren().size() == 1) {
			menu = menu.getChildren().get(0);
		}
	}

	public static String getHomePage(Permissions permissions) {
		if (permissions.isContractor()) {
			return "ContractorView.action";
		}

		if (permissions.has(OpPerms.Dashboard)) {
			return "Home.action";
		}

		List<ReportUser> reportUsers = reportUserDAO.findAllFavorite(permissions.getUserId());
		Report report = null;

		if (reportUsers != null && !reportUsers.isEmpty()) {
			report = reportUsers.get(0).getReport();
		}

		if (report != null) {
			return "Report.action?report=" + report.getId();
		} else {
			return "Reference!reportsManager.action?from=ReportsMenu";
		}
	}

	private static String getText(String key) {
		return TranslationServiceFactory.getTranslationService().getText(key, TranslationActionSupport.getLocaleStatic());
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
		String mibew_language_code = TranslationServiceFactory.getTranslationService().getText("Mibew.LanguageCode", locale);

		StringBuilder mibewURL = new StringBuilder("https://chat.picsorganizer.com/client.php?")
				.append("locale=").append(URLEncoder.encode(mibew_language_code, "UTF-8"))
				.append("&style=PICS");
		if (permissions.isLoggedIn()) {
			mibewURL.append("&name=").append(URLEncoder.encode(permissions.getName(), "UTF-8"))
					.append("&accountName=").append(URLEncoder.encode(permissions.getAccountName(), "UTF-8"))
					.append("&accountId=").append(permissions.getAccountId())
					.append("&userId=").append(permissions.getUserId())
					.append("&email=").append(URLEncoder.encode(permissions.getEmail(), "UTF-8")).toString();
		}
		return mibewURL.toString();
	}
}
