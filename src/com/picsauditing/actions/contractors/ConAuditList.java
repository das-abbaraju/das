package com.picsauditing.actions.contractors;

import java.util.List;

import org.apache.struts2.config.Result;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

public class ConAuditList extends ContractorActionSupport {
	public ConAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		return SUCCESS;
	}

	public List<ContractorAudit> getAudits() {
		return auditDao.findByContractor(id);
	}

}
