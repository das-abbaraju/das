package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
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
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;
	private List<ContractorOperator> operators;
	protected boolean limitedView = false;
	protected List<ContractorOperator> activeOperators;

	// TODO cleanup the PermissionToViewContractor duplicate code here
	private PermissionToViewContractor permissionToViewContractor = null;
	private AuditDataDAO auditDataDAO;

	public ContractorActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}

	@Override
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

	protected void resetActiveAudits() {
		contractorNonExpiredAudits = null;
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
		List<ContractorAudit> auditList = getActiveAudits();

		PicsLogger.log("Found [" + auditList.size() + "] total active audits");

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add the PQF
			List<ContractorAudit> pqfs = new ArrayList<ContractorAudit>();

			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().isPqf()) {
					pqfs.add(audit);
					iter.remove();
				}
			}
			if (pqfs.size() == 1) {
				ContractorAudit audit = pqfs.get(0);
				String url = "Audit.action?auditID=";
				MenuComponent menuComponent = new MenuComponent(audit.getAuditType().getAuditName(), url
						+ audit.getId());
				menuComponent.setAuditId(audit.getId());
				menu.add(menuComponent);
			} else if (pqfs.size() > 1) {
				MenuComponent subMenu = new MenuComponent("PQF", "ContractorDocuments.action?id=" + id + "#PQF");
				menu.add(subMenu);
				for (ContractorAudit audit : pqfs) {
					createMenuItem(subMenu, audit);
				}
			}
			PicsLogger.log("Found [" + pqfs.size() + "] PQFs");
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add the Annual Updates
			MenuComponent subMenu = new MenuComponent("Annual Update", "ContractorDocuments.action?id=" + id + "#AU");
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().isAnnualAddendum()) {
					String linkText = audit.getAuditFor() + " Update";
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					childMenu.setName(linkText);
					childMenu.setSortField(linkText);
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
			MenuComponent subMenu = new MenuComponent("InsureGUARD&trade;", "ConInsureGUARD.action?id=" + id);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
						&& audit.getOperators().size() > 0) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String year = DateBean.format(audit.getCreationDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;
					childMenu.setName(linkText);
					childMenu.setUrl("Audit.action?auditID=" + audit.getId());
					iter.remove();
				}
			}
			addSubMenu(menu, subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add Integrity Management
			MenuComponent subMenu = new MenuComponent("IM", "ContractorDocuments.action?id=" + id + "#IM");
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.IM) && audit.getOperators().size() > 0) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String linkText = audit.getAuditType().getAuditName()
							+ (audit.getAuditFor() == null ? "" : " " + audit.getAuditFor());
					childMenu.setName(linkText);
					iter.remove();
				}
			}
			addSubMenu(menu, subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) {
			// Add COR/SECOR
			MenuComponent subMenu = new MenuComponent("COR/SECOR", "ContractorDocuments.action?id=" + id + "#COR");
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if ((audit.getAuditType().getId() == AuditType.COR || audit.getAuditType().getId() == AuditType.SUPPLEMENTCOR)
						&& audit.getOperators().size() > 0) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String linkText = audit.getAuditType().getAuditName()
							+ (audit.getAuditFor() == null ? "" : " " + audit.getAuditFor());
					childMenu.setName(linkText);
					iter.remove();
				}
			}
			addSubMenu(menu, subMenu);
		}

		if (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety)) { // Add
			// All Other Audits
			MenuComponent subMenu = new MenuComponent("Audits", "ContractorDocuments.action?id=" + id + "#Audit");
			for (ContractorAudit audit : auditList) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Audit)) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);

					String year = DateBean.format(audit.getCreationDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;
					if (!Strings.isEmpty(audit.getAuditFor()))
						linkText = audit.getAuditFor() + " " + linkText;
					childMenu.setName(linkText);
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
		String linkText = audit.getAuditType().getAuditName();

		MenuComponent menuItem = subMenu.addChild(linkText, "Audit.action?auditID=" + audit.getId());
		menuItem.setAuditId(audit.getId());
		if(!permissions.isAdmin()){
			if (isShowCheckIcon(audit))
				menuItem.setCssClass("done");
		}
		return menuItem;
	}

	/**
	 * Only show the insurance link for contractors who are linked to an
	 * operator that collects insurance data. Also, don't show the link to users
	 * who don't have the InsuranceCerts permission.
	 * 
	 */
	public boolean isRequiresInsurance() {
		if (contractor.isAcceptsBids())
			return false;

		if (!accountDao.isContained(getOperators().iterator().next()))
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
	 * Only show the COR/SECOR link for contractors who have answered Yes to
	 * that question and linked to an operator that subscribes to COR
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
	 * Get a list of Audits that the current user can see Operators can't see
	 * each other's audits Contractors can't see the Welcome Call This is a bit
	 * complicated but needs to look at permissions
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
		Set<AuditType> aTypes = new HashSet<AuditType>();

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
		// TODO I'm really not sure how we should handle these check marks
		// anymore. This needs serious review
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (permissions.isContractor()) {
				if (conAudit.getAuditType().isCanContractorEdit()) {
					if (cao.getStatus().before(AuditStatus.Complete))
						return false;
				} else if (conAudit.getAuditType().getWorkFlow().isHasSubmittedStep() && cao.getStatus().isSubmitted())
					return true;
			} else if (cao.getStatus().after(AuditStatus.Pending))
				return true;
			return false;
		}
		return true;
	}
}
