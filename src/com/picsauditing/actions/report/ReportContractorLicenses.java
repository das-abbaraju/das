package com.picsauditing.actions.report;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportContractorLicenses extends ReportContractorAudits {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ContractorLicenseReport);
		sql.addWhere("ca.auditTypeID = 1"); // PQF
		sql.addWhere("a.active = 'Y'");
		sql.addPQFQuestion(401);
		sql.addPQFQuestion(755);
		sql.addField("q401.verifiedAnswer AS verifiedAnswer401");
		sql.addField("q401.isCorrect AS isCorrect401");
		sql.addField("q755.verifiedAnswer AS verifiedAnswer755");
		sql.addField("q755.isCorrect AS isCorrect755");
		sql.addField("q401.comment AS comment401");
		sql.addField("q755.comment AS comment755");
		setOrderBy("a.name");
		if (getFilter().isConExpiredLic()) {
			sql.addWhere("q755.verifiedAnswer < '" + DateBean.format(new Date(), "yyyy-MM-dd") + "'");
		}
		if (getFilter().getValidLicense().equals("Valid"))
			sql.addWhere("q401.isCorrect = 'Yes'");
		if (getFilter().getValidLicense().equals("UnValid"))
			sql.addWhere("q401.isCorrect <> 'Yes' OR q401.isCorrect IS NULL");
		if (getFilter().getValidLicense().equals("All"))
			sql.addWhere("1");

		if (filtered == null)
			filtered = false;

		getFilter().setShowAuditType(false);
		getFilter().setShowConLicense(true);
		getFilter().setShowExpiredLicense(true);

		return super.execute();
	}
}
