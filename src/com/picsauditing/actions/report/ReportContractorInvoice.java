package com.picsauditing.actions.report;



@SuppressWarnings("serial")
public class ReportContractorInvoice extends ReportAccount {
	
	public ReportContractorInvoice() {
		orderByDefault = "i.dueDate";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("i.id as invoiceId");
		sql.addField("ROUND(i.totalAmount) as totalAmount");
		sql.addField("i.dueDate");
		sql.addField("c.ccOnFile");
		
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
	}
}
