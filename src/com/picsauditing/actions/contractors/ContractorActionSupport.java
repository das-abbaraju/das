package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.OperatorBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.PermissionToViewContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	private List<ContractorAudit> contractorNonExpiredAudits = null;
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;
	private List<ContractorOperator> operators;
	protected boolean limitedView = false;
	protected List<ContractorOperator> activeOperators;

	private PermissionToViewContractor permissionToViewContractor = null;

	protected String subHeading;

	public ContractorActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}

	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		contractor = accountDao.find(id);
		if (contractor == null)
			throw new Exception("Contractor " + this.id + " not found");

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public List<ContractorAudit> getActiveAudits() {
		if (contractorNonExpiredAudits == null) {
			contractorNonExpiredAudits = new ArrayList<ContractorAudit>();
			List<ContractorAudit> list = auditDao.findNonExpiredByContractor(contractor.getId());
			for (ContractorAudit contractorAudit : list) {
				if (permissions.canSeeAudit(contractorAudit.getAuditType()))
					contractorNonExpiredAudits.add(contractorAudit);
			}
		}
		return contractorNonExpiredAudits;
	}

	/**
	 * Build a Menu (List<MenuComponent>) with the following:<br> * PQF<br> *
	 * Annual Update<br> * InsureGuard<br> * Audits<br>
	 * 
	 * @return
	 */
	public List<MenuComponent> getAuditMenu() {
		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		String url = "Audit.action?auditID=";

		// Add the PQF
		for (ContractorAudit audit : getActiveAudits()) {
			if (audit.getAuditType().isPqf()) {
				MenuComponent menuComponent = new MenuComponent("PQF", url + audit.getId());
				menuComponent.setAuditId(audit.getId());
				menu.add(menuComponent);
			}
		}

		{ // Add the Annual Updates
			MenuComponent subMenu = new MenuComponent("Annual Update", "ConAnnualUpdates.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().isAnnualAddendum()) {
					String linkText = audit.getAuditFor() + " Update";
					subMenu.addChild(linkText, url + audit.getId(), audit.getId());
				}
			}
		}

		if (isRequiresInsurance()) {
			// Add InsureGuard
			MenuComponent subMenu = new MenuComponent("InsureGuard", "ConInsureGuard.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
						&& !audit.equals(AuditStatus.Exempt)) {
					String linkText = buildLinkText(audit);
					subMenu.addChild(linkText, url + audit.getId(), audit.getId());
				}
			}
		}

		{ // Add All Other Audits
			MenuComponent subMenu = new MenuComponent("Audits", "ConAuditList.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Audit) && !audit.getAuditType().isPqf()
						&& !audit.getAuditType().isAnnualAddendum()) {
					String linkText = buildLinkText(audit);
					subMenu.addChild(linkText, url + audit.getId(), audit.getId());
				}
			}
		}
		return menu;
	}

	public String buildLinkText(ContractorAudit audit) {
		// Create the linkText
		// First use auditFor: Year in the cast of Annual Audit or Employee name
		// in the case of OQ
		String linkText = audit.getAuditFor();

		// If the audit is a desktop or office, we may want to add in pending
		// status
		if (Strings.isEmpty(linkText) && audit.getAuditStatus().isPendingSubmittedResubmitted())
			linkText = audit.getAuditStatus().toString();

		// When all else fails, make sure there is something displayed
		if (Strings.isEmpty(linkText))
			linkText = audit.getAuditType().getAuditName();
		else
			linkText += " " + audit.getAuditType().getAuditName();
		return linkText;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	/**
	 * Only show the insurance link for contractors who are linked to an
	 * operator that collects insurance data. Also, don't show the link to users
	 * who don't have the InsuranceCerts permission.
	 * 
	 */
	public boolean isRequiresInsurance() {
		if (!accountDao.isContained(getOperators().iterator().next()))
			operators = null;

		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				if (permissions.getAccountId() == op.getId() && op.getCanSeeInsurance().equals(YesNo.Yes))
					return true;
			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my
		// facilities
		for (ContractorOperator insurContractors : getOperators()) {
			OperatorAccount op = insurContractors.getOperatorAccount();
			if (op.getCanSeeInsurance().equals(YesNo.Yes))
				return true;
		}
		return false;
	}

	/**
	 * Only show the insurance link for contractors who are linked to an
	 * operator that collects insurance data. Also, don't show the link to users
	 * who don't have the InsuranceCerts permission.
	 * 
	 */
	public int getInsuranceCount() {
		int count = 0;

		for (Certificate certificate : contractor.getCertificates()) {
			if (permissions.isOperator()) {
				if (permissions.getAccountId() == certificate.getOperatorAccount().getId())
					count++;
			} else if (permissions.isCorporate()) {
				for (ContractorOperator co : getOperators()) {
					if (co.getOperatorAccount().equals(certificate.getOperatorAccount())) {
						count++;
					}
				}
			} else {
				// Admins and contractors can see all the certs for this
				// contractor
				count++;
			}
		}
		return count;
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
		OperatorBean operator = new OperatorBean();
		try {
			operator.isCorporate = true;
			operator.setFromDB(permissions.getAccountIdString());
			for (String id : operator.facilitiesAL) {
				for (ContractorOperator corporate : getOperators())
					if (corporate.getOperatorAccount().getIdString().equals(id))
						return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	// TODO change this to List<OperatorAccount> instead or figure out why we're
	// getting an expection on isRequiresInsurance()

	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = accountDao.findOperators(contractor, permissions, "");
		return operators;
	}

	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = accountDao.findOperators(contractor, permissions, " AND operatorAccount.active = 'Y' ");
		return activeOperators;
	}
	
	public List<ContractorAudit> getAudits() {
		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		List<ContractorAudit> list = auditDao.findByContractor(id);
		for (ContractorAudit contractorAudit : list) {
			if (permissions.canSeeAudit(contractorAudit.getAuditType()))
				temp.add(contractorAudit);
		}
		return temp;
	}
}
