package com.picsauditing.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
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
				subMenu = menu.addChild(getText("global.Home"), "ContractorView.action", "ContractorView");

				// Don't show for insurance only users
				if (!permissions.isInsuranceOnlyContractorUser()) {
					subMenu = menu.addChild(getText("global.Company"));
					subMenu.addChild(getText("menu.Contractor.WhereWeWork"), "ContractorFacilities.action",
							"ContractorFacilities");
					subMenu.addChild(getText("menu.Contractor.ActivityLog"), "ContractorNotes.action",
							"ContractorNotes");
				}

				if (permissions.hasPermission(OpPerms.ContractorAdmin)) {
					subMenu = menu.addChild(getText("button.Edit"));
					subMenu.addChild(getText("menu.Contractor.CompanyAccount"), "ContractorEdit.action",
							"ContractorEdit");
					subMenu.addChild(getText("global.Users"), "UsersManage.action", "UsersManage");
					subMenu.addChild(getTitle("ContractorTrades"), "ContractorTrades.action", "ContractorTrades");
					if (permissions.isRequiresOQ() || permissions.isRequiresCompetencyReview())
						subMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action", "ManageEmployees");
					if (permissions.isRequiresCompetencyReview()) {
						subMenu = menu.addChild(getText("global.HSECompetencies"));
						subMenu.addChild(getTitle("ManageJobRoles"), "ManageJobRoles.action", "ManageJobRoles");
						subMenu.addChild(getTitle("EmployeeCompetencies"), "EmployeeCompetencies.action",
								"EmployeeCompetencies");
					}
					if (permissions.isRequiresOQ()) {
						subMenu = menu.addChild(getText("global.OperatorQualification"));
						subMenu.addChild(getTitle("ReportOQEmployees"),
								"ReportOQEmployees.action?orderBy=e.lastName,e.firstName", "ReportQQEmployees");
						subMenu.addChild(getTitle("ReportOQChanges"), "ReportOQChanges.action", "ReportQQChanges");
						subMenu.addChild(getTitle("ReportNewProjects"), "ReportNewProjects.action", "ReportNewProjects");
					}
				}

				if (permissions.hasPermission(OpPerms.ContractorBilling)) {
					subMenu = menu.addChild(getText("menu.Billing"));
					subMenu.addChild(getText("menu.Contractor.BillingDetails"), "BillingDetail.action", "BillingDetail");
					subMenu.addChild(getText("menu.Contractor.PaymentOptions"), "ContractorPaymentOptions.action",
							"ContractorPaymentOptions");
				}

				menu.addChild(getText("global.Resources"), "ContractorForms.action", "ContractorForms");

				subMenu = addSupportLink(menu);
				addChildAction(subMenu, "ProfileEdit");
			} else {
				subMenu = menu.addChild(getText("Registration.CompanyDetails.heading"), "ContractorEdit.action",
						"ContractorEdit");
				addSupportLink(menu);
			}
			return menu;
		}

		if (permissions.isAssessment()) {
			subMenu = menu.addChild("Management");
			subMenu.addChild("Imported Data", "ManageImportData.action", "ManageImportData");
			subMenu.addChild("Assessment Tests", "ManageAssessmentTests.action", "ManageAssessmentTests");
			subMenu.addChild("Test Mapping", "ManageUnmappedTests.action", "ManageUnmappedTests");
			subMenu.addChild("Assessment Results", "ManageAssessmentResults.action", "ManageAssessmentResults");
			subMenu.addChild("Companies", "ManageMappedCompanies.action", "ManageMappedCompanies");
			subMenu.addChild("Company Mapping", "ManageUnmappedCompanies.action", "ManageUnmappedCompanies");

			subMenu = menu.addChild("Edit");
			subMenu.addChild("Account", "AssessmentCenterEdit.action", "AssessmentCenterEdit");
			subMenu.addChild("Users", "UsersManage.action", "UsersManage");

			addSupportLink(menu);
			return menu;
		}

		if (permissions.hasPermission(OpPerms.ImportPQF)) {
			subMenu = menu.addChild(getTitle("ReportImportPQFs"));
			subMenu.addChild(getTitle("ReportImportPQFs"), "ReportImportPQFs.action", "ReportImportPQFs");
		}

		subMenu = menu.addChild(getText("global.Contractors"));
		if (permissions.hasPermission(OpPerms.AllContractors) || permissions.isOperatorCorporate()) {
			subMenu.addChild(getTitle("ContractorList"), "ContractorList.action", "ContractorList");
		}

		if (permissions.isOperatorCorporate() && permissions.getLinkedGeneralContractors().size() > 0) {
			subMenu.addChild(getTitle("GeneralContractorList"), "GeneralContractorsList.action",
					"GeneralContractorsList");
			subMenu.addChild(getTitle("SubcontractorFlagMatrix"), "SubcontractorFlagMatrix.action",
					"SubcontractorFlagMatrix");
		}

		if (permissions.isGeneralContractor()) {
			subMenu.addChild(getTitle("SubcontractorFlagMatrix"), "SubcontractorFlagMatrix.action");
		}

		if (permissions.hasPermission(OpPerms.SearchContractors)) {
			final String url = "NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true&filter.tradeInformation=true";
			subMenu.addChild(getTitle("NewContractorSearch"), url, "NewContractorSearch");
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
			subMenu.addChild("My Audits", "AuditListAuditor.action?filter.auditStatus=Pending", "MyAudits");
			subMenu.addChild("My Audit History", "MyAuditHistory.action", "MyAuditHistory");
		}

		if (permissions.hasPermission(OpPerms.AuditorPayments) || permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			subMenu.addChild("Safety Pro Invoices", "AuditorInvoices.action", "AuditorInvoices");
			subMenu.addChild("Create Safety Pro Invoices", "CreateAuditorInvoices.action", "CreateAuditorInvoices");
		}

		if (permissions.isAdmin())
			subMenu.addChild("Audit List Compress", "ReportAuditList.action", "ReportAuditList");
		if (permissions.hasPermission(OpPerms.ContractorDetails))
			addChildAction(subMenu, "ReportCAOList");
		if (permissions.hasPermission(OpPerms.ContractorDetails) && !permissions.isOperatorCorporate())
			addChildAction(subMenu, "ReportCAOByStatusList");

		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Sched. &amp; Assign",
					"AuditAssignments.action?filter.status=Active&filter.auditTypeID=2&filter.auditTypeID=17",
					"AuditAssignments");
		if (permissions.isAdmin())
			subMenu.addChild("Cancelled Sched. Audits", "CancelledScheduledAudits.action", "CancShedAudits");
		if (permissions.hasPermission(OpPerms.AssignAudits))
			subMenu.addChild("Close Assigned Audits",
					"ReportCloseAuditAssignments.action?filter.auditStatus=Submitted", "CloseAssignedAudits");
		if (permissions.hasPermission(OpPerms.OfficeAuditCalendar))
			addChildAction(subMenu, "AuditCalendar");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Answer Updates", "AuditDataUpdates.action", "AuditDataUpdates");
		if (permissions.isAuditor()) {
			subMenu.addChild("Close Open Reqs", "ReportOpenRequirements.action", "CloseOpenReqs");
		}
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			subMenu.addChild("Auditor Assignment", "AuditorAssignmentMatrix.action", "AuditorAssignment");

		subMenu = menu.addChild("Customer Service");
		if (permissions.isAdmin()) {
			subMenu.addChild("Assign Contractors", "ContractorAssigned.action", "AssignContractos");
		}

		if (permissions.hasPermission(OpPerms.ManageWebcam)) {
			subMenu.addChild("Manage Webcams", "ManageWebcams.action?button=out", "ManageWebcams");
			subMenu.addChild("Assign Webcams", "AssignWebcams.action", "AssignWebcams");
		}
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild("Pending PQF", "ReportCompletePQF.action?filter.auditStatus=Pending", "ReportCompletePQF");
		if (permissions.hasPermission(OpPerms.AuditVerification))
			subMenu.addChild(
					"PQF Verification",
					"PqfVerification.action?filter.status=Active"
							+ (permissions.hasGroup(User.GROUP_CSR) ? "&filter.conAuditorId="
									+ permissions.getShadowedUserID() : ""), "PQFVerification");
		if (permissions.hasPermission(OpPerms.UserZipcodeAssignment))
			subMenu.addChild("CSR Assignment", "CSRAssignmentMatrix.action", "CSRAssignment");

		subMenu = menu.addChild("Accounting");
		if (permissions.hasPermission(OpPerms.Billing)) {
			subMenu.addChild("Billing Report", "ReportBilling.action?filter.status=Active&filter.status=Pending",
					"BillingReport");
			subMenu.addChild("Unpaid Invoices Report", "ReportUnpaidInvoices.action", "UnpaidInvoices");
			subMenu.addChild("Invoice Search Report", "ReportContractorUnpaidInvoices.action", "InvoiceSearch");
			subMenu.addChild("Expired CC Report", "ReportExpiredCreditCards.action?filter.status=Active", "ExpiredCCs");
			subMenu.addChild("Lifetime Members Report", "ReportLifetimeMembership.action", "LifetimeMembers");
			subMenu.addChild("QuickBooks Sync US", "QBSyncList.action?currency=USD", "QuickBooksSync_USD");
			subMenu.addChild("QuickBooks Sync Canada", "QBSyncList.action?currency=CAD", "QuickBooksSync_CAD");
			subMenu.addChild("QuickBooks Sync UK", "QBSyncList.action?currency=GBP", "QuickBooksSync_GBP");
			subMenu.addChild("QuickBooks Sync EUR", "QBSyncList.action?currency=EUR", "QuickBooksSync_EUR");
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
									+ permissions.getShadowedUserID() : ""), "PolicyVerification");
		if (permissions.hasPermission(OpPerms.InsuranceApproval))
			subMenu.addChild(getTitle("ReportInsuranceApproval"),
					"ReportInsuranceApproval.action?filter.auditStatus=Complete", "RepInsApproval");

		// Management
		subMenu = menu.addChild(getText("menu.Management"));
		if (permissions.hasPermission(OpPerms.ManageCorporate) || permissions.hasPermission(OpPerms.ManageOperators)
				|| permissions.hasPermission(OpPerms.ManageAssessment))
			subMenu.addChild("Manage Accounts",
					"ReportAccountList.action?filter.status=Active&filter.status=Demo&filter.status=Pending",
					"ManageAccounts");
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
			subMenu.addChild(getTitle("ManageEmployees"), "ManageEmployees.action?id=" + permissions.getAccountId(),
					"ManageEmployees");

		if (permissions.seesAllContractors()) {
			subMenu.addChild("Email Subscriptions", "ReportEmailSubscription.action", "EmailSubscriptions");
		}

		if (permissions.hasPermission(OpPerms.EmailTemplates)) {
			addChildAction(subMenu, "EmailWizard");
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates) && permissions.isPicsEmployee()) {
			addChildAction(subMenu, "ReportEmailWebinar");
		}
		if (permissions.hasPermission(OpPerms.EmailQueue)) {
			subMenu.addChild(getTitle("EmailQueueList"), "EmailQueueList.action?filter.status=Pending", "EmailQueue");
			if (permissions.isPicsEmployee())
				subMenu.addChild("Email Error Report", "ReportEmailError.action", "EmailErrorReport");
		}
		if (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorSafety))
			subMenu.addChild("Job Competency Matrix", "JobCompetencyMatrix.action", "JobCompetencyMatrix");

		if (permissions.hasPermission(OpPerms.FormsAndDocs))
			subMenu.addChild(getTitle("Resources"), "Resources.action", "Resources");

		if (permissions.hasPermission(OpPerms.UserRolePicsOperator)) {
			subMenu.addChild("Sales Report", "ReportSalesReps.action", "SalesReport");
		}
		if (permissions.hasPermission(OpPerms.EditProfile)) {
			addChildAction(subMenu, "ProfileEdit");
		}
		if (permissions.hasPermission(OpPerms.EditAccountDetails))
			subMenu.addChild(getTitle("FacilitiesEdit"),
					"FacilitiesEdit.action?operator=" + permissions.getAccountId(), "FacilitiesEdit");

		if (permissions.hasPermission(OpPerms.MyCalendar)) {
			subMenu.addChild("My Schedule", "MySchedule.action", "MySchedule");
		}

		if (permissions.hasPermission(OpPerms.ClientSiteReferrals)) {
			subMenu.addChild("Client Site Referrals", "ReportClientSiteReferrals.action", "ClientSiteReferrals");
		}

		if (permissions.hasPermission(OpPerms.Debug)) {
			MenuComponent debug = subMenu.addChild("Debug", "#");
			debug.setHtmlId("debug-menu");
		}

		// Configuration
		subMenu = menu.addChild(getText("menu.Configuration"));
		// List<MenuComponent> menuItems = new ArrayList<MenuComponent>();
		HashMap<String, String[]> menuItems = new HashMap<String, String[]>();

		if (permissions.hasPermission(OpPerms.Translator)) {
			menuItems.put("Manage Translations", new String[] { "ManageTranslations.action", "ManageTranslations" });

			if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
				menuItems.put("Import/Export Translations", new String[] { "TranslationETL.action", "ImExTrans" });
				menuItems.put("Unsynced Translations", new String[] { "UnsyncedTranslations.action", "UnsyncdTrans" });
			}
		}
		if (permissions.hasPermission(OpPerms.ManageTrades)) {
			menuItems.put(getTitle("TradeTaxonomy"), new String[] { "TradeTaxonomy.action", "TradeTaxonomy" });
		}
		if (permissions.hasPermission(OpPerms.ManageAudits)) {
			menuItems.put("Audit Definition", new String[] { "ManageAuditType.action", "AuditDefinition" });
			menuItems.put("Manage Audit Options", new String[] { "ManageOptionGroup.action", "ManageAuditOptions" });
			menuItems.put("Flag Criteria", new String[] { "ManageFlagCriteria.action", "FlagCriteria" });
		}
		if (permissions.hasPermission(OpPerms.ContractorSimulator))
			menuItems.put("Contractor Simulator", new String[] { "ContractorSimulator.action", "ContractorSimulator" });
		if (permissions.hasPermission(OpPerms.ManageAuditTypeRules, OpType.Edit)) {
			menuItems.put("Audit Type Rules", new String[] { "AuditTypeRuleSearch.action", "AuditTypeRules" });
		}
		if (permissions.hasPermission(OpPerms.ManageCategoryRules, OpType.Edit)) {
			menuItems.put("Category Rules", new String[] { "CategoryRuleSearch.action", "CatRules" });
		}
		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)) {
			menuItems.put(getTitle("AuditCategoryMatrix"), new String[] { "AuditCategoryMatrix.action",
					"AuditCatMatrix" });
		}
		if (permissions.hasPermission(OpPerms.ManageAuditWorkFlow))
			menuItems.put("Manage Workflow", new String[] { "ManageAuditWorkFlow.action", "ManageWorkflow" });
		if (permissions.hasPermission(OpPerms.EditFlagCriteria) && permissions.isOperatorCorporate()) {
			menuItems.put(getTitle("ManageFlagCriteriaOperator"), new String[] { "ManageFlagCriteriaOperator.action",
					"ManageFlagCriteriaOperator" });
			if (permissions.isCanSeeInsurance())
				menuItems.put(getTitle("ManageInsuranceCriteriaOperator"), new String[] {
						"ManageInsuranceCriteriaOperator.action", "ManageInsuranceCriteriaOperator" });
		}
		if (permissions.hasPermission(OpPerms.EmailTemplates, OpType.Edit)) {
			menuItems.put(getTitle("EditEmailTemplate"),
					new String[] { "EditEmailTemplate.action", "EditEmailTemplate" });
			if (permissions.isPicsEmployee())
				menuItems
						.put("Email Exclusions List", new String[] { "EditEmailExclusions.action", "EmailExclusions" });
		}

		// add to menu in sorted order
		ArrayList<String> keys = new ArrayList<String>(menuItems.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			subMenu.addChild(key, menuItems.get(key)[0], menuItems.get(key)[1]);
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
			subMenu.addChild("System Logging", "LoggerConfig.action", "SystemLogging");
			subMenu.addChild("Page Logging", "PageLogger.action", "PageLogging");
			subMenu.addChild("Clear Cache", "ClearCache.action", "ClearCache");
			subMenu.addChild("Cache Statistics", "CacheStatistics.action", "ClearStats");
			subMenu.addChild("Contractor Cron", "ContractorCron.action", "ContractorCron");
			subMenu.addChild("Con/Op Flag Differences", "ContractorFlagDifference.action", "FlagDifferences");
			subMenu.addChild("Mail Cron", "MailCron.action", "MailCron");
			subMenu.addChild("Subscription Cron", "SubscriptionCron.action", "SubscriptionCron");
			subMenu.addChild("Server Information", "ServerInfo.action", "ServerInfo");
			subMenu.addChild("Audit Schedule Builder", "AuditScheduleBuilderCron.action", "AuditScheduleBuilder");
			subMenu.addChild("Huntsman Sync", "ContractorCron.action", "HuntsmanSync");
			subMenu.addChild("CSS Style Guide", "css.jsp", "CSSStyleGuide");
			subMenu.addChild("Manage App Properties", "ManageAppProperty.action", "ManageAppProperties");
			subMenu.addChild("Exception Log", "ReportExceptions.action", "ExceptionLog");
			subMenu.addChild("Batch Insert Translations", "BatchTranslations.action", "BatchInsertTrans");
			subMenu.addChild("Dynamic Reporting", "ReportDynamic.action?report=1", "DynamicReporting");
		}

		subMenu = menu.addChild(getText("menu.Reports"));

		// TODO - remove these hacks
		if (permissions.getAccountId() == 6228) {
			subMenu.addChild("Site Orientation Report", "report_orientation.jsp", "SiteOrientationReport");
		}

		if (permissions.hasPermission(OpPerms.ManageAudits))
			subMenu.addChild("Audit Analysis", "ReportAuditAnalysis.action", "AuditAnalysis");
		if (permissions.hasGroup(User.GROUP_CSR) || permissions.hasGroup(User.GROUP_MANAGER)) {
			subMenu.addChild(getTitle("ReportCsrActivity"), "ReportCsrActivity.action", "ReportCSRActivity");
			subMenu.addChild("CSR Contractor Count", "ReportCsrContractorCount.action", "CSRContractorCount");
			subMenu.addChild("CSR Policies Status Count", "ReportCsrPoliciesStatusCount.action",
					"CSRPoliciesStatusCount");
		}

		if (permissions.hasPermission(OpPerms.ManageCategoryRules)
				|| permissions.hasPermission(OpPerms.ManageAuditTypeRules))
			subMenu.addChild("Audit Rule History", "ReportRuleHistory.action", "AuditRuleHistory");
		if (permissions.hasPermission(OpPerms.ContractorLicenseReport))
			subMenu.addChild("Contractor Licenses", "ReportContractorLicenses.action", "ContractorLicenses");
		if (permissions.hasPermission(OpPerms.RiskRank))
			subMenu.addChild("Contractor Risk Level", "ReportContractorRiskLevel.action", "ContractorRiskLevel");
		if (permissions.isAdmin()
				|| (permissions.isOperatorCorporate() && permissions.getCorporateParent().contains(10566)))
			subMenu.addChild(getTitle("ReportContractorScore.title"), "ReportContractorScore.action",
					"ReportContractorScore");
		if (permissions.hasPermission(OpPerms.EMRReport) && "US".equals(permissions.getCountry())) {
			subMenu.addChild(getTitle("GraphEmrRates"), "GraphEmrRates.action?years=2010", "GraphEmrRates");
			subMenu.addChild(getTitle("ReportEmrRates"), "ReportEmrRates.action?filter.auditFor=2010", "ReportEmrRates");
			if (permissions.isAuditor()) {
				subMenu.addChild("Auditor Emr Rates Report", "ReportAuditorEmrRates.action", "ReportAuditorEmrRates");
			}
		}
		if (permissions.hasPermission(OpPerms.FatalitiesReport)) {
			final String url = "ReportFatalities.action?filter.auditFor=2010&filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild(getTitle("ReportFatalities"), url, "ReportFatalities");
		}
		if (permissions.hasPermission(OpPerms.ForcedFlagsReport))
			addChildAction(subMenu, "ReportContractorsWithForcedFlags");

		if (permissions.hasPermission(OpPerms.TRIRReport)) {
			addChildAction(subMenu, "GraphTrirRates");
			final String url = "ReportIncidenceRate.action?filter.shaType=OSHA&filter.shaLocation=Corporate";
			subMenu.addChild(getTitle("ReportIncidenceRate"), url, "ReportIncidenceRate");
		}

		if (permissions.getAccountName().startsWith("Tesoro"))
			subMenu.addChild("Background Check", "QuestionAnswerSearchByAudit.action", "BackgroundCheck");

		// Hide this menu if the operator doesn't have any required tags.
		OperatorAccountDAO operatorAccountDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		if (permissions.hasPermission(OpPerms.ContractorTags) && permissions.isOperatorCorporate()
				&& !Strings.isEmpty(operatorAccountDAO.find(permissions.getAccountId()).getRequiredTags()))
			addChildAction(subMenu, "ReportUntaggedContractors");
		if (permissions.hasPermission(OpPerms.ManageTrades) && permissions.isAdmin())
			subMenu.addChild("Contractor Trade Conflicts", "ReportContractorTradeConflict.action",
					"ContractorTradeConflicts");

		if (permissions.seesAllContractors())
			subMenu.addChild("User Multi-Login", "MultiLoginUser.action", "MultiLogin");
		if (permissions.hasPermission(OpPerms.EditUsers))
			addChildAction(subMenu, "UserList");
		if (permissions.getAccountId() == 1813 || permissions.hasPermission(OpPerms.DevelopmentEnvironment)
				|| permissions.isAuditor()) // Hardcode to BP Cherry point
			subMenu.addChild("Washington Audit",
					"ReportWashingtonStateAudit.action?filter.riskLevel=3&filter.waAuditTypes=176", "WashingtonAudit");
		if (permissions.hasPermission(OpPerms.EmployeeList))
			addChildAction(subMenu, "EmployeeList");

		if (permissions.isAdmin()) {
			String custom = "";
			if (permissions.hasGroup(User.GROUP_CSR))
				custom = "?filter.conAuditorId=" + permissions.getShadowedUserID();
			if (permissions.hasGroup(User.GROUP_MARKETING))
				custom = "?filter.accountManager=" + permissions.getUserId();
			subMenu.addChild("Flag Changes", "ReportFlagChanges.action" + custom, "FlagChanges");
		}

		if (permissions.isRequiresCompetencyReview()) {
			subMenu = menu.addChild(getText("global.HSECompetencies"));

			if (permissions.hasPermission(OpPerms.DefineCompetencies))
				subMenu.addChild(getTitle("DefineCompetencies"), "DefineCompetencies.action", "DefineCompetencies");

			subMenu.addChild(getTitle("ReportCompetencyByAccount"), "ReportCompetencyByAccount.action",
					"ReportCompetByAccount");
			subMenu.addChild(getTitle("ReportCompetencyByEmployee"), "ReportCompetencyByEmployee.action",
					"ReportCompetByEmployee");
			// subMenu.addChild("Employee Turnover",
			// "ReportEmployeeTurnover.action");
		}

		if (permissions.isRequiresOQ()) {
			subMenu = menu.addChild(getText("global.OperatorQualification"));

			if (permissions.hasPermission(OpPerms.ManageJobTasks))
				subMenu.addChild(getTitle("ManageJobTasksOperator"), "ManageJobTasksOperator.action",
						"ManageJobTasksOperator");
			if (permissions.hasPermission(OpPerms.ManageProjects))
				subMenu.addChild(getTitle("ManageProjects"), "ManageProjects.action", "ManageProjects");

			subMenu.addChild(getTitle("ReportOQ"), "ReportOQ.action", "ReportQQ");
			subMenu.addChild(getTitle("ReportOQEmployees"), "ReportOQEmployees.action", "ReportQQEmployees");
		}

		if ((permissions.isOperatorCorporate() && permissions.isRequiresOQ()) || permissions.isAdmin())
			subMenu.addChild(getTitle("ReportAssessmentTests"), "ReportAssessmentTests.action", "ReportAssessmentTests");

		if (permissions.seesAllContractors())
			subMenu.addChild("Report WCB Accounts", "ReportWcbAccounts.action", "ReportWcbAccounts");

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
		MenuComponent subMenu = menu.addChild(getText("menu.Support"), "Contact.action");
		// We may want to add the Contact page as a child menu after we have
		// more "Support" options
		// subMenu.addChild(getTitle("Contact"), "Contact.action");
		return subMenu;
	}

	static private void addChildAction(MenuComponent menu, String actionName) {
		menu.addChild(getTitle(actionName), actionName + ".action", actionName);
	}

}
