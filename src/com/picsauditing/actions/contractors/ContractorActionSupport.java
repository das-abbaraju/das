package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;

public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	@Autowired(required=true)
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;

	public ContractorActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}
	
	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();
		
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			throw new Exception("Contractor " + this.id + " not found");
		
		// TODO Check permissions to view this contractor
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
		return auditDao.findNonExpiredByContractor(contractor.getId());
	}
	
	public List<ContractorOperator> getOperators() {
		return accountDao.findOperators(contractor, permissions);
	}
}
