package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportContractorUnpaidInvoice extends ReportAccount {
	protected int invoiceID;

	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}

	@Override
	public void run(SelectSQL sql) throws SQLException, IOException {
		if (filterOn(getFilter().getAccountName(), "- Name - ") || invoiceID > 0) {
			super.run(sql);
		}
	}

	public void buildQuery() {
		getFilter().setPrimaryInformation(true);

		super.buildQuery();
		sql.addField("i.id as invoiceId");
		sql.addField("ROUND(i.amountApplied) as amountApplied");
		sql.addField("ROUND(i.totalAmount) as totalAmount");
		sql.addWhere("i.totalAmount > 0");
		sql.addField("i.dueDate");
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
		sql.addWhere("i.status = 'Unpaid'");
		sql.addWhere("i.tableType = 'I'");
		if (invoiceID > 0)
			sql.addWhere("i.id = " + invoiceID);

		sql.addOrderBy("i.dueDate DESC");
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
}
