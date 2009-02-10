package com.picsauditing.actions.report;



@SuppressWarnings("serial")
public class ReportContractorInvoice extends ReportAccount {
	
	public ReportContractorInvoice() {
		orderByDefault = "i.dueDate";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("i.id as invoiceId");
		sql.addField("i.totalAmount");
		sql.addField("i.dueDate");
		
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
	}
}
