package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class DelinquentAccounts extends ReportContractorInvoice {

	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("i.dueDate < NOW()");
		sql.addWhere("i.status = 'Unpaid'");
		sql.addWhere("i.totalAmount > 0");
		sql.addWhere("a.status IN ('Active','Demo')");
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
