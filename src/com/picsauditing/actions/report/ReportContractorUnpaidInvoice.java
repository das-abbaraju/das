package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportContractorUnpaidInvoice extends ReportAccount {
	protected int invoiceID;
	protected TransactionStatus transactionStatus;


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
		if(transactionStatus == null)
			sql.addWhere("i.status = 'Unpaid'");
		else
			sql.addWhere("i.status = '"+ transactionStatus +"'");
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

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	public TransactionStatus[] getTransactionStatusList() {
		return TransactionStatus.values();
	}

}
