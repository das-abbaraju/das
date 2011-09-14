package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.PermissionToViewContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorActionSupport extends AccountActionSupport {

	protected ContractorAccount contractor;
	private List<ContractorAudit> contractorNonExpiredAudits = null;

	@Autowired
	protected ContractorAccountDAO accountDao;
	@Autowired
	protected ContractorAuditDAO auditDao;
	@Autowired
	private CertificateDAO certificateDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;

	private List<ContractorOperator> operators;
	protected boolean limitedView = false;
	protected List<ContractorOperator> activeOperators;
	protected Map<ContractorAudit, AuditStatus> contractorAuditWithStatuses = null;

	protected List<Certificate> certificates = null;

	private Map<ContractorTrade, String> tradeCssMap;

	// TODO cleanup the PermissionToViewContractor duplicate code here
	private PermissionToViewContractor permissionToViewContractor = null;

	protected ContractorRegistrationStep currentStep = null;

	public String execute() throws Exception {
		findContractor();
		return SUCCESS;
	}

	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		contractor = accountDao.find(id);
		account = contractor;
		if (contractor == null)
			throw new RecordNotFoundException("Contractor " + id);

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");
	}

	protected boolean checkPermissionToView() {
		loadPermissions();
		if (id == 0 || permissions == null)
			return false;

		if (permissionToViewContractor == null) {
			permissionToViewContractor = new PermissionToViewContractor(id, permissions);
			permissionToViewContractor.setActiveAudits(getActiveAudits());
			permissionToViewContractor.setOperators(getOperators());
		}
		return permissionToViewContractor.check(limitedView);
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	protected void resetActiveAudits() {
		contractorNonExpiredAudits = null;
	}

	public Map<ContractorAudit, AuditStatus> getActiveAuditsStatuses() {
		if (contractorAuditWithStatuses == null) {
			contractorAuditWithStatuses = new HashMap<ContractorAudit, AuditStatus>();
			List<ContractorAudit> list = contractor.getAudits();
			for (ContractorAudit contractorAudit : list) {
				// .isPqf may be wrong here. Consider using
				// contractorAudit.getAuditType().isRenewable() instead
				if (contractorAudit.getAuditType().isPqf() || !contractorAudit.isExpired()) {
					// We're dealing with a non-archived document
					if (permissions.isContractor()) {
						if (contractorAudit.getAuditType().isCanContractorView()) {
							contractorAuditWithStatuses.put(contractorAudit, null);
						}
					} else if (permissions.isPicsEmployee()) {
						contractorAuditWithStatuses.put(contractorAudit, null);
					} else {
						for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
							if (cao.isVisibleTo(permissions)) {
								contractorAuditWithStatuses.put(contractorAudit, cao.getStatus());
							}
						}
					}
				}
			}
		}
		return contractorAuditWithStatuses;
	}

	public List<ContractorAudit> getActiveAudits() {
		if (contractorNonExpiredAudits == null) {
			contractorNonExpiredAudits = new ArrayList<ContractorAudit>();
			List<ContractorAudit> list = getAudits();
			for (ContractorAudit contractorAudit : list) {
				if (contractorAudit.getAuditType().isPqf() || !contractorAudit.isExpired()) {
					if (contractorAudit.isVisibleTo(permissions))
						contractorNonExpiredAudits.add(contractorAudit);
				}
			}
		}
		return contractorNonExpiredAudits;
	}

	public List<MenuComponent> getMenu() {
		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		MenuComponent item = null;
		getRequestString();

		ContractorRegistrationStep step = ContractorRegistrationStep.getStep(contractor);

		// setup initial registration
		if (step == ContractorRegistrationStep.Register) {
			item = new MenuComponent(getText("ContractorRegistration.title"), "ContractorRegistration.action",
					"conRegisterLink");
			if (requestURL.contains("ContractorRegistration.action")) {
				item.setCurrent(true);
			}
			menu.add(item);
			menu.add(new MenuComponent(getText("ContractorTrades.title"), null, "conTradesLink")); // Trades
			menu.add(new MenuComponent(getText("ContractorRegistrationServices.title"), null, "conServicesLink")); // Services
																													// Performed
			menu.add(new MenuComponent(getText("ContractorFacilities.title"), null, "conFacilitiesLink")); // Facilities
			menu.add(new MenuComponent(getText("ContractorPaymentOptions.title"), null, "conPaymentLink")); // Payment
																											// Options
			menu.add(new MenuComponent(getText("ContractorRegistrationFinish.title"), null, "conConfirmLink")); // Confirm
		} else {
			// setup account editing

			// Edit Details
			item = new MenuComponent(getText("ContractorEdit.title"), "ContractorEdit.action?id=" + id,
					"edit_contractor");
			if (requestURL.contains("ContractorEdit.action"))
				item.setCurrent(true);
			menu.add(item);

			// Trades
			MenuComponent itemTrades = new MenuComponent(getText("ContractorTrades.title"), null, "conTradesLink");
			if (requestURL.contains("ContractorTrades.action")) {
				itemTrades.setCurrent(true);
			}
			menu.add(itemTrades);

			// Services
			MenuComponent itemServices = new MenuComponent(getText("ContractorRegistrationServices.title"), null,
					"conServicesLink");
			if (requestURL.contains("ContractorRegistrationServices.action")) {
				itemServices.setCurrent(true);
			}
			menu.add(itemServices);

			// Facilities
			MenuComponent itemFacilities = new MenuComponent(getText("ContractorFacilities.title"), null,
					"conFacilitiesLink");
			if (requestURL.contains("ContractorFacilities.action")) {
				itemFacilities.setCurrent(true);
			}
			menu.add(itemFacilities);

			// Payment Options
			MenuComponent itemPaymentOptions = new MenuComponent(getText("ContractorPaymentOptions.title"), null,
					"conPaymentLink");
			if (requestURL.contains("ContractorPaymentOptions.action")) {
				itemPaymentOptions.setCurrent(true);
			}
			menu.add(itemPaymentOptions);

			// Confirm
			MenuComponent itemConfirm = new MenuComponent(getText("ContractorRegistrationFinish.title"), null,
					"conConfirmLink");
			if (requestURL.contains("ContractorRegistrationFinish.action")) {
				itemConfirm.setCurrent(true);
			}
			menu.add(itemConfirm);

			// set urls based on step
			switch (step) {
			case Done:
			case Confirmation:
				itemConfirm.setUrl("ContractorRegistrationFinish.action?id=" + id);
			case Payment:
				itemPaymentOptions.setUrl("ContractorPaymentOptions.action?id=" + id);
			case Facilities:
				itemFacilities.setUrl("ContractorFacilities.action?id=" + id);
			case Risk:
				itemServices.setUrl("ContractorRegistrationServices.action?id=" + id);
			default:
				itemTrades.setUrl("ContractorTrades.action?id=" + id);
			}
		}

		// number menu steps
		int counter = 0;
		for (MenuComponent menuItem : menu) {
			counter++;
			menuItem.setName(counter + ") " + menuItem.getName());
		}

		return menu;
	}

	/**
	 * Build a Menu (List<MenuComponent>) with the following:<br>
	 * * PQF<br>
	 * * Annual Update<br>
	 * * InsureGUARD<br>
	 * * Audits<br>
	 * 
	 * @return
	 */
	public List<MenuComponent> getAuditMenu() {
		// PicsLogger.addRuntimeRule("ContractorActionSupport.getAuditMenu");
		PicsLogger.start("ContractorActionSupport.getAuditMenu");

		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		// String checkIcon =
		// "<img src=\"images/okCheck.gif\" border=\"0\" title=\"Complete\"/>";
		Set<ContractorAudit> auditList = getActiveAuditsStatuses().keySet();

		// Sort audits, by throwing them into a tree set and sorting them by
		// display and then name
		TreeSet<ContractorAudit> treeSet = new TreeSet<ContractorAudit>(new Comparator<ContractorAudit>() {
			@Override
			public int compare(ContractorAudit o1, ContractorAudit o2) {
				if (o1 == null || o2 == null)
					return 0; // can't compare null objects

				if (o1.getAuditType().getDisplayOrder() < o2.getAuditType().getDisplayOrder())
					return -1;
				if (o1.getAuditType().getDisplayOrder() > o2.getAuditType().getDisplayOrder())
					return 1;

				if (o1.getAuditType().equals(o2.getAuditType())) {
					if (o1.getAuditFor() != null && o2.getAuditFor() != null) {
						if (o1.getAuditType().isAnnualAddendum())
							// Annual Update 2011 vs Annual Update 2010
							return o2.getAuditFor().compareTo(o1.getAuditFor());
						else
							return o1.getAuditFor().compareTo(o2.getAuditFor());
					} else {
						// Just in case
						return o1.getId() - o2.getId();
					}
				}

				// get display names as seen in menu
				String name1 = getText(o1.getAuditType().getI18nKey("name"));
				String name2 = getText(o2.getAuditType().getI18nKey("name"));
				if (name1 == null || name2 == null)
					// Just in case
					return o1.getId() - o2.getId();

				return name1.compareTo(name2);
			}
		});
		treeSet.addAll(auditList);
		auditList = treeSet;

		PicsLogger.log("Found [" + auditList.size() + "] total active audits");

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add the PQF
			MenuComponent subMenu = new MenuComponent(getText("AuditType.1.name"), "ContractorDocuments.action?id="
					+ id);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().isPqf()) {
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0
							|| audit.getAuditType().getId() == AuditType.IMPORT_PQF) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						childMenu.setUrl("Audit.action?auditID=" + audit.getId());

						// Put Trades menu after 'PQF' menu entry
						if (audit.getAuditType().isPqf()) {
							MenuComponent tradeItem = subMenu.addChild(getText("ContractorTrades.title"),
									"ContractorTrades.action?id=" + id);
							if (contractor != null && !contractor.isNeedsTradesUpdated()
									&& permissions.isOperatorCorporate())
								// Only operators need to see the checkmarks?
								tradeItem.setCssClass("done");
						}
					}
					iter.remove();
				}
			}
			menu.add(subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add the Annual Updates
			MenuComponent subMenu = new MenuComponent(getText("AuditType.11.name"), "ContractorDocuments.action?id="
					+ id + "#" + ContractorDocuments.getSafeName(getText("AuditType.11.name")));
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().isAnnualAddendum()) {
					String linkText = this.getTextParameterized("ContractorActionSupport.Update", audit.getAuditFor());
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						childMenu.setName(linkText);
					}
					iter.remove();
				}
			}

			addSubMenu(menu, subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance)) {
			// Add InsureGUARD
			MenuComponent subMenu = new MenuComponent(getText("global.InsureGUARD"), "ConInsureGUARD.action?id=" + id);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
						&& audit.getOperators().size() > 0) {
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
						String linkText = getText(audit.getAuditType().getI18nKey("name")) + " '" + year;
						childMenu.setName(linkText);
						childMenu.setUrl("Audit.action?auditID=" + audit.getId());
					}
					iter.remove();
				}
			}

			if (subMenu.getChildren().size() > 0) {
				subMenu.addChild(getText("ContractorActionSupport.ManageCertificates"), "ConInsureGUARD.action?id=" + contractor.getId());

				if (permissions.hasPermission(OpPerms.AuditVerification))
					subMenu.addChild(getText("ContractorActionSupport.InsuranceVerification"),
							"InsureGuardVerification.action?id=" + contractor.getId());

				addSubMenu(menu, subMenu);
			}
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add Integrity Management
			MenuComponent subMenu = new MenuComponent("IM", "ContractorDocuments.action?id=" + id + "#"
					+ ContractorDocuments.getSafeName(getText("AuditType.17.name")));
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.IM) && audit.getOperators().size() > 0) {
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						String linkText = getText(audit.getAuditType().getI18nKey("name"))
								+ (audit.getAuditFor() == null ? "" : " " + audit.getAuditFor());
						childMenu.setName(linkText);
					}
					iter.remove();
				}
			}
			addSubMenu(menu, subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) { // Add
			// All Other Audits
			MenuComponent subMenu = new MenuComponent(getText("global.AuditGUARD"), "ContractorDocuments.action?id="
					+ id + "#" + ContractorDocuments.getSafeName(getText("global.AuditGUARD")));
			for (ContractorAudit audit : auditList) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Audit)) {
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);

						String year = DateBean.format(audit.getEffectiveDateLabel(), "yy");
						String linkText = getText(audit.getAuditType().getI18nKey("name")) + " '" + year;
						if (!Strings.isEmpty(audit.getAuditFor()))
							linkText = audit.getAuditFor() + " " + linkText;
						childMenu.setName(linkText);
					}
				}
			}
			addSubMenu(menu, subMenu);
		}
		PicsLogger.stop();
		resetActiveAudits();
		return menu;
	}

	private void addSubMenu(List<MenuComponent> menu, MenuComponent subMenu) {
		if (subMenu.getChildren().size() > 0) {
			PicsLogger.log("Found [" + subMenu.getChildren().size() + "] " + subMenu.getName()
					+ (subMenu.getChildren().size() == 1 ? "" : "s"));
			menu.add(subMenu);
		}
	}

	private MenuComponent createMenuItem(MenuComponent subMenu, ContractorAudit audit) {
		String linkText = getText(audit.getAuditType().getI18nKey("name"));

		MenuComponent menuItem = subMenu.addChild(linkText, "Audit.action?auditID=" + audit.getId());
		menuItem.setAuditId(audit.getId());
		if (isShowCheckIcon(audit))
			menuItem.setCssClass("done");

		return menuItem;
	}

	/**
	 * TODO: Find out if this comment is useful. Is the method it is for missing. Only show the COR/SECOR link for
	 * contractors who have answered Yes to that question and linked to an operator that subscribes to COR
	 */

	public boolean isShowHeader() {
		if (permissions.isContractor())
			return true;
		if (!permissions.hasPermission(OpPerms.ContractorDetails))
			return false;
		if (permissions.isOperator())
			return isCheckPermissionForOperator();
		if (permissions.isCorporate())
			return isCheckPermissionForCorporate();
		if (permissions.isOnlyAuditor()) {
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId())
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus().before(AuditStatus.Complete))
							return true;
					}
			}
			return false;
		}
		return true;
	}

	public boolean isCheckPermissionForOperator() {
		for (ContractorOperator operator : getOperators())
			if (operator.getOperatorAccount().getId() == permissions.getAccountId())
				return true;

		return false;
	}

	public boolean isCheckPermissionForCorporate() {
		for (ContractorOperator co : getOperators()) {
			int opID = co.getOperatorAccount().getId();
			if (permissions.getOperatorChildren().contains(opID))
				return true;
		}
		return false;
	}

	// TODO change this to List<OperatorAccount> instead or figure out why we're
	// getting an exception on isRequiresInsurance()

	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = accountDao.findOperators(contractor, permissions, " AND type IN ('Operator')");
		return operators;
	}

	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = accountDao.findOperators(contractor, permissions,
					" AND status IN ('Active','Demo') AND type IN ('Operator')");
		return activeOperators;
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "", permissions);
	}

	/**
	 * 
	 * @return a list of the certificates, if the user is an operator/corporate then this does the appropriate checking
	 *         to remove the certs that they shouldn't be able to see
	 */
	@SuppressWarnings("deprecation")
	public List<Certificate> getCertificates() {
		if (certificates == null)
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, true);

		if (permissions.isOperatorCorporate()) {
			int topID = permissions.getTopAccountID();
			OperatorAccount opAcc = operatorDAO.find(topID);

			List<Integer> allowedList = new ArrayList<Integer>();
			List<Integer> certIds = new ArrayList<Integer>();
			allowedList = opAcc.getOperatorHeirarchy();

			for (OperatorAccount tmpOp : opAcc.getOperatorChildren())
				allowedList.add(tmpOp.getId());

			for (Certificate cert : certificates)
				certIds.add(cert.getId());

			Map<Integer, List<Integer>> certIdToOp = certificateDAO.findOpsMapByCert(certIds);
			Iterator<Certificate> itr = certificates.iterator();

			while (itr.hasNext()) {
				Certificate c = itr.next();
				int certID = c.getId();

				boolean remove = true;

				if (certIdToOp.get(certID) != null) {
					for (Integer i : certIdToOp.get(certID)) {
						if (allowedList.contains(i)) {
							remove = false;
							break;
						}
					}
				}
				if (remove)
					itr.remove();
			}
		}
		return certificates;
	}

	/**
	 * Get a list of Audits that the current user can see Operators can't see each other's audits Contractors can't see
	 * the Welcome Call This is a bit complicated but needs to look at permissions
	 * 
	 * @return
	 */
	public List<ContractorAudit> getAudits() {

		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		try {
			// Is this ever used? We should just make sure findContractor() has
			// already been called
			if (!accountDao.isContained(contractor))
				findContractor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (ContractorAudit contractorAudit : contractor.getAudits()) {
			if (contractorAudit.isVisibleTo(permissions)) {
				temp.add(contractorAudit);
			}
		}
		return temp;
	}

	public LowMedHigh[] getRiskLevelList() {
		return LowMedHigh.values();
	}

	public boolean isShowCheckIcon(ContractorAudit conAudit) {
		AuditStatus status = getActiveAuditsStatuses().get(conAudit);
		if (status == null)
			return false;
		if (status.after(AuditStatus.Resubmitted))
			return true;
		return false;
	}

	public ContractorRegistrationStep getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(ContractorRegistrationStep currentStep) {
		this.currentStep = currentStep;
	}

	public ContractorRegistrationStep getRegistrationStep() {
		return ContractorRegistrationStep.getStep(contractor);
	}

	/**
	 * We're assuming that the ording in the enum is the standard order of contractor registration.
	 * 
	 * @return Previous ContractorRegistrationStep, according to the ContractorRegistrationStep enum order
	 */
	public ContractorRegistrationStep getPreviousRegistrationStep() {
		if (currentStep != null && currentStep.isHasPrevious())
			return ContractorRegistrationStep.values()[currentStep.ordinal() - 1];

		return null;
	}

	/**
	 * @return Next ContractorRegistrationStep, according to the ContractorRegistrationStep enum order
	 */
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (currentStep != null && currentStep.isHasNext())
			return ContractorRegistrationStep.values()[currentStep.ordinal() + 1];

		return null;
	}

	public String previousStep() throws Exception {
		findContractor();
		redirect(getPreviousRegistrationStep().getUrl(contractor.getId()));
		return SUCCESS;
	}

	public String nextStep() throws Exception {
		findContractor();
		if (getNextRegistrationStep() != null)
			redirect(getNextRegistrationStep().getUrl(contractor.getId()));
		return SUCCESS;
	}

	public Map<ContractorTrade, String> getTradeCssMap() {
		if (tradeCssMap == null) {
			/**
			 * the power to raise the activityPercent. Larger numbers mean that 9s (most of the time) are less prone to
			 * dilution when other trades are added
			 */
			final float factor = 1.8f;

			tradeCssMap = new HashMap<ContractorTrade, String>();
			int sumTrades = 0;
			for (ContractorTrade trade : contractor.getTrades()) {
				sumTrades += (int) Math.round(Math.pow(trade.getActivityPercent(), factor));
			}

			// assign style mappings
			for (ContractorTrade trade : contractor.getTrades()) {
				int activityPercent = (int) Math.round(Math.pow(trade.getActivityPercent(), factor));

				int tradePercent = Math.round(10f * activityPercent / sumTrades);

				switch (trade.getActivityPercent()) {
				case 1:
					tradePercent = cap(tradePercent, 1, 6);
					break;
				case 3:
					tradePercent = cap(tradePercent, 2, 7);
					break;
				case 5:
					tradePercent = cap(tradePercent, 3, 8);
					break;
				case 7:
					tradePercent = cap(tradePercent, 4, 9);
					break;
				case 9:
					tradePercent = cap(tradePercent, 5, 10);
					break;

				default:
					tradePercent = cap(tradePercent, 1, 10);
				}
				tradeCssMap.put(trade, "" + tradePercent);

			}
		}

		return tradeCssMap;
	}

	private int cap(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

}
