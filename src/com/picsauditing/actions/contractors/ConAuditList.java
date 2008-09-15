package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.config.Result;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

public class ConAuditList extends ContractorActionSupport {
	R
	private boolean manuallyAdded;	

	public ConAuditList(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

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

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}

}
