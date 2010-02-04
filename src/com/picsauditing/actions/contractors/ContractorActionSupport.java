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
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
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
				if (contractorAudit.getAuditType().isPqf() || !contractorAudit.getAuditStatus().isExpired())
					contractorNonExpiredAudits.add(contractorAudit);
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

		if(!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))
		{			
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
				MenuComponent subMenu = new MenuComponent("PQF", "ConPqfList.action?id=" + id);
				menu.add(subMenu);
				for (ContractorAudit audit : pqfs) {
					createMenuItem(subMenu, audit);
				}
			}
			PicsLogger.log("Found [" + pqfs.size() + "] PQFs");
		}

		if(!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))
		{ // Add the Annual Updates
			MenuComponent subMenu = new MenuComponent("Annual Update", "ConAnnualUpdates.action?id=" + id);
			menu.add(subMenu);
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
			PicsLogger.log("Found [" + subMenu.getChildren() + "] Annual Updates");
		}

		if (isRequiresInsurance() && (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorInsurance))) {
			// Add InsureGUARD
			MenuComponent subMenu = new MenuComponent("InsureGUARD&trade;", "ConInsureGUARD.action?id=" + id);
			menu.add(subMenu);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
						&& !audit.getAuditStatus().equals(AuditStatus.Exempt)) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String year = DateBean.format(audit.getEffectiveDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;
					childMenu.setName(linkText);
					childMenu.setUrl("AuditCat.action?auditID=" + audit.getId() + "&catDataID="
							+ audit.getCategories().get(0).getId());
					iter.remove();
				}
			}
			PicsLogger.log("Found [" + subMenu.getChildren() + "] Policies");
		}

		if (isRequiresIntegrityManagement() && (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))) {
			// Add Integrity Management
			MenuComponent subMenu = new MenuComponent("IM", "ConIntegrityManagement.action?id=" + id);
			menu.add(subMenu);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.IM)
						&& !audit.getAuditStatus().equals(AuditStatus.Exempt)) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String linkText = audit.getAuditType().getAuditName()
							+ (audit.getAuditFor() == null ? "" : " " + audit.getAuditFor());
					childMenu.setName(linkText);
					iter.remove();
				}
			}
			PicsLogger.log("Found [" + subMenu.getChildren() + "] IM Audits");
		}

		if (isRequiresCOR() && (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))) {
			// Add COR/SECOR
			MenuComponent subMenu = new MenuComponent("COR/SECOR", "ConCorAuditList.action?id=" + id);
			menu.add(subMenu);
			Iterator<ContractorAudit> iter = auditList.iterator();
			while (iter.hasNext()) {
				ContractorAudit audit = iter.next();
				if (audit.getAuditType().getId() == AuditType.COR && !audit.getAuditStatus().equals(AuditStatus.Exempt)) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);
					String linkText = audit.getAuditType().getAuditName()
							+ (audit.getAuditFor() == null ? "" : " " + audit.getAuditFor());
					childMenu.setName(linkText);
					iter.remove();
				}
			}
			PicsLogger.log("Found [" + subMenu.getChildren() + "] COR Audits");
		}

		if(!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))
		{ // Add All Other Audits
			MenuComponent subMenu = new MenuComponent("Audits", "ConAuditList.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : auditList) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Audit)) {
					MenuComponent childMenu = createMenuItem(subMenu, audit);

					String year = DateBean.format(audit.getEffectiveDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;
					if (!Strings.isEmpty(audit.getAuditFor()))
						linkText = audit.getAuditFor() + " " + linkText;
					childMenu.setName(linkText);
				}
			}
			PicsLogger.log("Found [" + subMenu.getChildren() + "] Other Audits");
		}
		PicsLogger.stop();
		return menu;
	}

	private MenuComponent createMenuItem(MenuComponent subMenu, ContractorAudit audit) {
		String linkText = audit.getAuditType().getAuditName();

		MenuComponent menuItem = subMenu.addChild(linkText, "Audit.action?auditID=" + audit.getId());
		menuItem.setAuditId(audit.getId());
		menuItem.setTitle(audit.getAuditStatus().toString());
		if (isShowCheckIcon(audit))
			menuItem.setCssClass("done");
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
	 * Only show the Integrity Management link for contractors who are linked to
	 * an operator that subscribes to Integrity Management
	 */
	public boolean isRequiresIntegrityManagement() {
		if (contractor.isAcceptsBids())
			return false;

		if (!accountDao.isContained(getOperators().iterator().next()))
			operators = null;

		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				for (AuditOperator audit : op.getVisibleAudits()) {
					if (audit.getAuditType().getClassType() == AuditTypeClass.IM
							&& permissions.getAccountId() == op.getId()) {
						return true;
					}
				}

			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my
		// facilities
		for (ContractorOperator insurContractors : getOperators()) {
			OperatorAccount op = insurContractors.getOperatorAccount();
			for (AuditOperator audit : op.getVisibleAudits()) {
				if (audit.getAuditType().getClassType() == AuditTypeClass.IM) {
					return true;
				}
			}
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

	public boolean isRequiresCOR() {
		boolean hasCOR = false;
		if (!accountDao.isContained(getOperators().iterator().next()))
			operators = null;

		if (contractor.isCOR(getAuditDataDAO())) {
			hasCOR = true;
		}

		if (hasCOR) {
			for (ContractorOperator corContractors : getOperators()) {
				OperatorAccount op = corContractors.getOperatorAccount();
				for (AuditOperator audit : op.getVisibleAudits()) {
					if (audit.getAuditType().getId() == AuditType.COR) {
						return true;
					}
				}
			}
		}
		return false;
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
					if (audit.getAuditStatus().isPendingSubmitted())
						return true;
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
			operators = accountDao.findOperators(contractor, permissions, "");
		return operators;
	}

	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = accountDao.findOperators(contractor, permissions, " AND operatorAccount.status IN ('Active','Demo') ");
		return activeOperators;
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "", permissions);
	}

	/**
	 * Get a list of Audits that the current user can see Operators can't see
	 * each other's audits Contractors can't see the Welcome Call
	 * 
	 * @return
	 */
	public List<ContractorAudit> getAudits() {
		// Why is this method so complicated? Seems like it could be simpler
		// Like this: return contractor.getAudits()

		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		try {
			if (!accountDao.isContained(contractor))
				findContractor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<ContractorAudit> list = contractor.getAudits();
		Set<AuditType> aTypes = new HashSet<AuditType>();

		if (permissions.isContractor()) {
			for (ContractorOperator conOperator : contractor.getOperators()) {
				for (AuditOperator aOperator : conOperator.getOperatorAccount().getVisibleAudits()) {
					aTypes.add(aOperator.getAuditType());
				}
			}
		}

		for (ContractorAudit contractorAudit : list) {
			if (permissions.canSeeAudit(contractorAudit.getAuditType())) {
				if (permissions.isContractor()) {
					for (AuditType auditType : aTypes) {
						if (auditType.equals(contractorAudit.getAuditType())) {
							temp.add(contractorAudit);
							break;
						}
					}
				} else
					temp.add(contractorAudit);
			}
		}
		return temp;
	}

	public LowMedHigh[] getRiskLevelList() {
		return LowMedHigh.values();
	}

	public boolean isShowCheckIcon(ContractorAudit conAudit) {
		if (permissions.isContractor()) {
			if (conAudit.getAuditStatus().isActive()) {
				return true;
			} else if (!conAudit.getAuditType().isHasRequirements() && conAudit.getAuditStatus().isSubmitted())
				return true;
		} else if (conAudit.getAuditStatus().isActive())
			return true;
		return false;
	}
}
