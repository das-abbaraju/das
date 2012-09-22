package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions

public class ContractorTable extends ReportTable {

	public static final String Account = "Account";
	public static final String CustomerService = "CustomerService";
	public static final String PQF = "PQF";
	public static final String Flag = "Flag";
	public static final String RequestedBy = "RequestedBy";

	ContractorTable() {
		super("contractor_info");
		addKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id")));
		addKey(new ReportForeignKey(CustomerService, new UserTable(), new ReportOnClause("welcomeAuditor_id")));
		// FieldCategory.CustomerServiceRepresentatives
		addKey(new ReportForeignKey(PQF, new ContractorAuditTable(), new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = 1")));
		
		addKey(new ReportForeignKey(Flag, new ContractorOperatorTable(), new ReportOnClause("id", "subID", ReportOnClause.ToAlias + ".genID = " + ReportOnClause.AccountID)));
		// contractorOperator.includeAllColumns();
		
		addOptionalKey(new ReportForeignKey(RequestedBy, new AccountTable(), new ReportOnClause("requestedByID")));
		// requestedByOperator.setOverrideCategory(FieldCategory.RequestingClientSite);
	}

	protected void defineFields() {
		// addFields(com.picsauditing.jpa.entities.ContractorAccount.class,
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
}