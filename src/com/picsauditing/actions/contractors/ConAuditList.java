package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ConAuditList extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private int selectedAudit;
	private int selectedOperator;
	private List<AuditType> auditTypeName;

	public ConAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

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

		return SUCCESS;
	}

	// TODO Move the security into findbyContractor
	public List<ContractorAudit> getAudits() {
		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		List<ContractorAudit> list = auditDao.findByContractor(id);
		for (ContractorAudit contractorAudit : list) {
			if (permissions.canSeeAudit(contractorAudit.getAuditType()))
				temp.add(contractorAudit);
		}
		return temp;
	}

	public List<AuditType> getAuditTypeName() {
		return auditTypeDAO.findAll(permissions, true);
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
}
