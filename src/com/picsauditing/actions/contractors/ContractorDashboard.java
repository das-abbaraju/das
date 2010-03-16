package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private OperatorAccountDAO operatorDAO;

	public ContractorDashboard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDAO) {
		super(accountDao, auditDao);
		this.operatorDAO = operatorDAO;
		this.subHeading = "Contractor Dashboard";
	}

	@Override
	public String execute() throws Exception {
		return super.execute();
	}

}
