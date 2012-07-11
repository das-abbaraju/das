package com.picsauditing.access;

import java.util.List;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

/**
 * This is a rewrite of PicsMenu.java for the version 7.0.
 */
// TODO Verify that all active menus are being translated
public class MenuBuilder {

	private static I18nCache i18nCache = I18nCache.getInstance();

	private MenuBuilder() {
	}

	public static MenuComponent buildMenubar(Permissions permissions) {
		return buildMenubar(permissions, null);
	}

	@SuppressWarnings("unused")
	public static MenuComponent buildMenubar(Permissions permissions, List<ReportUser> favoriteReports) {
		MenuComponent menubar = new MenuComponent();
		if (menubar == null)
			return null;

		if (permissions == null || !permissions.isLoggedIn()) {
			buildNotLoggedInMenubar(menubar);
		} else if (permissions.isContractor()) {
			buildContractorMenubar(menubar, permissions);
		} else if (permissions.isAssessment()) {
			buildAssessmentMenubar(menubar);
		} else if (permissions.isOperatorCorporate()) {
			// TODO make this actually do something
			buildOperatorCorporateMenubar(menubar, permissions);
		} else {
			buildGeneralMenubar(menubar, permissions, favoriteReports);
		}

		handleSingleChildMenu(menubar);

		return menubar;
	}

	// For Operators, Corporate users, and PICS employees
	private static void buildGeneralMenubar(MenuComponent menubar, Permissions permissions, List<ReportUser> favoriteReports) {
		addDashboard(menubar);

		addReportsMenu(menubar, favoriteReports);

		//addContractorSubmenu(menu, permissions);

		addConfigureMenu(menubar, permissions);

		addManageMenu(menubar, permissions);

		addDevelopmentMenu(menubar, permissions);

		addSupportMenu(menubar);

		addUserMenu(menubar, permissions);
	}

	private static void buildNotLoggedInMenubar(MenuComponent menubar) {
		menubar.addChild(getText("global.Home"), "index.jsp", "index");
	}

	private static void buildContractorMenubar(MenuComponent menubar, Permissions permissions) {
		// Don't show a menu for Contractors, they will use their sub menu for now
		if (!permissions.getAccountStatus().isActiveDemo()) {
			menubar.addChild(getText("Registration.CompanyDetails.heading"), "ContractorEdit.action", "contractor_edit");
			addSupportMenu(menubar);
			return;
		}

		menubar.addChild(getText("global.Home"), "ContractorView.action", "contractor_view");

		// Don't show for insurance only users
		if (!permissions.isInsuranceOnlyContractorUser()) {
			MenuComponent companyMenu = menubar.addChild(getText("global.Company"));
			companyMenu.addChild(getText("menu.Contractor.WhereWeWork"), "ContractorFacilities.action", "contractor_facilities");
			companyMenu.addChild(getText("menu.Contractor.ActivityLog"), "ContractorNotes.action", "contractor_notes");
		}

		if (permissions.has(OpPerms.ContractorAdmin)) {
			MenuComponent editMenu = menubar.addChild(getText("button.Edit"));
			editMenu.addChild(getText("menu.Contractor.CompanyAccount"), "ContractorEdit.action", "contractor_edit");
			editMenu.addChild(getText("global.Users"), "UsersManage.action", "users_manage");
			editMenu.addChild(getText("ContractorTrades.title"), "ContractorTrades.action", "contractor_trades");

			if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
				editMenu.addChild(getText("ManageEmployees.title"), "ManageEmployees.action", "manage_employees");

			if (permissions.isRequiresCompetencyReview()) {
				MenuComponent hseMenu = menubar.addChild(getText("global.HSECompetencies"));
				hseMenu.addChild(getText("ManageJobRoles.title"), "ManageJobRoles.action", "manage_job_roles");
				hseMenu.addChild(getText("EmployeeCompetencies.title"), "EmployeeCompetencies.action", "employee_competencies");
			}

//			if (permissions.isRequiresOQ()) {
				//MenuComponent operatorQualMenu = menu.addChild(getText("global.OperatorQualification"));
				//String url = "ReportOQEmployees.action?orderBy=e.lastName,e.firstName";
				//operatorQualMenu.addChild(getText("ReportOQEmployees.title"), url, "report_oq_employees");
				//operatorQualMenu.addChild(getText("ReportOQChanges.title"), "ReportOQChanges.action", "report_oq_changes");
				//operatorQualMenu.addChild(getText("ReportNewProjects.title"), "ReportNewProjects.action", "report_new_projects");
//			}
		}

		if (permissions.has(OpPerms.ContractorBilling)) {
			MenuComponent billingMenu = menubar.addChild(getText("menu.Billing"));
			billingMenu.addChild(getText("menu.Contractor.BillingDetails"), "BillingDetail.action", "billing_detail");
			billingMenu.addChild(getText("menu.Contractor.PaymentOptions"), "ContractorPaymentOptions.action", "contractor_payment_options");
		}

		menubar.addChild(getText("global.Resources"), "ContractorForms.action", "contractor_forms");

		addSupportMenu(menubar);
	}

