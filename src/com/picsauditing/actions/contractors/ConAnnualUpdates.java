package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.comparators.ContractorAuditComparator;

@SuppressWarnings("serial")
public class ConAnnualUpdates extends ContractorActionSupport {
	public List<ContractorAudit> annualAddendums = new ArrayList<ContractorAudit>();

	public ConAnnualUpdates(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		for (ContractorAudit contractorAudit : getAudits()) {
			if (contractorAudit.getAuditType().getClassType().isAnnualUpdate()) {
				annualAddendums.add(contractorAudit);
			}
		}

		return SUCCESS;
	}

	public List<ContractorAudit> getAnnualAddendums() {
		return annualAddendums;
	}

	public Comparator<ContractorAudit> getDateComparator() {
		return new ContractorAuditComparator("auditFor -1");
	}
}
