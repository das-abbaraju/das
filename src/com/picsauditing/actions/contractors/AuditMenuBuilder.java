package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorTagCategory;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;

public class AuditMenuBuilder {
	private static final Logger logger = LoggerFactory.getLogger(AuditMenuBuilder.class);

	public final int MAX_MENU_ITEM = 10;

	private ContractorAccount contractor;
	private Permissions permissions;
	private Locale locale;
	private URLUtils urlUtils;

	private List<MenuComponent> auditMenu;
	private Set<AuditType> manuallyAdded;
	private Set<ContractorAudit> sortedAudits;

	private boolean clientReviewsUnderAuditGUARD = false;

	public AuditMenuBuilder(ContractorAccount contractor, Permissions permissions) {
		this.contractor = contractor;
		this.permissions = permissions;
	}

	public List<MenuComponent> buildAuditMenuFrom(Collection<ContractorAudit> activeAudits) {
		if (contractor == null || permissions == null) {
			throw new IllegalArgumentException("Missing contractor or permissions");
		}

		auditMenu = new ArrayList<>();
		// PICS-9473 Operators should not be able to view a Pending/Requested
		// Contractor's PQF or Flag
		if (permissions.isOperatorCorporate()
				&& (contractor.getStatus() == AccountStatus.Pending || contractor.getStatus() == AccountStatus.Requested)) {
			return auditMenu;
		}

		sortedAudits = getSortedAudits(activeAudits);
		buildServiceMenus();

		return auditMenu;
	}

	public void setManuallyAddedAuditTypes(Set<AuditType> manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

	public void setClientReviewsUnderAuditGUARD(boolean showClientReviews) {
		this.clientReviewsUnderAuditGUARD = showClientReviews;
	}

	private void buildServiceMenus() {
		buildPQFSection();
		buildAnnualUpdatesSection();
		buildInsureGUARDSection();
		buildEmployeeGUARDSection();
		buildAuditGUARDSection();
		buildClientReviewsSection();
	}

	private void buildPQFSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id",
						contractor.getId());

				// Add the PQF
				MenuComponent subMenu = new MenuComponent(getText("AuditType.1.name"), contractorDocumentsPage);
				boolean addMoreMenu = false;
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					AuditType auditType = audit.getAuditType();

					if (auditType.getClassType().isPqf() && auditType.getId() != AuditType.IHG_INSURANCE_QUESTIONAIRE) {
						if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0
								|| auditType.getId() == AuditType.IMPORT_PQF) {
							if (subMenu.getChildren().size() < MAX_MENU_ITEM || auditType.isPicsPqf()) {
								MenuComponent childMenu = createMenuItem(subMenu, audit);
								childMenu.setUrl(urlUtils().getActionUrl("Audit", "auditID", audit.getId()));
							}

							addMoreMenu = (subMenu.getChildren().size() >= MAX_MENU_ITEM);

							// Put Trades menu after 'PQF' menu entry
							if (auditType.isPicsPqf()) {
								MenuComponent tradeItem = subMenu.addChild(getText("ContractorTrades.title"),
										urlUtils().getActionUrl("ContractorTrades", "id", contractor.getId()));
								if (!contractor.isNeedsTradesUpdated() && permissions.isOperatorCorporate()) {
									// Only operators need to see the
									// checkmarks?
									tradeItem.setCssClass("done");
								}
							}
						}

