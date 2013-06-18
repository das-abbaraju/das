package com.picsauditing.access;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.report.RecordNotFoundException;

public final class ContractorSubmenuBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ContractorSubmenuBuilder.class);
	public static final String NOTES = "global.Notes";
	public static final String USERS = "global.Users";
	public static final String BILLING_DETAIL = "BillingDetail.title";
	public static final String PAYMENT_OPTIONS = "ContractorPaymentOptions.header";
	public static final String COMPANY = "global.Company";
	public static final String CLIENT_SITES = "global.Facilities";
	public static final String SUBCONTRACTOR_CLIENT_SITES = "SubcontractorFacilities.title";
	public static final String TRADES = "global.Trades";
	public static final String RESOURCES = "global.Resources";
	public static final String COMPANY_PROFILE = "ContractorSubmenu.MenuItem.CompanyProfile";
	public static final String DOCUGUARD = "global.DocuGUARD";
	public static final String DOCUGUARD_SUMMARY = "ContractorSubmenu.MenuItem.Summary";
	public static final String PQF_CURRENT_YEAR = "AuditType.1.name";
	public static final String ANNUAL_UPDATE_CURRENT_YEAR = "AuditType.11.name" ;
	public static final String AUDITGUARD = "global.AuditGUARD";
	public static final String AUDITGUARD_SUMMARY = "ContractorSubmenu.MenuItem.Summary";
	public static final String MANUAL_AUDIT = "AuditType.2.name";
	public static final String PQF = "AuditType.1.name";
	public static final String IMPLEMENTATION_AUDIT = "AuditType.3.name";
	public static final String INSUREGUARD = "global.InsureGUARD";
	public static final String INSUREGUARD_SUMMARY = "ContractorSubmenu.MenuItem.Summary";
	public static final String MANAGE_CERTIFICATES = "ContractorActionSupport.ManageCertificates";
	public static final String CERTIFICATES_MANAGER = "ContractorSubmenu.MenuItem.CertificatesManager";
	public static final String AUTOMOBILE_LIABILITY = "AuditType.15.name";
	public static final String EXCESS_UMBRELLA_LIABILITY = "AuditType.16.name";
	public static final String GENERAL_LIABILITY = "AuditType.13.name";
	public static final String WORKER_COMP = "AuditType.14.name";
	public static final String EMPLOYEEGUARD = "global.EmployeeGUARD";
	public static final String EMPLOYEEGUARD_SUMMARY = "ContractorSubmenu.MenuItem.Summary";
	public static final String EMPLOYEES = "global.Employees";
	public static final String JOB_ROLES = "ContractorSubmenu.MenuItem.JobRoles";
	public static final String DASHBOARD = "ContractorSubmenu.MenuItem.Dashboard";
	private static I18nCache i18nCache = I18nCache.getInstance();

	@Autowired
	private ContractorAccountDAO contractorAccountDao;

	@Autowired
	private ContractorActionSupport contractorActionSupport;

	public ContractorSubmenuBuilder() {
	}

	public MenuComponent buildMenubar(ContractorAccount contractorAccount,
			Permissions permissions, boolean includeSupportMenu)
					throws RecordNotFoundException {
		List<MenuComponent> auditMenu = buildAuditMenu(permissions, contractorAccount);

		MenuComponent menubar = new MenuComponent();
		addCompanyMenu(menubar, permissions, contractorAccount);
		addDocuguardMenu(menubar, permissions, contractorAccount, auditMenu);
		addAuditguardMenu(menubar, permissions, contractorAccount, auditMenu);
		addInsureguardMenu(menubar, permissions, contractorAccount, auditMenu);
		addEmployeeguardMenu(menubar, permissions, contractorAccount);
		addSupportMenu(menubar, permissions, contractorAccount,
				includeSupportMenu);

		return menubar;
	}

	// todo: Not good that we're injecting and calling methods on a controller here. This is a interim approach that does not change the existing code.
	// Eventually, we need to extract/rework all the logic in ContractorActionSupport and dependencies that builds the auditmenu.
	protected List<MenuComponent> buildAuditMenu(Permissions permissions, ContractorAccount contractorAccount) throws RecordNotFoundException {

		contractorActionSupport.setContractor(contractorAccount);
		contractorActionSupport.setPermissions(permissions);
		List<MenuComponent> auditMenu = contractorActionSupport.getAuditMenu();

		return auditMenu;
	}

	protected MenuComponent addCompanyMenu(MenuComponent menubar,
			Permissions permissions, ContractorAccount contractorAccount) {

		MenuComponent companyMenu = menubar.addChild(getText(COMPANY));

		companyMenu.addChild(getText(DASHBOARD), "ContractorView.action?id="
				+ contractorAccount.getId());

		companyMenu.addChild(getText(NOTES), "ContractorNotes.action?id="
				+ contractorAccount.getId());

		if (!permissions.isOperator() && !permissions.isInsuranceOnlyContractorUser()) {
			companyMenu.addChild(
					getText(CLIENT_SITES),
					"ContractorFacilities.action?id="
							+ contractorAccount.getId());
		}

		if (permissions.isGeneralContractor()) {
			companyMenu.addChild(
					getText(SUBCONTRACTOR_CLIENT_SITES),
					"SubcontractorFacilities.action?id="
							+ contractorAccount.getId());
		}

		if (permissions.isAdmin() || (permissions.isContractor() && permissions.hasPermission(OpPerms.ContractorAdmin))) {
			companyMenu.addChild(getText(USERS), "UsersManage.action?account="
					+ contractorAccount.getId());
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			companyMenu.addChild(getText(TRADES), "ContractorTrades.action?id="
					+ contractorAccount.getId());
		}

		if (permissions.isContractor()) {
			companyMenu.addChild(getText(RESOURCES), "Resources.action");
		}
		//todo: Investigate. For some reason the conHeader.jsp is showing these erroneously?
		if (permissions.isAdmin() && permissions.getAccountStatus() != AccountStatus.Demo) {
			companyMenu.addChild(getText(BILLING_DETAIL),
					"BillingDetail.action?id=" + contractorAccount.getId(),
					"billing_detail");
			companyMenu.addChild(
					getText(PAYMENT_OPTIONS),
					"ContractorPaymentOptions.action?id="
							+ contractorAccount.getId());
		}

		if (permissions.isAdmin()) {
			companyMenu.addChild(getText(COMPANY_PROFILE),
					"ContractorEdit.action?id=" + contractorAccount.getId(),
					"edit_contractor");
		}

		return menubar;
	}

	protected MenuComponent addAuditguardMenu(MenuComponent menubar,
			Permissions permissions, ContractorAccount contractorAccount,
			List<MenuComponent> auditMenu) {

		MenuComponent auditguard = menubar.addChild(getText(AUDITGUARD));

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			auditguard.addChild(
					getText(AUDITGUARD_SUMMARY),
					"ContractorDocuments.action?id="
							+ contractorAccount.getId() + "#auditguard");

			MenuComponent menuComponent = findComponent(AUDITGUARD, MANUAL_AUDIT, auditMenu);
			if (menuComponent != null) {
				auditguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId());
			}

			menuComponent = findComponent(AUDITGUARD, IMPLEMENTATION_AUDIT, auditMenu);
			if (menuComponent != null) {
				auditguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId());
			}
		}

		return menubar;
	}

	protected MenuComponent addDocuguardMenu(MenuComponent menubar,
			Permissions permissions, ContractorAccount contractorAccount,
			List<MenuComponent> auditMenu) {

		MenuComponent docuguard = menubar.addChild(getText(DOCUGUARD));

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			docuguard.addChild(
					getText(DOCUGUARD_SUMMARY),
					"ContractorDocuments.action?id="
							+ contractorAccount.getId());

			MenuComponent menuComponent = findComponent(PQF, PQF, auditMenu);
			if (menuComponent != null) {
				// todo: See if there is an effectiveDate on the audit and append the yyyy string to the name
				docuguard.addChild(getText(PQF_CURRENT_YEAR), "Audit.action?auditID=" + menuComponent.getAuditId());
			}
			// todo: See if there is any way to find and append the yyyy string to the name
			docuguard.addChild(
					getText(ANNUAL_UPDATE_CURRENT_YEAR),
					"ContractorDocuments.action?id="
							+ contractorAccount.getId()
							+ "#"
							+ ContractorDocuments
									.getSafeName(getText("AuditType.11.name")));
		}

		return menubar;
	}

	protected MenuComponent addInsureguardMenu(MenuComponent menubar,
			Permissions permissions, ContractorAccount contractorAccount,
			List<MenuComponent> auditMenu) {

		MenuComponent insureguard = menubar.addChild(getText(INSUREGUARD));

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance)) {

			insureguard.addChild(getText(INSUREGUARD_SUMMARY),
					"ConInsureGUARD.action?id=" + contractorAccount.getId()); // todo:
																				// Verify.
																				// The
																				// Summary
																				// and
																				// Certificates
																				// Manager
																				// links
																				// are
																				// dupes?

			MenuComponent menuComponent = findComponent(INSUREGUARD, MANAGE_CERTIFICATES, auditMenu);
			if (menuComponent != null) {
				insureguard
						.addChild(
								getText(CERTIFICATES_MANAGER),
								"ConInsureGUARD.action?id="
										+ contractorAccount.getId());
			}

			menuComponent = findComponent(INSUREGUARD, AUTOMOBILE_LIABILITY, auditMenu);
			if (menuComponent != null) {
				insureguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId());
			}

			menuComponent = findComponent(INSUREGUARD, EXCESS_UMBRELLA_LIABILITY, auditMenu);
			if (menuComponent != null) {
				insureguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId()); // todo: Remove "/" from the english translation?
			}

			menuComponent = findComponent(INSUREGUARD, GENERAL_LIABILITY, auditMenu);
			if (menuComponent != null) {
				insureguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId());
			}

			menuComponent = findComponent(INSUREGUARD, WORKER_COMP, auditMenu);
			if (menuComponent != null) {
				insureguard.addChild(expandYearToFourDigits(menuComponent.getName()), "Audit.action?auditID=" + menuComponent.getAuditId());
			}
		}

		return menubar;
	}

	protected MenuComponent addEmployeeguardMenu(MenuComponent menubar, Permissions permissions, ContractorAccount contractorAccount) {

		MenuComponent employeeguard = menubar.addChild(getText(EMPLOYEEGUARD));

		if (contractorAccount.isHasEmployeeGUARDTag() && (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))) {
			employeeguard.addChild(getText(EMPLOYEEGUARD_SUMMARY),
					"EmployeeDashboard.action?id=" + contractorAccount.getId());

			if (permissions.isAdmin() || permissions.hasPermission(OpPerms.ContractorAdmin)) {
				employeeguard.addChild(
						getText(EMPLOYEES),
						"ManageEmployees.action?id="
								+ contractorAccount.getId());
			}

			if (permissions.isAdmin() || permissions.hasPermission(OpPerms.DefineRoles)) {
				employeeguard
						.addChild(
								getText(JOB_ROLES),
								"ManageJobRoles.action?id="
										+ contractorAccount.getId());
			}

			// Competencies coming soon...
			//			if (permissions.isAdmin() || permissions.hasPermission(???)) {
			// employeeguard.addChild(getText(COMPETENCIES),
			// "ManageCompetencies.action?id=" + contractorAccount.getId());
			//			}

		}

		return menubar;
	}

	// todo: Revisit. For now, this is basically the same as the Support item on main menu. We'll see if the two menus
	// end up diverging or not.
	protected MenuComponent addSupportMenu(MenuComponent menubar,
			Permissions permissions, ContractorAccount contractorAccount,
			boolean includeSupportMenu) {
		if (includeSupportMenu) {

			MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));

			String helpUrl = "http://help.picsorganizer.com/login.action?os_destination=homepage.action&os_username=admin&os_password=ad9870mins";
			supportMenu.addChild(getText("Header.HelpCenter"), helpUrl, "help_center");
			supportMenu.addChild(getText("Registration.Error.LiveChat"), "#", "live_chat");
			supportMenu.addChild(getText("global.ContactPICS"), "Contact.action", "contact_action");
			supportMenu.addChild(getText("global.AboutPICS"), "About.action", "about_pics");

			MenuComponent referenceMenu = supportMenu.addChild("Reference");
			if (permissions.hasPermission(OpPerms.ManageTrades)) {
				referenceMenu.addChild(getText("TradeTaxonomy.title"), "TradeTaxonomy.action", "TradeTaxonomy");
			}

			referenceMenu.addChild("Navigation Menu", "Reference!navigationMenu.action", "navigation_menu");
			referenceMenu.addChild("Dynamic Reports", "Reference!dynamicReport.action", "dynamic_report");
			referenceMenu.addChild("Reports Manager", "Reference!reportsManager.action", "reports_manager");
			/*
			 * referenceMenu.addChild("Navigation Restructure",
			 * "Reference!navigationRestructure.action", "navigation_restructure");
			 */
		}

		return menubar;
	}

	// todo: This should go away with a rework on AuditMenuBuilder
	private String expandYearToFourDigits(String name) {
		return name.replace("'12", "2012")
				.replace("'13", "2013")
				.replace("'14", "2014")
				.replace("'15", "2015")
				.replace("'16", "2016");
	}

	// todo: Revisit. Fragile. For now, we'll reuse the code that builds auditMenu.
	private MenuComponent findComponent(String componentKey, String subComponentKey, List<MenuComponent> menuComponents) {

		String componentTranslation = getText(componentKey);
		String subComponentTranslation = null;

		if (subComponentKey != null) {
			subComponentTranslation = getText(subComponentKey);
		}

		for (MenuComponent menuComponent : menuComponents) {
			if (menuComponent.getName().equals(componentTranslation)) {
				if (subComponentKey == null) {
					return menuComponent;
				}
				for (MenuComponent subMenuComponent : menuComponent.getChildren()) {
					if (subMenuComponent.getName().startsWith(subComponentTranslation)) {
						return subMenuComponent;
					}
				}
			}
		}
		return null;
	}

	private static String getText(String key) {
		return i18nCache.getText(key, TranslationActionSupport.getLocaleStatic());
	}

}