	private static void buildAssessmentMenubar(MenuComponent menubar) {
		MenuComponent assessmentMenu = menubar.addChild(getText("menu.Assessment"));
		assessmentMenu.addChild("Imported Data", "ManageImportData.action", "manage_import_data");
		assessmentMenu.addChild("Assessment Tests", "ManageAssessmentTests.action", "manage_assessment_tests");
		assessmentMenu.addChild("Test Mapping", "ManageUnmappedTests.action", "manage_unmapped_tests");
		assessmentMenu.addChild("Assessment Results", "ManageAssessmentResults.action", "manage_assessment_results");
		assessmentMenu.addChild("Companies", "ManageMappedCompanies.action", "manage_mapped_companies");
		assessmentMenu.addChild("Company Mapping", "ManageUnmappedCompanies.action", "manage_unmapped_companies");

		// TODO ask matt about this
//		MenuComponent editMenu = menubar.addChild("Edit");
//		editMenu.addChild("Account", "AssessmentCenterEdit.action", "assessment_center_edit");
//		editMenu.addChild("Users", "UsersManage.action", "users_manage");

		addSupportMenu(menubar);
	}

	private static void buildOperatorCorporateMenubar(MenuComponent menubar, Permissions permissions) {
		// TODO Flesh this out
	}

	private static void addConfigureMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent configureMenu = menubar.addChild(getText("menu.Configure"));
		// We're aliasing this menu because menu components could be added in a submenu, or the main menu
		MenuComponent auditsMenu = configureMenu;

		if (permissions.has(OpPerms.Translator)) {
			MenuComponent translationMenu = configureMenu.addChild("Translations");
			translationMenu.addChild("Manage Translations", "ManageTranslations.action", "manage_translations");

			if (permissions.has(OpPerms.DevelopmentEnvironment)) {
				translationMenu.addChild("Import/Export Translations", "TranslationETL.action", "im_ex_trans");
				translationMenu.addChild("Unsynced Translations", "UnsyncedTranslations.action", "unsynced_translations");
			}
			translationMenu.addChild("View Traced Translations", "ManageTranslations.action?showDoneButton=true", "traced_translations")
				.setTarget("_BLANK");
		}

		if (permissions.has(OpPerms.ManageAudits)) {
			auditsMenu = configureMenu.addChild(getText("global.Audits"));
			auditsMenu.addChild("Audit Definition", "ManageAuditType.action", "manage_audit_type");
			auditsMenu.addChild("Manage Audit Options", "ManageOptionGroup.action", "manage_option_group");

			if (permissions.has(OpPerms.ManageAudits, OpType.Edit)) {
				auditsMenu.addChild(getText("AuditCategoryMatrix.title"), "AuditCategoryMatrix.action", "audit_category_matrix");
			}
		}

		if (permissions.has(OpPerms.ManageAudits))
			configureMenu.addChild("Flag Criteria", "ManageFlagCriteria.action", "manage_flag_criteria");

		if (permissions.has(OpPerms.ManageAuditTypeRules, OpType.Edit))
			auditsMenu.addChild("Audit Type Rules", "AuditTypeRuleSearch.action", "audit_type_rule_search");

		if (permissions.has(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			if (permissions.isCanSeeInsurance()) {
				MenuComponent flagCriteria = configureMenu.addChild(getText("menu.FlagCriteria"));
				flagCriteria.addChild(getText("ManageFlagCriteriaOperator.title"), "ManageFlagCriteriaOperator.action", "manage_flag_criteria_operator");
				flagCriteria.addChild(getText("ManageInsuranceCriteriaOperator.title"), "ManageInsuranceCriteriaOperator.action", "manage_insurance_criteria_operator");
			} else {
				configureMenu.addChild(getText("ManageFlagCriteriaOperator.title"), "ManageFlagCriteriaOperator.action", "manage_flag_criteria_operator");
			}
		}

		if (permissions.has(OpPerms.ContractorSimulator))
			configureMenu.addChild("Contractor Simulator", "ContractorSimulator.action", "contractor_simulator");

		if (permissions.has(OpPerms.ManageCategoryRules, OpType.Edit))
			configureMenu.addChild("Category Rules", "CategoryRuleSearch.action", "category_rule_search");

		if (permissions.has(OpPerms.ManageAuditWorkFlow))
			configureMenu.addChild("Workflows", "ManageAuditWorkFlow.action", "manage_audit_work_flow");
	}

