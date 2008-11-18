package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditStatus;

public class ReportPQFVerification extends ReportContractorAudits {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		getFilter().setShowOshaEmr(true);

		permissions.tryPermission(OpPerms.AuditVerification);
		sql.addField("c.notes");
		//sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Submitted + "','" + AuditStatus.Pending + "')");
		sql.addWhere("ca.auditTypeID = 1");
		sql.addJoin("LEFT JOIN osha os ON os.conID = a.id AND os.location = 'Corporate'");
		sql.addField("os.verifiedDate1");
		sql.addField("os.verifiedDate2");
		sql.addField("os.verifiedDate3");
		sql.addPQFQuestion(1617);
		sql.addPQFQuestion(1519);
		sql.addPQFQuestion(889);
		sql.addField("q1617.dateVerified as dateVerified07");
		sql.addField("q1519.dateVerified as dateVerified06");
		sql.addField("q889.dateVerified as dateVerified05");
		sql.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc " +
				"JOIN audit_operator ao ON gc.genID = ao.opID WHERE ao.auditTypeID = 1 AND ao.requiredAuditStatus = 'Active')");
		
		if (getFilter().isOsha1()) {
			sql.addWhere("os.verifiedDate1 IS NULL");
		}
		if (getFilter().isOsha2()) {
			sql.addWhere("os.verifiedDate2 IS NULL");
		}
		if (getFilter().isOsha3()) {
			sql.addWhere("os.verifiedDate3 IS NULL");
		}
		if (getFilter().isEmr07()) {
			sql.addWhere("q1617.dateVerified IS NULL OR q1617.dateVerified='0000-00-00'");
		}
		if (getFilter().isEmr06()) {
			sql.addWhere("q1519.dateVerified IS NULL OR q1519.dateVerified='0000-00-00'");
		}
		if (getFilter().isEmr05()) {
			sql.addWhere("q889.dateVerified IS NULL OR q889.dateVerified='0000-00-00'");
		}

		if (filtered == null)
			filtered = false;

		getFilter().setShowAuditType(false);

		return super.execute();
	}
}
