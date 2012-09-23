package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAccount;


public class ContractorTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String CustomerService = "CustomerService";
	public static final String PQF = "PQF";
	public static final String Flag = "Flag";
	public static final String RequestedBy = "RequestedBy";

	ContractorTable() {
		super("contractor_info");
		// TODO Forgot to add these fields back in
		// FieldImportance.Low);
		//
		// Field conID = addPrimaryKey(FilterType.Integer);
		// conID.setCategory(FieldCategory.AccountInformation);
		// conID.setWidth(80);

		// Field contractorName = addField(new Field(symanticName + "Name",
		// symanticName + ".name", FilterType.AccountName));
		// contractorName.setCategory(FieldCategory.AccountInformation);
		// contractorName.setUrl("ContractorView.action?id={accountID}");
		// contractorName.setWidth(300);

		addFields(ContractorAccount.class);
		
		// TODO Remove these fields eventually
		// Field contractorEdit = addField(prefix + "Edit", "'Edit'",
		// FilterType.String);
		// contractorEdit.setUrl("ContractorEdit.action?id={" + prefix +
		// "ID}");
		// contractorEdit.setWidth(100);
		//
		// Field contractorAudits = addField(prefix + "Audits", "'Audits'",
		// FilterType.String);
		// contractorAudits.setUrl("ContractorDocuments.action?id={" +
		// prefix + "ID}");
		// contractorAudits.setWidth(100);
	}

	protected void addJoins() {
		addKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id")));
		addKey(new ReportForeignKey(CustomerService, new UserTable(), new ReportOnClause("welcomeAuditor_id"))).setMinimumImportance(FieldImportance.Average);
		// FieldCategory.CustomerServiceRepresentatives
		addKey(new ReportForeignKey(PQF, new ContractorAuditTable(), new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = 1"))).setMinimumImportance(FieldImportance.Required);

		addKey(new ReportForeignKey(Flag, new ContractorOperatorTable(), new ReportOnClause("id", "subID", ReportOnClause.ToAlias + ".genID = " + ReportOnClause.AccountID)));
		// contractorOperator.includeAllColumns();

		addOptionalKey(new ReportForeignKey(RequestedBy, new AccountTable(), new ReportOnClause("requestedByID"))).setMinimumImportance(FieldImportance.Required);
		// requestedByOperator.setOverrideCategory(FieldCategory.RequestingClientSite);
	}
}