	private static void addDashboard(MenuComponent menubar) {
		menubar.addChild("Dashboard", "Home.action", "logo");
	}

	private static void addDevelopmentMenu(MenuComponent menubar, Permissions permissions) {
		if (!permissions.isDeveloperEnvironment())
			return;

		MenuComponent devMenu = menubar.addChild("Development");

		MenuComponent loggingSubmenu = devMenu.addChild("Logging");
		loggingSubmenu.addChild("System Logging", "LoggerConfig.action", "system_logging");
		loggingSubmenu.addChild("Page Logging", "PageLogger.action", "page_logging");

		MenuComponent cacheSubmenu = devMenu.addChild("Caching");
		cacheSubmenu.addChild("Clear Cache", "ClearCache.action", "clear_cache");
		cacheSubmenu.addChild("Cache Statistics", "CacheStatistics.action", "cache_stats");

		MenuComponent cronSubmenu = devMenu.addChild("Crons");
		cronSubmenu.addChild("Contractor Cron", "ContractorCron.action", "contractor_cron");
		cronSubmenu.addChild("Mail Cron", "MailCron.action", "mail_cron");
		cronSubmenu.addChild("Subscription Cron", "SubscriptionCron.action", "subscription_cron");

		devMenu.addChild("Server Information", "ServerInfo.action", "server_info");
		devMenu.addChild("App Properties", "ManageAppProperty.action", "manage_app_properties");
		devMenu.addChild("CSS Style Guide", "css.jsp", "css_style_guide");
		devMenu.addChild("Con/Op Flag Differences", "ContractorFlagDifference.action", "flag_differences");
		devMenu.addChild("Audit Schedule Builder", "AuditScheduleBuilderCron.action", "audit_schedule_builder");

		devMenu.addChild("Debug", "#", "debug-menu");
	}

