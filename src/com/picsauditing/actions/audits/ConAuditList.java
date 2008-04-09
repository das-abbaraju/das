package com.picsauditing.actions.audits;

import java.util.List;

import org.apache.struts2.config.Result;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@Result(name = "success", value = "contractor_audits.jsp")
public class ConAuditList extends ContractorActionSupport {
	protected List<ContractorAudit> audits;
	protected ContractorAuditDAO auditDao;

	public ConAuditList(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}

	public String execute() throws Exception {
		findContractor();
		audits = auditDao.findByContractor(id);

		return SUCCESS;
	}

	public List<ContractorAudit> getAudits() {
		return audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

}
