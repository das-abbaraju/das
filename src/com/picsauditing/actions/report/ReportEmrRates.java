package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ReportEmrRates extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.EMRReport);
		super.checkPermissions();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		getFilter().setShowEmrRange(true);
		getFilter().setShowAuditFor(true);
		
		sql.addJoin("JOIN pqfdata d ON d.auditID = ca.auditID");
		sql.addField("d.answer");

		sql.addWhere("d.questionID = " + AuditQuestion.EMR);
		sql.addWhere("d.answer >= " + getFilter().getMinEMR());
		sql.addWhere("d.answer < " + getFilter().getMaxEMR());
		sql.addWhere("d.answer > ''");
	}

}