						iterator.remove();
					}
				}

				if (addMoreMenu) {
					subMenu.addChild(getText("global.More"), contractorDocumentsPage);
				}

				auditMenu.add(subMenu);
			}
		} catch (Exception exception) {
			logger.error("Error building Annual Updates section in AuditMenuBuilder", exception);
		}
	}

	private void buildAnnualUpdatesSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				// Add the Annual Updates
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id",
						contractor.getId());
				MenuComponent subMenu = new MenuComponent(getText("AuditType.11.name"), contractorDocumentsPage + "#"
						+ ContractorDocuments.getSafeName(getText("AuditType.11.name")));
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					if (audit.getAuditType().isAnnualAddendum()) {
						String linkText = getTextParameterized("ContractorActionSupport.Update", audit.getAuditFor());
						if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
							MenuComponent childMenu = createMenuItem(subMenu, audit);
							childMenu.setName(linkText);
						}

						iterator.remove();
					}
				}

				addSubMenu(auditMenu, subMenu);
			}
		} catch (Exception exception) {
			logger.error("Error building Annual Updates section in AuditMenuBuilder", exception);
		}
	}

	private void buildInsureGUARDSection() {
		try {
			if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance)) {
				// Add InsureGUARD
				String conInsureGUARDPage = urlUtils().getActionUrl("ConInsureGUARD", "id", contractor.getId());
				MenuComponent subMenu = new MenuComponent(getText("global.InsureGUARD"), conInsureGUARDPage);
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					if ((audit.getAuditType().getClassType().equals(AuditTypeClass.Policy) || audit.getAuditType()
							.getId() == AuditType.IHG_INSURANCE_QUESTIONAIRE) && audit.getOperators().size() > 0) {
						if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
							MenuComponent childMenu = createMenuItem(subMenu, audit);
							if (audit.getAuditType().getId() == AuditType.IHG_INSURANCE_QUESTIONAIRE) {
								childMenu.setName(getText(audit.getAuditType().getI18nKey("name")));
							} else if (audit.getEffectiveDate() != null || audit.getAuditType().isWCB()) {
								String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
								childMenu.setName(getText(audit.getAuditType().getI18nKey("name")) + " '" + year);
							} else {
								childMenu.setName(getText(audit.getAuditType().getI18nKey("name")) + " "
										+ getText("ContractorAudit.New"));
							}

							String linkUrl = urlUtils().getActionUrl("Audit", "auditID", audit.getId());
							childMenu.setUrl(linkUrl);
						}

						iterator.remove();
					}
				}

				if (subMenu.getChildren().size() > 0) {
					subMenu.addChild(getText("ContractorActionSupport.ManageCertificates"), conInsureGUARDPage);

					if (permissions.hasPermission(OpPerms.AuditVerification)) {
						String insureGUARDVerificationPage = urlUtils().getActionUrl("InsureGuardVerification",
								"contractor", contractor.getId());
						subMenu.addChild(getText("ContractorActionSupport.InsuranceVerification"),
								insureGUARDVerificationPage);
					}

					addSubMenu(auditMenu, subMenu);
				}
			}
		} catch (Exception exception) {
			logger.error("Error building InsureGUARD section in AuditMenuBuilder", exception);
		}
	}

	private void buildEmployeeGUARDSection() {
		try {
			if (contractorSafetyOrNonContractorUser() && employeeGUARDApplicable()) {
				// Add EmployeeGUARD
				String employeeDashboardPage = urlUtils().getActionUrl("EmployeeDashboard", "id", contractor.getId());
				MenuComponent subMenu = new MenuComponent(getText("global.EmployeeGUARD"), employeeDashboardPage);
				Iterator<ContractorAudit> iterator = sortedAudits.iterator();

				if (permissions.isAdmin() || permissions.hasPermission(OpPerms.ContractorAdmin)) {
					String manageEmployeesPage = urlUtils().getActionUrl("ManageEmployees", "id", contractor.getId());
					subMenu.addChild(getText("ManageEmployees.title"), manageEmployeesPage);
				}

				if (permissions.isAdmin() || permissions.hasPermission(OpPerms.DefineRoles)) {
					String manageJobRolesPage = urlUtils().getActionUrl("ManageJobRoles", "id", contractor.getId());
					subMenu.addChild(getText("ManageJobRoles.title"), manageJobRolesPage);
				}

				while (iterator.hasNext()) {
					ContractorAudit audit = iterator.next();
					if (audit.getAuditType().getClassType().isImEmployee() && audit.getOperators().size() > 0) {
						if (!audit.getAuditType().isEmployeeSpecificAudit()) {
							MenuComponent childMenu = createMenuItem(subMenu, audit);
							String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
							childMenu.setName(getText(audit.getAuditType().getI18nKey("name")) + " '" + year);
							childMenu.setUrl("Audit.action?auditID=" + audit.getId());
						}

						iterator.remove();
					}
				}

				if (canManuallyAddAudits()) {
					String auditOverridePage = urlUtils().getActionUrl("AuditOverride", "id", contractor.getId());
					subMenu.addChild(getText("EmployeeGUARD.CreateNewAudit"), auditOverridePage);
				}

				addSubMenu(auditMenu, subMenu);
			}
		} catch (Exception exception) {
			logger.error("Error building EmployeeGUARD section in AuditMenuBuilder", exception);
		}
	}

	private boolean employeeGUARDApplicable() {
		return (contractor.isHasEmployeeGUARDTag() || competencyRequiresDocumentation());
	}

	private boolean competencyRequiresDocumentation() {
		for (ContractorTag contractorTag : contractor.getOperatorTags()) {
			OperatorTagCategory operatorTagCategory = contractorTag.getTag().getCategory();
			if (operatorTagCategory != null && operatorTagCategory.isRemoveEmployeeGUARD()) {
				return false;
			}
		}

		return contractor.hasOperatorWithCompetencyRequiringDocumentation();
	}

	private boolean canManuallyAddAudits() {
		return (manuallyAdded != null && manuallyAdded.size() > 0)
				&& (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit) || permissions.isOperatorCorporate());
	}

	private void buildAuditGUARDSection() {
		try {
			if (contractorSafetyOrNonContractorUser()) {
				// Add All AuditGUARD Audits
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id",
						contractor.getId())
						+ "#" + ContractorDocuments.getSafeName(getText("global.AuditGUARD"));

				MenuComponent subMenu = new MenuComponent(getText("global.AuditGUARD"), contractorDocumentsPage);
				boolean addMoreMenu = false;

				for (ContractorAudit audit : sortedAudits) {
					if (displayAuditUnderAuditGUARDMenu(audit) && subMenu.getChildren().size() < MAX_MENU_ITEM
							&& (!permissions.isContractor() || audit.getCurrentOperators().size() > 0)) {
						MenuComponent childMenu;
						if (audit.getAuditType().getClassType().equals(AuditTypeClass.Review)) {
							childMenu = createMenuItemMovedMenuItem(subMenu, audit);
						} else {
							childMenu = createMenuItem(subMenu, audit);
						}

						String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
						String linkText = getText(audit.getAuditType().getI18nKey("name")) + " '" + year;

						if (!Strings.isEmpty(audit.getAuditFor())) {
							linkText = audit.getAuditFor() + " " + linkText;
						}

						childMenu.setName(linkText);
						addMoreMenu = (subMenu.getChildren().size() >= MAX_MENU_ITEM);
					}
				}

				if (addMoreMenu) {
					subMenu.addChild(getText("global.More"), contractorDocumentsPage);
				}

				addSubMenu(auditMenu, subMenu);
			}
		} catch (Exception exception) {
			logger.error("Error building AuditGUARD section in AuditMenuBuilder", exception);
		}
	}

	private MenuComponent createMenuItemMovedMenuItem(MenuComponent subMenu, ContractorAudit audit) {
		String link = urlUtils().getActionUrl("AuditMoved", "auditID", audit.getId());
		String linkText = getText(audit.getAuditType().getI18nKey("name"));

		MenuComponent menuItem = subMenu.addChild(linkText, link);
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
			if (contractorSafetyOrNonContractorUser()) { // Add
				// All Reviews Audits
				String contractorDocumentsPage = urlUtils().getActionUrl("ContractorDocuments", "id",
						contractor.getId())
						+ "#" + ContractorDocuments.getSafeName(getText("global.ClientReviews"));
				MenuComponent subMenu = new MenuComponent(getText("global.ClientReviews"), contractorDocumentsPage);
				boolean addMoreMenu = false;

				for (ContractorAudit audit : sortedAudits) {
					if (audit.getAuditType().getClassType().equals(AuditTypeClass.Review)) {
						if (subMenu.getChildren().size() < MAX_MENU_ITEM
								&& (!permissions.isContractor() || audit.getCurrentOperators().size() > 0)) {
							MenuComponent childMenu = createMenuItem(subMenu, audit);

							String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
							String linkText = getText(audit.getAuditType().getI18nKey("name")) + " '" + year;
							if (!Strings.isEmpty(audit.getAuditFor())) {
								linkText = audit.getAuditFor() + " " + linkText;
							}
							childMenu.setName(linkText);

							addMoreMenu = (subMenu.getChildren().size() >= MAX_MENU_ITEM);
						}
					}
				}

				if (addMoreMenu) {
					subMenu.addChild(getText("global.More"), contractorDocumentsPage);
				}

				addSubMenu(auditMenu, subMenu);
			}
		} catch (Exception exception) {
			logger.error("Error building Client Reviews section in AuditMenuBuilder", exception);
		}
	}

	private boolean contractorSafetyOrNonContractorUser() {
		return permissions.hasPermission(OpPerms.ContractorSafety) || !permissions.isContractor();
	}

	private MenuComponent createMenuItem(MenuComponent subMenu, ContractorAudit audit) {
		String link = urlUtils().getActionUrl("Audit", "auditID", audit.getId());
		String linkText = getText(audit.getAuditType().getI18nKey("name"));
		MenuComponent menuItem = subMenu.addChild(linkText, link);
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

	private void addSubMenu(List<MenuComponent> menu, MenuComponent subMenu) {
		if (subMenu.getChildren().size() > 0) {
			logger.info("Found [{}] {}{}", new Object[] { subMenu.getChildren().size(), subMenu.getName(),
					(subMenu.getChildren().size() == 1 ? "" : "s") });
			menu.add(subMenu);
		}
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
}
