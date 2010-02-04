package com.picsauditing.actions.report;

import java.util.Date;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportContractorLicenses extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorLicenseReport);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addWhere("ca.auditTypeID = 1"); // PQF
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addPQFQuestion(401);
		sql.addPQFQuestion(755);
		sql.addField("q401.dateVerified AS dateVerified401");
		sql.addField("q755.dateVerified AS dateVerified755");
		sql.addField("q401.comment AS comment401");
		sql.addField("q755.comment AS comment755");
		
		orderByDefault = "a.name";
		
		if (getFilter().isConExpiredLic()) {
			sql.addWhere("q755.answer < '" + DateBean.format(new Date(), "yyyy-MM-dd") + "'");
		}
		if (getFilter().getValidLicense().equals("Valid"))
			sql.addWhere("q401.dateVerified IS NOT NULL");
		if (getFilter().getValidLicense().equals("UnValid"))
			sql.addWhere("q401.dateVerified = NULL");
		if (getFilter().getValidLicense().equals("All"))
			sql.addWhere("1");

		getFilter().setShowAuditType(false);
		getFilter().setShowConLicense(true);
		getFilter().setShowExpiredLicense(true);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowCcOnFile(false);
	}
}
