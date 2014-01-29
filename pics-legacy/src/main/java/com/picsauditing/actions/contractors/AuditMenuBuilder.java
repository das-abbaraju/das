package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.external.ProductSubscriptionService;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AuditMenuBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(AuditMenuBuilder.class);
	private static final Logger PROFILER = LoggerFactory.getLogger("org.perf4j.DebugTimingLogger");
	private static final StopWatch STOPWATCH = new Slf4JStopWatch(PROFILER);

	public static final String SUMMARY = "ContractorSubmenu.MenuItem.Summary";

	public final int MAX_MENU_ITEM = 10;

	private ContractorAccount contractor;
	private Permissions permissions;
	private Locale locale;
	private URLUtils urlUtils;

	private Set<AuditType> manuallyAdded;
	private Set<ContractorAudit> sortedAudits;
	private Map<Service, List<MenuComponent>> serviceMenus;

	private boolean clientReviewsUnderAuditGUARD = false;
	private boolean docuGUARDAddMoreLinkVisible = false;

	public AuditMenuBuilder(ContractorAccount contractor, Permissions permissions) {
		this.contractor = contractor;
		this.permissions = permissions;
	}

	public Map<Service, List<MenuComponent>> buildAuditMenu() {
		if (contractor == null || permissions == null) {
			throw new IllegalArgumentException("Missing contractor or permissions");
		}

		return buildAuditMenuFrom(visibleAndNonExpiredAudits());
	}

	private List<ContractorAudit> visibleAndNonExpiredAudits() {
		List<ContractorAudit> contractorNonExpiredAudits = new ArrayList<>();

		for (ContractorAudit contractorAudit : contractor.getAudits()) {
			boolean notExpired = contractorAudit.getAuditType().isRenewable() || !contractorAudit.isExpired();

			if (contractorAudit.isVisibleTo(permissions) && notExpired) {
				contractorNonExpiredAudits.add(contractorAudit);
			}
		}

		return contractorNonExpiredAudits;
	}

	public Map<Service, List<MenuComponent>> buildAuditMenuFrom(Collection<ContractorAudit> activeAudits) {
		if (contractor == null || permissions == null) {
			throw new IllegalArgumentException("Missing contractor or permissions");
		}

		STOPWATCH.start("AuditMenuBuilder.buildAuditMenuFrom");

		serviceMenus = new TreeMap<>();
		if (permissions.isOperatorCorporate() && contractor.getStatus().isPendingRequestedOrDeactivated()) {
			return serviceMenus;
		}

		sortedAudits = getSortedAudits(activeAudits);
		buildServiceMenus();

		STOPWATCH.start("AuditMenuBuilder.buildAuditMenuFrom");

		return Collections.unmodifiableMap(serviceMenus);
	}

	public void setManuallyAddedAuditTypes(Set<AuditType> manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	public void setClientReviewsUnderAuditGUARD(boolean showClientReviews) {
		this.clientReviewsUnderAuditGUARD = showClientReviews;
	}

	private void buildServiceMenus() {
		buildDocuGUARDSection();
		buildInsureGUARDSection();
		buildEmployeeGUARDSection();
		buildAuditGUARDSection();
		buildClientReviewsSection();
	}

	private void buildDocuGUARDSection() {
		buildAnnualUpdatesSection();
		buildPQFSection();

		if (docuGUARDAddMoreLinkVisible) {
			String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id", contractor.getId());
			addToServiceMenu(Service.DOCUGUARD, new MenuComponent(getText("global.More"), contractorDocumentsPage));
		}
	}

	private void buildPQFSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id", contractor.getId());
				MenuComponent summary = new MenuComponent(getText(SUMMARY), contractorDocumentsPage);
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();
				int counter = 0;

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					AuditType auditType = audit.getAuditType();

					if (auditType.getClassType().isPqf() && auditType.getId() != AuditType.IHG_INSURANCE_QUESTIONAIRE) {
						if (auditVisibleToUserOnMenu(audit)) {
							if (counter < MAX_MENU_ITEM) {
								MenuComponent childMenu = createAuditMenuItem(audit);
								childMenu.setUrl(urlUtils().getActionUrl("Audit", "auditID", audit.getId()));

								if (auditType.isPicsPqf()) {
									addToStartOfServiceMenu(Service.DOCUGUARD, childMenu);

									// PICS Admin is logged in, see trades menu
									// Operator is logged in, see trades menu
									// Contractor is logged in and does not use v7, see trades menu
									// contractor is logged in and does use v7, do NOT see trades menu

									if (tradesSubMenuVisible()) {
										// Put Trades menu after 'PQF' menu entry
										String contractorTradesUrl = urlUtils().getActionUrl("ContractorTrades", "id", contractor.getId());
										MenuComponent tradeItem = new MenuComponent(getText("ContractorTrades.title"), contractorTradesUrl);
										if (!contractor.isNeedsTradesUpdated() && permissions.isOperatorCorporate()) {
											// Only operators need to see the checkmarks?
											tradeItem.setCssClass("done");
										}

										addToServiceMenu(Service.DOCUGUARD, tradeItem);
									}
								} else {
									addToServiceMenu(Service.DOCUGUARD, childMenu);
								}
							}

							counter++;
						}

						iterator.remove();
					}
				}

				if (counter > 0) {
					addToStartOfServiceMenu(Service.DOCUGUARD, summary);
					docuGUARDAddMoreLinkVisible = counter >= MAX_MENU_ITEM;
				}
			}
		} catch (Exception exception) {
			LOG.error("Error building Annual Updates section in AuditMenuBuilder", exception);
		}
	}

	private boolean auditVisibleToUserOnMenu(ContractorAudit audit) {
		return !permissions.isContractor() || audit.getCurrentOperators().size() > 0;
	}

	/**
	 * Contractors using the new V7 menu have their trades listed under Company
	 *
	 * @return
	 */
	private boolean tradesSubMenuVisible() {
		if (permissions.isContractor() && permissions.getAccountId() == contractor.getId()) {
			return !permissions.isUsingVersion7Menus();
		}

		return true;
	}

	private void buildAnnualUpdatesSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					if (audit.getAuditType().isAnnualAddendum()) {
						String linkText = getTextParameterized("ContractorActionSupport.Update", audit.getAuditFor());
						if (auditVisibleToUserOnMenu(audit)) {
							MenuComponent childMenu = createAuditMenuItem(audit);
							childMenu.setName(linkText);

							MenuComponent annualUpdate = createAuditMenuItem(audit);
							annualUpdate.setName(getText("AuditType.11.name") + " " + audit.getAuditFor());
							addToServiceMenu(Service.DOCUGUARD, annualUpdate);
						}

						iterator.remove();
					}
				}
			}
		} catch (Exception exception) {
			LOG.error("Error building Annual Updates section in AuditMenuBuilder", exception);
		}
	}

	private void buildInsureGUARDSection() {
		try {
			if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance)) {
				String conInsureGUARDPage = urlUtils().getActionUrl("ConInsureGUARD", "id", contractor.getId());
				MenuComponent summary = new MenuComponent(getText(SUMMARY), conInsureGUARDPage);
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();
				int counter = 0;

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					AuditType auditType = audit.getAuditType();
					boolean auditUnderInsureGUARD = auditType.getClassType().equals(AuditTypeClass.Policy)
							|| auditType.getId() == AuditType.IHG_INSURANCE_QUESTIONAIRE;

					if (auditUnderInsureGUARD && audit.getOperators().size() > 0) {
						if (auditVisibleToUserOnMenu(audit)) {
							MenuComponent childMenu = createAuditMenuItem(audit);

							if (auditType.getId() == AuditType.IHG_INSURANCE_QUESTIONAIRE) {
								childMenu.setName(getText(auditType.getI18nKey("name")));
							} else if (audit.getEffectiveDate() != null || auditType.isWCB()) {
								String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
								childMenu.setName(getText(auditType.getI18nKey("name")) + " '" + year);
							} else {
								childMenu.setName(getText(auditType.getI18nKey("name")) + " " + getText("ContractorAudit.New"));
							}

							String linkUrl = urlUtils().getActionUrl("Audit", "auditID", audit.getId());
							childMenu.setUrl(linkUrl);
							addToServiceMenu(Service.INSUREGUARD, childMenu);
						}

						iterator.remove();
						counter++;
					}
				}

				if (counter > 0) {
					if (permissions.hasPermission(OpPerms.AuditVerification)) {
						String insureGUARDVerificationPage = urlUtils().getActionUrl("InsureGuardVerification", "contractor", contractor.getId());
						MenuComponent verification = new MenuComponent(getText("ContractorActionSupport.InsuranceVerification"), insureGUARDVerificationPage);
						addToStartOfServiceMenu(Service.INSUREGUARD, verification);
					}

					MenuComponent certificates = new MenuComponent(getText("ContractorSubmenu.MenuItem.CertificatesManager"), conInsureGUARDPage);
					addToStartOfServiceMenu(Service.INSUREGUARD, certificates);

					addToStartOfServiceMenu(Service.INSUREGUARD, summary);
				}
			}
		} catch (Exception exception) {
			LOG.error("Error building InsureGUARD section in AuditMenuBuilder", exception);
		}
	}

	private void buildEmployeeGUARDSection() {
		try {
			if (contractorSafetyOrNonContractorUser() && employeeGUARDApplicable()) {
				String employeeDashboardPage = urlUtils().getActionUrl("EmployeeDashboard", "id", contractor.getId());
				MenuComponent summary = new MenuComponent(getText(SUMMARY), employeeDashboardPage);
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				if (permissions.isAdmin() || permissions.hasPermission(OpPerms.ContractorAdmin)) {
					String manageEmployeesPage = urlUtils().getActionUrl("ManageEmployees", "id", contractor.getId());
					MenuComponent manageEmployees = new MenuComponent(getText("ManageEmployees.title"), manageEmployeesPage);
					addToServiceMenu(Service.EMPLOYEEGUARD, manageEmployees);
				}

				if (permissions.isAdmin() || permissions.hasPermission(OpPerms.DefineRoles)) {
					String manageJobRolesPage = urlUtils().getActionUrl("ManageJobRoles", "id", contractor.getId());
					MenuComponent jobRoles = new MenuComponent(getText("ManageJobRoles.title"), manageJobRolesPage);
					addToServiceMenu(Service.EMPLOYEEGUARD, jobRoles);
				}

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					if (audit.getAuditType().getClassType().isImEmployee() && audit.getOperators().size() > 0) {
						if (!audit.getAuditType().isEmployeeSpecificAudit()) {
							MenuComponent childMenu = createAuditMenuItem(audit);
							String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
							childMenu.setName(getText(audit.getAuditType().getI18nKey("name")) + " '" + year);
							childMenu.setUrl("Audit.action?auditID=" + audit.getId());
							addToServiceMenu(Service.EMPLOYEEGUARD, childMenu);
						}

						iterator.remove();
					}
				}

				if (permissions.isContractor()) {
					MenuComponent employeeCompetencies = new MenuComponent(getText("EmployeeCompetencies.title"), "EmployeeCompetencies.action", "employee_competencies");
					addToServiceMenu(Service.EMPLOYEEGUARD, employeeCompetencies);
				}

				MenuComponent competencyMatrix = new MenuComponent(getText("global.HSECompetencyMatrix"), urlUtils().getActionUrl("JobCompetencyMatrix", "id", permissions.getAccountId()), "employee_competencies");
				addToServiceMenu(Service.EMPLOYEEGUARD, competencyMatrix);

				if (canManuallyAddAudits()) {
					String auditOverridePage = urlUtils().getActionUrl("AuditOverride", "id", contractor.getId());
					MenuComponent createNewAudit = new MenuComponent(getText("EmployeeGUARD.CreateNewAudit"), auditOverridePage);
					addToServiceMenu(Service.EMPLOYEEGUARD, createNewAudit);
				}

				if (permissions.isContractor()) {
					ProductSubscriptionService productSubscriptionService = SpringUtils.getBean(SpringUtils.PRODUCT_SUBSCRIPTION_SERVICE);
					if (productSubscriptionService.hasEmployeeGUARD(permissions.getAccountId())) {
						MenuComponent egV3 = new MenuComponent();
						egV3.setUrl("/employee-guard/contractor/dashboard");
						egV3.setTitle("Version 3");
						egV3.setName("Version 3");
						addToServiceMenu(Service.EMPLOYEEGUARD, egV3);
					}
				}

				addToStartOfServiceMenu(Service.EMPLOYEEGUARD, summary);
			}
		} catch (Exception exception) {
			LOG.error("Error building EmployeeGUARD section in AuditMenuBuilder", exception);
		}
	}

	private boolean employeeGUARDApplicable() {
		return (contractor.isHasEmployeeGUARDTag() || contractorHasCompetencyRequiringDocumentation());
	}

	private boolean contractorHasCompetencyRequiringDocumentation() {
		if (!worksOnsiteForValsparGarland()) {
			return false;
		}

		for (ContractorTag contractorTag : contractor.getOperatorTags()) {
			OperatorTagCategory operatorTagCategory = contractorTag.getTag().getCategory();
			if (operatorTagCategory != null && operatorTagCategory.isRemoveEmployeeGUARD()) {
				return false;
			}
		}

		return contractor.hasOperatorWithCompetencyRequiringDocumentation();
	}

	private boolean worksOnsiteForValsparGarland() {
		// FIXME This is a business requirement, need to find a better way to implement this
		return contractor.isWorksForOperator(OperatorAccount.VALSPAR_GARLAND) && contractor.isOnsiteServices();
	}

	private boolean canManuallyAddAudits() {
		return (manuallyAdded != null && manuallyAdded.size() > 0)
				&& (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit) || permissions.isOperatorCorporate());
	}

	private void buildAuditGUARDSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id", contractor.getId())
						+ "#" + ContractorDocuments.getSafeName(getText("global.AuditGUARD"));
				MenuComponent summary = new MenuComponent(getText(SUMMARY), contractorDocumentsPage);
				int counter = 0;

				for (ContractorAudit audit : sortedAudits) {
					if (displayAuditUnderAuditGUARDMenu(audit) && counter < MAX_MENU_ITEM
							&& (auditVisibleToUserOnMenu(audit))) {
						MenuComponent childMenu;
						if (audit.getAuditType().getClassType().equals(AuditTypeClass.Review)) {
							childMenu = createMenuItemMovedMenuItem(audit);
						} else {
							childMenu = createAuditMenuItem(audit);
						}

						String linkText;

						if (audit.getAuditType().getPeriod().isMonthlyQuarterlyAnnual()) {
							linkText = getText(audit.getAuditType().getI18nKey("name")) + " " + audit.getAuditFor();
						} else {
							String year = DateBean.format(audit.getEffectiveDateLabel(), "yyyy");
							linkText = getText(audit.getAuditType().getI18nKey("name")) + " " + year;
							if (!Strings.isEmpty(audit.getAuditFor())) {
								linkText = audit.getAuditFor() + " " + linkText;
							}
						}

						childMenu.setName(linkText);
						addToServiceMenu(Service.AUDITGUARD, childMenu);
						counter++;
					}
				}

				if (counter > 0) {
					addToStartOfServiceMenu(Service.AUDITGUARD, summary);
				}

				if (counter >= MAX_MENU_ITEM) {
					MenuComponent addMore = new MenuComponent(getText("global.More"), contractorDocumentsPage);
					addToServiceMenu(Service.AUDITGUARD, addMore);
				}
			}
		} catch (Exception exception) {
			LOG.error("Error building AuditGUARD section in AuditMenuBuilder", exception);
		}
	}

	private MenuComponent createMenuItemMovedMenuItem(ContractorAudit audit) {
		String link = urlUtils().getActionUrl("AuditMoved", "auditID", audit.getId());
		String linkText = getText(audit.getAuditType().getI18nKey("name"));

		MenuComponent menuItem = new MenuComponent(linkText, link);
		menuItem.setAuditId(audit.getId());

		if (consideredDone(audit)) {
			menuItem.setCssClass("done");
		}

		return menuItem;
	}

	private boolean displayAuditUnderAuditGUARDMenu(ContractorAudit audit) {
		AuditTypeClass classType = audit.getAuditType().getClassType();

		if (classType.equals(AuditTypeClass.Audit)) {
			return true;
		} else if (classType.equals(AuditTypeClass.Review) && clientReviewsUnderAuditGUARD) {
			return true;
		}

		return false;
	}

	private void buildClientReviewsSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id", contractor.getId())
						+ "#" + ContractorDocuments.getSafeName(getText("global.ClientReviews"));
				MenuComponent summary = new MenuComponent(getText(SUMMARY), contractorDocumentsPage);
				int counter = 0;

				for (ContractorAudit audit : sortedAudits) {
					if (audit.getAuditType().getClassType().equals(AuditTypeClass.Review)) {
						if (counter < MAX_MENU_ITEM && auditVisibleToUserOnMenu(audit)) {
							MenuComponent childMenu = createAuditMenuItem(audit);

							String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
							String linkText = getText(audit.getAuditType().getI18nKey("name")) + " '" + year;

							if (!Strings.isEmpty(audit.getAuditFor())) {
								linkText = audit.getAuditFor() + " " + linkText;
							}

							childMenu.setName(linkText);
							addToServiceMenu(Service.CLIENT_REVIEWS, childMenu);
							counter++;
						}
					}
				}

				if (counter > 0) {
					addToStartOfServiceMenu(Service.CLIENT_REVIEWS, summary);
				}

				if (counter >= MAX_MENU_ITEM) {
					MenuComponent addMore = new MenuComponent(getText("global.More"), contractorDocumentsPage);
					addToServiceMenu(Service.CLIENT_REVIEWS, addMore);
				}
			}
		} catch (Exception exception) {
			LOG.error("Error building Client Reviews section in AuditMenuBuilder", exception);
		}
	}

	private void addToServiceMenu(Service service, MenuComponent menu) {
		initializeServiceMenu(service);
		serviceMenus.get(service).add(menu);
	}

	private void addToStartOfServiceMenu(Service service, MenuComponent menu) {
		initializeServiceMenu(service);
		serviceMenus.get(service).add(0, menu);
	}

	private void initializeServiceMenu(Service service) {
		if (serviceMenus.get(service) == null) {
			serviceMenus.put(service, new ArrayList<MenuComponent>());
		}
	}

	private boolean contractorSafetyOrNonContractorUser() {
		return permissions.hasPermission(OpPerms.ContractorSafety) || !permissions.isContractor();
	}

	private MenuComponent createAuditMenuItem(ContractorAudit audit) {
		String link = urlUtils().getActionUrl("Audit", "auditID", audit.getId());
		String linkText = getText(audit.getAuditType().getI18nKey("name"));
		if (audit.getAuditType().getPeriod().isMonthlyQuarterlyAnnual()) {
			linkText += " " + audit.getAuditFor();
		}
		MenuComponent menuItem = new MenuComponent(linkText, link);
		menuItem.setAuditId(audit.getId());

		if (consideredDone(audit)) {
			menuItem.setCssClass("done");
		}

		return menuItem;
	}

	private TreeSet<ContractorAudit> getSortedAudits(Collection<ContractorAudit> activeAudits) {
		// Sort audits, by throwing them into a tree set and sorting them by
		// display and then name
		TreeSet<ContractorAudit> treeSet = new TreeSet<>(new Comparator<ContractorAudit>() {
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				if (o1 == null || o2 == null) {
					return 0; // can't compare null objects
				}

				if (o1.getAuditType().getDisplayOrder() < o2.getAuditType().getDisplayOrder()) {
					return -1;
				}

				if (o1.getAuditType().getDisplayOrder() > o2.getAuditType().getDisplayOrder()) {
					return 1;
				}

				if (o1.getAuditType().equals(o2.getAuditType())) {
					if (o1.getAuditFor() != null && o2.getAuditFor() != null) {
						if (o1.getAuditType().isAnnualAddendum()) {
							// e.g. Annual Update 2011 vs Annual Update 2010
							int descendingCompare = o2.getAuditFor().compareTo(o1.getAuditFor());
							return (descendingCompare == 0) ? o1.getId() - o2.getId() : descendingCompare;
						} else {
							int ascendingCompare = o1.getAuditFor().compareTo(o2.getAuditFor());
							return (ascendingCompare == 0) ? o1.getId() - o2.getId() : ascendingCompare;
						}
					} else {
						// Just in case
						return o1.getId() - o2.getId();
					}
				}

				// get display names as seen in menu
				String name1 = getText(o1.getAuditType().getI18nKey("name"));
				String name2 = getText(o2.getAuditType().getI18nKey("name"));

				if (name1 == null || name2 == null) {
					// Just in case
					return o1.getId() - o2.getId();
				}

				return name1.compareTo(name2);
			}
		});

		treeSet.addAll(activeAudits);

		return treeSet;
	}

	private String getText(String key) {
		if (Strings.isNotEmpty(key)) {
			return getTranslationService().getText(key, locale());
		}

		return null;
	}

	private String getTextParameterized(String key, Object... arguments) {
		String translation = null;

		if (Strings.isNotEmpty(key)) {
			translation = getTranslationService().getText(key, locale(), arguments);
		}

		return translation;
	}

	private boolean consideredDone(ContractorAudit audit) {
		if (permissions.isContractor() || permissions.isPicsEmployee()) {
			return false;
		}

		if (visibleOnMenu(audit) && audit.getAuditType().isCanOperatorView()) {
			for (ContractorAuditOperator cao : audit.getOperators()) {
				if (cao.isVisibleTo(permissions)) {
					return cao.getStatus().after(AuditStatus.Resubmitted);
				}
			}
		}

		return false;
	}

	private boolean visibleOnMenu(ContractorAudit audit) {
		return audit.getAuditType().isRenewable() || !audit.isExpired();
	}

	private TranslationService getTranslationService() {
		return TranslationServiceFactory.getTranslationService();
	}

	private Locale locale() {
		if (locale == null) {
			locale = TranslationActionSupport.getLocaleStatic();
		}

		return locale;
	}

	private URLUtils urlUtils() {
		if (urlUtils == null) {
			urlUtils = new URLUtils();
		}

		return urlUtils;
	}

	public enum Service {
		DOCUGUARD, INSUREGUARD, EMPLOYEEGUARD, AUDITGUARD, CLIENT_REVIEWS
	}
}
