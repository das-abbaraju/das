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
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
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

	// TODO cleanup the PermissionToViewContractor duplicate code here
	private PermissionToViewContractor permissionToViewContractor = null;
	private AuditDataDAO auditDataDAO;

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

				// get display names as seen in menu
				String name1 = getText(o1.getAuditType().getI18nKey("name"));
				String name2 = getText(o2.getAuditType().getI18nKey("name"));
				if (name1 == null || name2 == null)
					return 0; // can't compare names

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
			int count = 0;
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().isPqf()) {
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						childMenu.setUrl("Audit.action?auditID=" + audit.getId());
						count++;

						// Put Trades menu after 'PQF' menu entry
						if (audit.getAuditType().isPqf()) {
							subMenu.addChild(getText("ConctratorTrades.title"), "ContractorTrades.action?id=" + id);
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
					String linkText = audit.getAuditFor() + " Update";
					if (!permissions.isContractor() || audit.getCurrentOperators().size() > 0) {
						MenuComponent childMenu = createMenuItem(subMenu, audit);
						childMenu.setName(linkText);
						childMenu.setSortField(linkText);
					}
					iter.remove();
				}
			}

			try {
				subMenu.sortChildren();
			} catch (Exception e) {
				PicsLogger.log("Failed to sort Annual Updates");
			}
			addSubMenu(menu, subMenu);
		}

		if (isRequiresInsurance()
				&& (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance))) {
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

			subMenu.addChild("Manage Certificates", "ConInsureGUARD.action?id=" + contractor.getId());

			if (permissions.hasPermission(OpPerms.AuditVerification))
				subMenu.addChild("Insurance Verification", "InsureGuardVerification.action?id=" + contractor.getId());

			addSubMenu(menu, subMenu);
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
	 * Only show the insurance link for contractors who are linked to an operator that collects insurance data. Also,
	 * don't show the link to users who don't have the InsuranceCerts permission.
	 * 
	 */
	public boolean isRequiresInsurance() {
		if (contractor.isAcceptsBids())
			return false;

		if (getOperators().iterator().hasNext()) {
			if (!accountDao.isContained(getOperators().iterator().next()))
				operators = null;
		} else
			operators = null;

		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				if (permissions.getAccountId() == op.getId() && op.getCanSeeInsurance().isTrue())
					return true;
			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my
		// facilities
		for (ContractorOperator co : getOperators()) {
			if (co.getOperatorAccount().getCanSeeInsurance().isTrue())
				return true;
		}
		return false;
	}

	/**
	 * Only show the COR/SECOR link for contractors who have answered Yes to that question and linked to an operator
	 * that subscribes to COR
	 */

	protected AuditDataDAO getAuditDataDAO() {
		if (auditDataDAO == null)
			auditDataDAO = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
		return auditDataDAO;
	}

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
	
	public ContractorRegistrationStep getRegistrationStep() {
		return ContractorRegistrationStep.getStep(contractor);
	}
}
