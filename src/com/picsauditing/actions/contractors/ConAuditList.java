package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ConAuditList extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private int selectedAudit;
	private int selectedOperator;
	private List<AuditType> auditTypeList;
	private AuditTypeClass auditClass = AuditTypeClass.Audit;
	public List<ContractorAudit> upComingAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> currentAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> expiredAudits = new ArrayList<ContractorAudit>();

	public ConAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		for (ContractorAudit contractorAudit : getAudits()) {
			// Only show Insurance policies or audits
			if (contractorAudit.getAuditStatus().isPendingSubmitted())
				upComingAudits.add(contractorAudit);
			else if (contractorAudit.getAuditStatus().isActiveResubmittedExempt())
				currentAudits.add(contractorAudit);
			else if (contractorAudit.getAuditStatus().equals(AuditStatus.Expired))
				expiredAudits.add(contractorAudit);
			else {
				// There shouldn't be any others
			}
		}

		if (button != null && button.equals("Create")) {
			boolean alreadyExists = false;
			if (permissions.isOperator() || permissions.isCorporate())
				selectedOperator = permissions.getAccountId();

			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (conAudit.getRequestingOpAccount() != null
						&& conAudit.getRequestingOpAccount().getId() == selectedOperator
						&& conAudit.getAuditType().getAuditTypeID() == selectedAudit)
					alreadyExists = true;
			}
			if (alreadyExists) {
				addActionError("Audit already exists");
				return SUCCESS;
			}
			ContractorAudit conAudit = new ContractorAudit();
			conAudit.setAuditType(new AuditType());
			conAudit.getAuditType().setAuditTypeID(selectedAudit);
			conAudit.setContractorAccount(contractor);
			conAudit.setCreatedDate(new Date());
			conAudit.setAuditStatus(AuditStatus.Pending);
			if (selectedOperator != 0) {
				conAudit.setRequestingOpAccount(new OperatorAccount());
				conAudit.getRequestingOpAccount().setId(selectedOperator);
			}
			conAudit.setPercentComplete(0);
			conAudit.setPercentVerified(0);
			conAudit.setManuallyAdded(true);
			auditDao.save(conAudit);
			return "saved";
		}
		auditTypeList = auditTypeDAO.findAll(permissions, true, auditClass);
		return SUCCESS;
	}

	public List<AuditType> getAuditTypeName() {
		return auditTypeList;
	}

	public int getSelectedAudit() {
		return selectedAudit;
	}

	public void setSelectedAudit(int selectedAudit) {
		this.selectedAudit = selectedAudit;
	}

	public int getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(int selectedOperator) {
		this.selectedOperator = selectedOperator;
	}

	public List<ContractorAudit> getUpComingAudits() {
		return upComingAudits;
	}

	public List<ContractorAudit> getCurrentAudits() {
		return currentAudits;
	}

	public List<ContractorAudit> getExpiredAudits() {
		return expiredAudits;
	}

	public boolean isManuallyAddAudit() {
		if (permissions.isContractor()) {
			if (auditClass.equals(AuditTypeClass.Policy))
				return true;
			return false;
		}

		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)
				|| permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Edit))
			return true;
		if (permissions.isOperator() || permissions.isCorporate()) {
			if (auditTypeList.size() > 0)
				return true;
		}
		return false;
	}

	public AuditTypeClass getAuditClass() {
		return auditClass;
	}

	public void setAuditClass(AuditTypeClass auditClass) {
		this.auditClass = auditClass;
	}

}
