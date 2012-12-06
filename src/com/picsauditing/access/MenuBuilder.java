package com.picsauditing.access;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public final class MenuBuilder {

    private static I18nCache i18nCache = I18nCache.getInstance();

    private static final String SEARCH_FOR_NEW_URL = "NewContractorSearch.action?filter.performedBy=Self%20Performed&filter.primaryInformation=true&filter.tradeInformation=true";

    private static final Logger logger = LoggerFactory.getLogger(MenuBuilder.class);

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
        if (!permissions.getAccountStatus().isActiveDemo()) {
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

            if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
                editMenu.addChild(getText("ManageEmployees.title"), "ManageEmployees.action", "manage_employees");

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

        // TODO ask matt about this
        // MenuComponent editMenu = menubar.addChild("Edit");
        // editMenu.addChild("Account", "AssessmentCenterEdit.action",
        // "assessment_center_edit");
        // editMenu.addChild("Users", "UsersManage.action", "users_manage");

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
                translationMenu.addChild(getText("menu.Configure.UnsyncedTranslations"), "UnsyncedTranslations.action",
                        "unsynced_translations");
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
        if (!permissions.isDeveloperEnvironment())
            return;

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

        devMenu.addChild("Report Tester", "ReportTester.action", "report_tester");
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

        if (permissions.has(OpPerms.EditUsers)) {
            manageMenu.addChild(getText("menu.UserAccounts"), "UsersManage.action", "users_manage");
        }

        if (permissions.has(OpPerms.UserZipcodeAssignment)) {
            manageMenu
                    .addChild(getText("global.CSRAssignments"), "CSRAssignmentMatrix.action", "csr_assignment_matrix");
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

        if (permissions.has(OpPerms.ContractorWatch))
            manageMenu.addChild(getText("ReportActivityWatch.title"), "ReportActivityWatch.action",
                    "report_activity_watch");

        if (permissions.has(OpPerms.WatchListManager))
            manageMenu.addChild(getText("WatchList.title"), "WatchListManager.action", "watch_list");

        if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate())
            manageMenu.addChild(getText("OperatorTags.title"), "OperatorTags.action", "OperatorTags");

        if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
            manageMenu.addChild(getText("FlagCriteriaOperator.title"), "ManageFlagCriteriaOperator.action",
                    "FlagCriteriaOperator");
            if (permissions.isCanSeeInsurance())
                manageMenu.addChild(getText("InsuranceCriteriaOperator.title"), "ManageInsuranceCriteriaOperator.action",
                        "ManageInsuranceCriteriaOperator");
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
    }

    private static void addReportsMenu(MenuComponent menubar, List<ReportUser> favoriteReports, Permissions permissions) {
        MenuComponent reportsMenu = menubar.addChild(getText("menu.Reports"));

        reportsMenu.addChild(getText("menu.ManageReports"), ManageReports.LANDING_URL, "manage_reports");

        {
            MenuComponent legacyMenu = reportsMenu.addChild("Legacy Reports");
            // CONTRACTORS
            if (permissions.hasPermission(OpPerms.RequestNewContractor))
                legacyMenu.addChild(getText("ReportNewRequestedContractor.title"),
                        "ReportNewRequestedContractor.action", "ReportNewRequestedContractor");
            if (permissions.hasPermission(OpPerms.SearchContractors)) {
                legacyMenu.addChild(getText("NewContractorSearch.title"), SEARCH_FOR_NEW_URL, "NewContractorSearch");
            }
            if (permissions.hasPermission(OpPerms.ViewTrialAccounts)) {
                String statusFilter = "";
                if (!permissions.getAccountStatus().isDemo())
                    statusFilter = "?filter.status=Active";
                legacyMenu.addChild(getText("BiddingContractorSearch.title"), "BiddingContractorSearch.action"
                        + statusFilter, "BiddingContractorSearch");
            }
            if (permissions.hasPermission(OpPerms.DelinquentAccounts)) {
                legacyMenu.addChild(getText("ArchivedContractorAccounts.title"), "ArchivedContractorAccounts.action",
                        "ArchivedContractorAccounts");
                legacyMenu.addChild(getText("DelinquentContractorAccounts.title"),
                        "DelinquentContractorAccounts.action", "DelinquentContractorAccounts");
            }
            if (permissions.hasPermission(OpPerms.ContractorApproval))
                legacyMenu.addChild(getText("ContractorApproval.title"),
                        "ContractorApproval.action?filter.workStatus=P", "subMenu_ApproveContractors");

            // FLAGS
            if (permissions.isCorporate())
                legacyMenu.addChild(getText("ReportContractorOperatorFlag.title"),
                        "ReportContractorOperatorFlag.action", "ReportContractorOperatorFlag");
            if ((permissions.isCorporate() || permissions.getCorporateParent().size() > 0) && !permissions.isSecurity())
                legacyMenu.addChild(getText("ReportContractorOperatorFlagMatrix.title"),
                        "ReportContractorOperatorFlagMatrix.action", "ReportContractorOperatorFlagMatrix");
            if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.OperatorFlagMatrix))
                legacyMenu.addChild(getText("OperatorFlagMatrix.title"), "OperatorFlagMatrix.action",
                        "OperatorFlagMatrix");

            if (permissions.hasPermission(OpPerms.InsuranceApproval))
                legacyMenu.addChild(getText("ReportInsuranceApproval.title"),
                        "ReportInsuranceApproval.action?filter.auditStatus=Complete", "RepInsApproval");

            // OTHER
            if (permissions.hasPermission(OpPerms.ContractorDetails))
                legacyMenu.addChild(getText("QuestionAnswerSearch.title"), "QuestionAnswerSearch.action",
                        "QuestionAnswerSearch");
            if (permissions.hasPermission(OpPerms.EmailQueue))
                legacyMenu.addChild(getText("EmailQueueList.title"), "EmailQueueList.action?filter.status=Pending",
                        "EmailQueue");

            if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
                legacyMenu.addChild(getText("GraphEmrRates.title"), "GraphEmrRates.action?years=2010", "GraphEmrRates");
                legacyMenu.addChild(getText("ReportEmrRates.title"), "ReportEmrRates.action?filter.auditFor=2010",
                        "ReportEmrRates");
            }
            if (permissions.hasPermission(OpPerms.TRIRReport)) {
                legacyMenu.addChild(getText("GraphTrirRates.title"), "GraphTrirRates.action", "GraphTrirRates");
                legacyMenu.addChild(getText("ReportIncidenceRate.title"),
                        "ReportIncidenceRate.action?filter.shaType=OSHA&filter.shaLocation=Corporate",
                        "ReportIncidenceRate");
            }
            if (permissions.hasPermission(OpPerms.FatalitiesReport))
                legacyMenu
                        .addChild(
                                getText("ReportFatalities.title"),
                                "ReportFatalities.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate",
                                "ReportFatalities");
        }

        if (CollectionUtils.isNotEmpty(favoriteReports)) {
            reportsMenu.addChild("separator", null);
            MenuComponent favoriteLabel = new MenuComponent("Favorites", null);
            favoriteLabel.setCssClass("label");
            reportsMenu.addChild(favoriteLabel);
        }

        for (ReportUser reportUser : favoriteReports) {
            Report report = reportUser.getReport();
            reportsMenu
                    .addChild(report.getName(), "Report.action?report=" + report.getId(), "report_" + report.getId());
        }
    }

    private static void addSupportMenu(MenuComponent menubar, Permissions permissions) {
        MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));
        String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&os_username=admin&os_password=ad9870mins";
        supportMenu.addChild(getText("Header.HelpCenter"), helpUrl, "help_center");
        supportMenu.addChild(getText("Registration.Error.LiveChat"), "#", "live_chat");
        supportMenu.addChild(getText("global.ContactPICS"), "Contact.action", "contact_action");
        supportMenu.addChild(getText("global.AboutPICS"), "About.action", "about_pics");

        MenuComponent referenceMenu = supportMenu.addChild("Reference");
        if (permissions.hasPermission(OpPerms.ManageTrades))
            referenceMenu.addChild(getText("TradeTaxonomy.title"), "TradeTaxonomy.action", "TradeTaxonomy");

        referenceMenu.addChild("Navigation Menu", "Tutorial!navigationMenu.action", "navigation_menu");
    }

    private static void addUserMenu(MenuComponent menu, Permissions permissions) {
        MenuComponent userMenu = menu.addChild(permissions.getName(), null, "user_menu");

        if (permissions.hasPermission(OpPerms.EditProfile)) {
            userMenu.addChild(getText("Account"), "ProfileEdit.action", "profile_edit");
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

        if (permissions.hasPermission(OpPerms.EmailTemplates))
            emailMenu.addChild(getText("EmailWizard.title"), "EmailWizard.action", "EmailWizard");
        if (permissions.has(OpPerms.EmailTemplates, OpType.Edit)) {
            emailMenu.addChild(getText("EditEmailTemplate.title"), "EditEmailTemplate.action", "edit_email_template");

            if (permissions.isPicsEmployee()) {
                emailMenu.addChild(getText("menu.Email.EmailExclusions"), "EditEmailExclusions.action",
                        "edit_email_exclusions");
            }
        }
    }

    private static void handleSingleChildMenu(MenuComponent menu) {
        if (menu.getChildren().size() == 1) {
            menu = menu.getChildren().get(0);
        }
    }

    public static String getHomePage(MenuComponent menu, Permissions permissions) {
        if (permissions.isContractor())
            return "ContractorView.action";

        if (permissions.has(OpPerms.Dashboard))
            return "Home.action";

        if (menu == null || menu.getChildren() == null || menu.getChildren().isEmpty())
            return "Home.action";

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

    // TODO find out where these menus should go
    private static void orphanedMenus(MenuComponent menu, Permissions permissions) {
        // We're trying to get rid of this
        if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR))
            menu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action", "create_auditor_invoices");

        // From dev menu
        menu.addChild("Exception Log", "ReportExceptions.action", "exception_log");
        menu.addChild("Batch Insert Translations", "BatchTranslations.action", "batch_insert_trans");

        // From user menu
        menu.addChild("Schedule", "MySchedule.action", "my_schedule");
    }
}
