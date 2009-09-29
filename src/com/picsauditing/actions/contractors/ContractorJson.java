package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorJson extends ContractorActionSupport {

	public ContractorJson(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		return JSON;
	}

	public String getJson() {
		return contractor.toJSON(true).toString();
	}

}
