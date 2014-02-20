package com.picsauditing.access;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.actions.contractors.AuditMenuBuilder;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class ContractorSubmenuBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ContractorSubmenuBuilder.class);
	public static final String NOTES = "global.Notes";
	public static final String BILLING_DETAIL = "BillingDetail.title";
	public static final String COMPANY = "global.Company";
	public static final String TRADES = "global.Trades";
	public static final String DOCUGUARD = "menu.DocuGUARD";
	public static final String AUDITGUARD = "menu.AuditGUARD";
	public static final String MANUAL_AUDIT = "AuditType.2.name";
	public static final String PQF = "AuditType.1.name";
	public static final String IMPLEMENTATION_AUDIT = "AuditType.3.name";
	public static final String INSUREGUARD = "menu.InsureGUARD";
	public static final String MANAGE_CERTIFICATES = "ContractorActionSupport.ManageCertificates";
	public static final String AUTOMOBILE_LIABILITY = "AuditType.15.name";
	public static final String EXCESS_UMBRELLA_LIABILITY = "AuditType.16.name";
	public static final String GENERAL_LIABILITY = "AuditType.13.name";
	public static final String WORKER_COMP = "AuditType.14.name";
	public static final String EMPLOYEEGUARD = "menu.EmployeeGUARD";
	public static final String CLIENT_REVIEW = "menu.ClientReview";

	public ContractorSubmenuBuilder() {
	}

	public MenuComponent buildMenubar(ContractorAccount contractorAccount, Permissions permissions,
	                                  boolean includeSupportMenu) throws RecordNotFoundException {
		Logger profiler = LoggerFactory.getLogger("org.perf4j.DebugTimingLogger");
		StopWatch stopwatch = new Slf4JStopWatch(profiler);
		stopwatch.start("ContractorSubmenuBuilder.buildMenubar");

		AuditMenuBuilder auditMenuBuilder = new AuditMenuBuilder(contractorAccount, permissions);
		Map<AuditMenuBuilder.Service, List<MenuComponent>> serviceMenus = auditMenuBuilder.buildAuditMenu();

		MenuComponent menubar = new MenuComponent();
		addChildrenToMenubar(menubar, getText(DOCUGUARD), serviceMenus.get(AuditMenuBuilder.Service.DOCUGUARD));
		addChildrenToMenubar(menubar, getText(AUDITGUARD), serviceMenus.get(AuditMenuBuilder.Service.AUDITGUARD));
		addChildrenToMenubar(menubar, getText(INSUREGUARD), serviceMenus.get(AuditMenuBuilder.Service.INSUREGUARD));
		addChildrenToMenubar(menubar, getText(EMPLOYEEGUARD), serviceMenus.get(AuditMenuBuilder.Service.EMPLOYEEGUARD));
		addChildrenToMenubar(menubar, getText(CLIENT_REVIEW), serviceMenus.get(AuditMenuBuilder.Service.CLIENT_REVIEWS));
		addSupportMenu(menubar, permissions, contractorAccount, includeSupportMenu);

		stopwatch.stop("ContractorSubmenuBuilder.buildMenubar");
		return menubar;
	}

	private MenuComponent addChildrenToMenubar(MenuComponent menubar, String title, List<MenuComponent> menuItems) {
		if (menuItems != null && !menuItems.isEmpty()) {
			MenuComponent menuEntry = menubar.addChild(title);
			for (MenuComponent item : menuItems) {
				menuEntry.addChild(item);
			}
            MenuBuilder.removeMenuIfEmpty(menubar,menuEntry);
		}

		return menubar;
	}

	// todo: Revisit. For now, this is basically the same as the Support item on
	// main menu. We'll see if the two menus
	// end up diverging or not.
	protected MenuComponent addSupportMenu(MenuComponent menubar, Permissions permissions,
	                                       ContractorAccount contractorAccount, boolean includeSupportMenu) {
		if (includeSupportMenu) {
			MenuComponent supportMenu = menubar.addChild(getText("menu.Support"));

			supportMenu.addChild(getText("Header.HelpCenter"), "HelpCenter.action", "help_center");
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
			 * "Reference!navigationRestructure.action",
			 * "navigation_restructure");
			 */
		}

		return menubar;
	}

	private static String getText(String key) {
		return TranslationServiceFactory.getTranslationService().getText(key, TranslationActionSupport.getLocaleStatic());
	}

}
