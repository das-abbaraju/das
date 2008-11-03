package com.picsauditing.actions.report;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.SpringUtils;

public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addAudit(AuditType.PQF);
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		if (permissions.isOperator())
			sql.addField("flags.waitingOn");
		if (filtered == null)
			filtered = true;

		return super.execute();
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}

	public static boolean isInsuranceApproved(int id) {
		ContractorAccountDAO contractorAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		ContractorAccount cAccount = contractorAccountDAO.find(id);
		for (Certificate certificate : cAccount.getCertificates()) {
			if (certificate.getStatus().equals("Approved")) {
				return true;
			}
		}
		return false;
	}
}
