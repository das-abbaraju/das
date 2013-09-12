package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class DelinquentAccounts extends ReportContractorInvoice {

	public void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("left join contractor_audit manual on manual.conID=c.id and manual.auditTypeID=2 and (manual.expiresDate is NULL or manual.expiresDate > NOW())");
		sql.addJoin("left join contractor_audit_operator manualcao on manualcao.auditID = manual.id and manualcao.visible=1");
		sql.addJoin("left join contractor_audit imp on imp.conID=c.id and imp.auditTypeID=3 and imp.expiresDate > NOW()");
		sql.addJoin("left join contractor_audit_operator impcao on impcao.auditID = imp.id and impcao.visible=1");
		sql.addField("manualcao.status as manualAuditStatus");
		sql.addField("impcao.status as impAuditStatus");
		sql.addField("imp.scheduledDate as impAuditScheduledDate");
		
		sql.addWhere("i.dueDate < NOW()");
		sql.addWhere("i.status = 'Unpaid'");
		sql.addWhere("i.totalAmount > 0");
		sql.addWhere("a.status = 'Active'");
		sql.addField("DATEDIFF(ADDDATE(i.dueDate, 90),NOW()) AS DaysLeft");
		if(permissions.seesAllContractors()) {
			sql.addField("(i.totalAmount - i.amountApplied) AS invoiceAmount");
			sql.addField("COUNT(gcon.genid) as facilityCount");
			sql.addJoin("LEFT JOIN generalcontractors gcon on gcon.subid = a.id");
			sql.addGroupBy("gcon.subid, i.id");
		}
		getFilter().setShowStatus(false);
	}
}
