package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportContractorUnpaidInvoice extends ReportAccount {
	protected Integer invoiceID;
	protected String transactionStatus = "All";


	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}

	@Override
	public void run(SelectSQL sql) throws SQLException, IOException {
		run(sql, null);
	}

	@Override
	public void run(SelectSQL sql, List<SelectSQL> unionSql) throws SQLException, IOException {
		if (filterOn(getFilter().getAccountName(), "- Name - ") || invoiceID != null) {
			super.run(sql, new ArrayList<SelectSQL>());
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
		sql.addField("i.status");
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
		if(filterOn(transactionStatus, "All"))
			sql.addWhere("i.status = '"+ transactionStatus +"'");
		sql.addWhere("i.tableType = 'I'");
		if (invoiceID != null)
			sql.addWhere("i.id = " + invoiceID);

		sql.addOrderBy("i.dueDate DESC");
	}

	public int getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	public TransactionStatus[] getTransactionStatusList() {
		return TransactionStatus.values();
	}

}
