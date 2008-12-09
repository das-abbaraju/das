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

	public List<MenuComponent> getAuditMenu() {
		// Figure out which auditTypes are duplicate and which are not
		Map<AuditType, List<ContractorAudit>> audits = new TreeMap<AuditType, List<ContractorAudit>>();
		
		// Take a list of A,B,B,C and convert it into (A)(B,B)(C)
		for(ContractorAudit audit : getActiveAudits()) {
			if (!audit.equals(AuditStatus.Exempt)) {
				AuditType t = audit.getAuditType();
				if (!audits.containsKey(t))
					audits.put(t, new ArrayList<ContractorAudit>());
				audits.get(t).add(audit);
			}
		}
		
		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		String url = "Audit.action?auditID=";
		for(AuditType t : audits.keySet()) {
			List<ContractorAudit> auditList = audits.get(t);
			ContractorAudit conAudit = auditList.get(0);
			if (auditList.size() == 1) {
				// Just one audit of this type
				menu.add(new MenuComponent(conAudit.getAuditType().getAuditName(), url + conAudit.getId()));
			} else {
				// We have more than one audit of this type, so create a subMenu with multiple children
				MenuComponent subMenu = new MenuComponent(conAudit.getAuditType().getAuditName(), ""+ conAudit.getAuditType().getAuditTypeID());
				menu.add(subMenu);
				
				for(ContractorAudit audit : auditList) {
					// Create the linkText
					// First use auditFor: Year in the cast of Annual Audit or Employee name in the case of OQ
					String linkText = audit.getAuditFor();
					
					// If the audit is a desktop or office, we may want to add in pending status
					if (Strings.isEmpty(linkText) && audit.getAuditStatus().isPendingSubmittedResubmitted())
						linkText = audit.getAuditStatus().toString();
					
					// When all else fails, make sure there is something displayed
					if (Strings.isEmpty(linkText))
						linkText = audit.getAuditType().getAuditName();
					else
						linkText += " " +audit.getAuditType().getAuditName();
					
					subMenu.addChild(linkText, url + audit.getId());
				}
			}
		}
		return menu;
	}

	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = accountDao.findOperators(contractor, permissions, "");
		return operators;
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
		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				if (permissions.getAccountId() == op.getId() && op.getCanSeeInsurance().equals(YesNo.Yes))
					return true;
			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my facilities
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
			}
			else if (permissions.isCorporate()) {
				for(ContractorOperator co :  getOperators()) {
					if (co.getOperatorAccount().equals(certificate.getOperatorAccount())) {
						count++;
					}
				}
			}
			else {
				// Admins and contractors can see all the certs for this contractor
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
	
	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = accountDao.findOperators(contractor, permissions, " AND operatorAccount.active = 'Y' ");
		return activeOperators;
	}


}