	private static void addManageMenu(MenuComponent menubar, Permissions permissions) {
		MenuComponent manageMenu = menubar.addChild(getText("menu.Manage"));

		if (permissions.has(OpPerms.EditUsers))
			manageMenu.addChild(getText("menu.UserAccounts"), "UsersManage.action", "users_manage");

		if (permissions.has(OpPerms.UserZipcodeAssignment)) {
			manageMenu.addChild(getText("global.CSRAssignments"), "CSRAssignmentMatrix.action", "csr_assignment_matrix");
			manageMenu.addChild(getText("global.AuditorAssignments"), "AuditorAssignmentMatrix.action", "auditor_assignment_matrix");
		}

		if (permissions.has(OpPerms.OfficeAuditCalendar))
			manageMenu.addChild(getText("AuditCalendar.title"), "AuditCalendar.action", "audit_calendar");

		if (permissions.has(OpPerms.EditUsers))
			manageMenu.addChild(getText("ReportUserPermissionMatrix.title"), "ReportUserPermissionMatrix.action", "report_user_permission_matrix");

		if (permissions.has(OpPerms.FormsAndDocs))
			manageMenu.addChild(getText("Resources.title"), "Resources.action", "resources");

		if (permissions.has(OpPerms.EditAccountDetails)) {
			String url = "FacilitiesEdit.action?operator=" + permissions.getAccountId();
			manageMenu.addChild(getText("global.Facilities"), url, "facilities_edit");
		}

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR))
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();

			if (permissions.hasGroup(User.GROUP_MARKETING))
				custom = "?filter.accountManager=" + permissions.getUserId();

			manageMenu.addChild(getText("global.FlagChanges"), "ReportFlagChanges.action" + custom, "report_flag_changes");
		}

		addEmailSubmenu(manageMenu, permissions);

		// We're not translating webcam stuff because it's being phased out
		if (permissions.has(OpPerms.ManageWebcam)) {
			MenuComponent webcamMenu = manageMenu.addChild("Webcams");
			webcamMenu.addChild("Manage Webcams", "ManageWebcams.action?button=out", "manage_webcams");
			webcamMenu.addChild("Assign Webcams", "AssignWebcams.action", "assign_webcams");
		}
	}

	private static void addReportsMenu(MenuComponent menubar, List<ReportUser> favoriteReports) {
		MenuComponent reportsMenu = menubar.addChild(getText("menu.Reports"));

		reportsMenu.addChild(getText("menu.ManageReports"), ManageReports.MY_REPORTS_URL, "manage_reports");

		if (favoriteReports == null)
			return;

		for (ReportUser userReport : favoriteReports) {
			Report report = userReport.getReport();
			reportsMenu.addChild(report.getName(), "ReportDynamic.action?report=" + report.getId(), "report_" + report.getId());
		}
	}

	private static void addSupportMenu(MenuComponent menubar) {
		MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));
		String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&os_username=admin&os_password=ad9870mins";
		supportMenu.addChild(getText("Header.HelpCenter"), helpUrl, "help_center");
		supportMenu.addChild(getText("Registration.Error.LiveChat"), "#", "live_chat");
		supportMenu.addChild(getText("global.ContactPICS"), "Contact.action", "contact_action");
		supportMenu.addChild(getText("global.AboutPICS"), "#", "about_pics");
	}

	private static void addUserMenu(MenuComponent menu, Permissions permissions) {
		MenuComponent userMenu = menu.addChild(permissions.getName(), null, "user_menu");

		if (permissions.hasPermission(OpPerms.EditProfile))
			userMenu.addChild(getText("Account"), "ProfileEdit.action", "profile_edit");

		userMenu.addChild(getText("Header.Logout"), "Login.action?button=logout", "logout");
	}

	// TODO find out where these menus should go
	private static void addOrphanedMenus(MenuComponent menu, Permissions permissions) {
		// We're trying to get rid of this
		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR))
			menu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action", "create_auditor_invoices");

		if (permissions.has(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
			menu.addChild(getText("OperatorTags.title"), "OperatorTags.action", "operator_tags");

		// From dev menu
		menu.addChild("Exception Log", "ReportExceptions.action", "exception_log");
		menu.addChild("Batch Insert Translations", "BatchTranslations.action", "batch_insert_trans");

		// From user menu
		menu.addChild("Schedule", "MySchedule.action", "my_schedule");

		// From configure menu
		if (permissions.has(OpPerms.ManageTrades))
			menu.addChild(getText("TradeTaxonomy.title"), "TradeTaxonomy.action", "trade_taxonomy");
	}

	// TODO this menu is very different from the one in PicsMenu, starting on line ~146
	private static MenuComponent addContractorSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return menu;

		MenuComponent contractorMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.has(OpPerms.ContractorWatch))
			contractorMenu.addChild(getText("ReportActivityWatch.title"), "ReportActivityWatch.action", "report_activity_watch");

		if (permissions.has(OpPerms.WatchListManager))
			contractorMenu.addChild(getText("WatchListManager.title"), "WatchListManager.action", "watch_list_manager");

		return contractorMenu;
	}

	private static void addEmailSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		MenuComponent emailMenu = menu.addChild(getText("global.Email"));

		if (permissions.seesAllContractors())
			emailMenu.addChild(getText("ProfileEdit.label.EmailSubscriptions"), "ReportEmailSubscription.action", "report_email_subscription");

		if (permissions.has(OpPerms.EmailTemplates, OpType.Edit)) {
			emailMenu.addChild(getText("EditEmailTemplate.title"), "EditEmailTemplate.action", "edit_email_template");

			if (permissions.isPicsEmployee()) {
				emailMenu.addChild("Email Exclusions", "EditEmailExclusions.action", "edit_email_exclusions");
			}
		}
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

	/* I'm leaving this here to more easily see what menus existed before

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
			//managementMenu.addChild(getText("ContractorApproval.title"), "ContractorApproval.action?filter.workStatus=P");
			if (permissions.isGeneralContractor()) {
				//managementMenu.addChild(getText("ReportSubcontractors.title"), "ReportSubcontractors.action", "ReportSubcontractors");
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
			managementMenu.addChild(getText("ManageEmployees.title"), "ManageEmployees.action?id=" + permissions.getAccountId());

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
			//managementMenu.addChild(getText("EmailQueueList.title"), "EmailQueueList.action?filter.status=Pending");
			if (permissions.isPicsEmployee()) {
				//managementMenu.addChild("Email Error Report", "ReportEmailError.action");
			}
		}

		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety)) {
			//managementMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action");
		}
		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			managementMenu.addChild(getText("Resources.title"), "Resources.action");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			//managementMenu.addChild("Sales Report", "ReportSalesReps.action");
		}

		if (permissions.hasPermission(OpPerms.EditProfile)) {
			addChildAction(managementMenu, "ProfileEdit");
		}

		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			managementMenu.addChild(getText("FacilitiesEdit.title"), "FacilitiesEdit.action?operator=" + permissions.getAccountId());

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
			//contractorMenu.addChild(getText("ContractorList.title"), "ContractorList.action");
		}

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			//contractorMenu.addChild(getText("NewContractorSearch.title"), url);
		}
		if (permissions.hasPermission(OpPerms.RequestNewContractor)) {
			//addChildAction(contractorMenu, "ReportNewRequestedContractor");
		}
		if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
			String url = "BiddingContractorSearch.action";
			if (!permissions.getAccountStatus().isDemo())
				url += "?filter.status=Active";
			//contractorMenu.addChild(getText("BiddingContractorSearch.title"), url);
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
				hseCompetencyMenu.addChild(getText("DefineCompetencies.title"), "DefineCompetencies.action");

			//hseCompetencyMenu.addChild(getText("ReportCompetencyByAccount.title"), "ReportCompetencyByAccount.action");
			//hseCompetencyMenu.addChild(getText("ReportCompetencyByEmployee.title"), "ReportCompetencyByEmployee.action");
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
			//insureGuardMenu.addChild(getText("PolicyVerification.title"), url);
		}

		if (permissions.hasPermission(OpPerms.InsuranceApproval)) {
			String url = "ReportInsuranceApproval.action?filter.auditStatus=Complete";
			//insureGuardMenu.addChild(getText("ReportInsuranceApproval.title"), url);
		}
	}

	private static void oldOperatorQualificationSubmenu(MenuComponent menu, Permissions permissions) {
		if (permissions.isRequiresOQ()) {
			MenuComponent operatorQualMenu = menu.addChild(getText("global.OperatorQualification"));

			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				operatorQualMenu.addChild(getText("ManageJobTasksOperator.title"), "ManageJobTasksOperator.action");

			if (permissions.hasPermission(OpPerms.ManageProjects))
				operatorQualMenu.addChild(getText("ManageProjects.title"), "ManageProjects.action");

			//operatorQualMenu.addChild(getText("ReportOQ.title"), "ReportOQ.action");
			//operatorQualMenu.addChild(getText("ReportOQEmployees.title"), "ReportOQEmployees.action");
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
			//reportsMenu.addChild(getText("ReportCsrActivity.title"), "ReportCsrActivity.action");
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
			//reportsMenu.addChild(getText("ReportContractorScore.title.title"), "ReportContractorScore.action");
		}

		if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
			//reportsMenu.addChild(getText("GraphEmrRates.title"), "GraphEmrRates.action?years=2010");
			//reportsMenu.addChild(getText("ReportEmrRates.title"), "ReportEmrRates.action?filter.auditFor=2010");
			if (permissions.isAuditor()) {
				//reportsMenu.addChild("Auditor Emr Rates Report", "ReportAuditorEmrRates.action", "ReportAuditorEmrRates");
			}
		}

		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportFatalities.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			//reportsMenu.addChild(getText("ReportFatalities.title"), url);
		}

		if (permissions.hasPermission(OpPerms.ForcedFlagsReport)) {
			//addChildAction(reportsMenu, "ReportContractorsWithForcedFlags");
		}

		if (permissions.hasPermission(OpPerms.TRIRReport)) {
			//addChildAction(reportsMenu, "GraphTrirRates");
			final String url = "ReportIncidenceRate.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			//reportsMenu.addChild(getText("ReportIncidenceRate.title"), url);
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
			//reportsMenu.addChild(getText("ReportAssessmentTests.title"), "ReportAssessmentTests.action");
		}
		if (permissions.seesAllContractors()) {
			//reportsMenu.addChild("Report WCB Accounts", "ReportWcbAccounts.action");
		}
	}

	private static void oldImportPqfSubmenu(MenuComponent menu, Permissions permissions) {
		if (menu == null || permissions == null)
			return;

		if (permissions.hasPermission(OpPerms.ImportPQF)) {
			//MenuComponent importPqfMenu = menu.addChild(getText("ReportImportPQFs.title"));
			//importPqfMenu.addChild(getText("ReportImportPQFs.title"), "ReportImportPQFs.action");
		}
	}
	*/
}
