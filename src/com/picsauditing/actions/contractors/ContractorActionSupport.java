package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.OperatorBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.YesNo;

public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	private List<ContractorAudit> contractorNonExpiredAudits = null;
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;
	private List<ContractorOperator> operators;
	protected boolean limitedView = false;

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
		// OR
		if (permissions.isContractor()) {
			return permissions.getAccountIdString().equals(Integer.toString(this.id));
		}

		if (limitedView)
			// Basically, if all we're doing is searching for contractors
			// and looking at their summary page, then it's OK
			return true;

		else if(!permissions.hasPermission(OpPerms.ContractorDetails)) {
			return false;
		}
		
		if (permissions.hasPermission(OpPerms.AllContractors))
			return true;

		if (permissions.isOperator() || permissions.isCorporate()) {
			// If we want to look at their detail, like PQF data
			// Then we have to add them first (generalContractors).
			if (permissions.isCorporate()) {
				OperatorBean operator = new OperatorBean();
				try {
					operator.isCorporate = true;
					operator.setFromDB(permissions.getAccountIdString());
					// if any of this corporate operators can see this
					// contractor,
					// then the corporate users can see them too
					for (String id : operator.facilitiesAL) {
						for (ContractorOperator corporate : getOperators())
							if (corporate.getOperatorAccount().getIdString().equals(id))
								return true;
					}
				} catch (Exception e) {
				}
				return false;
			}
			// To see anything other than the summary, you need to be on their
			// list
			for (ContractorOperator operator : getOperators())
				if (operator.getOperatorAccount().getIdString().equals(permissions.getAccountIdString()))
					return true;
		}

		for (ContractorAudit audit : getActiveAudits()) {
			if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId())
				if (audit.getAuditStatus().equals(AuditStatus.Pending)
						|| audit.getAuditStatus().equals(AuditStatus.Submitted))
					return true;
		}

		return false;
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
	public boolean isHasInsurance() {
		if (!permissions.isContractor() && !permissions.hasPermission(OpPerms.InsuranceCerts))
			return false;

		for (ContractorOperator insurContractors : getOperators())
			if (insurContractors.getOperatorAccount().getCanSeeInsurance().equals(YesNo.Yes))
				return true;

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
					if ((audit.getAuditStatus().equals(AuditStatus.Pending) || audit.getAuditStatus().equals(
							AuditStatus.Submitted)))
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
}
