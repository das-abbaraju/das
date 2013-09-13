package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectFilterDate;



@SuppressWarnings("serial")
public class ReportContractorInvoice extends ReportAccount {
	
	public ReportContractorInvoice() {
		orderByDefault = "i.dueDate";
	}
	
	public void buildQuery() {
		super.buildQuery();
		
		if(permissions.hasPermission(OpPerms.Billing)) {
			getFilter().setShowInvoiceDueDate(true);
		}
		getFilter().setShowConWithPendingAudits(false);
		
		sql.addField("i.id as invoiceId");
		sql.addField("i.creationDate as invoicedDate");
		sql.addField("ROUND(i.totalAmount) as totalAmount");
		sql.addField("i.dueDate");
		sql.addField("c.ccOnFile");
		
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
		sql.addWhere("i.tableType = 'I'");
		if (filterOn(getFilter().getInvoiceDueDate1())) {
			report.addFilter(new SelectFilterDate("invoiceDueDate1", "i.dueDate >= '?'", DateBean.format(getFilter().getInvoiceDueDate1(), "M/d/yy")));
		}	
		if (filterOn(getFilter().getInvoiceDueDate2())) {
			report.addFilter(new SelectFilterDate("invoiceDueDate2", "i.dueDate < '?'", DateBean.format(getFilter().getInvoiceDueDate2(), "M/d/yy")));
		}
	}
}
