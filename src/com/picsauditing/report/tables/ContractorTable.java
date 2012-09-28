package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String CustomerService = "CustomerService";
	public static final String PQF = "PQF";
	public static final String Flag = "Flag";
	public static final String RequestedBy = "RequestedBy";
	public static final String Watch = "Watch";
	public static final String Tag = "Tag";

	public ContractorTable() {
		super("contractor_info");
		addFields(ContractorAccount.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id", "id",
				ReportOnClause.ToAlias + ".type = 'Contractor'")));

		ReportForeignKey flagKey = addRequiredKey(new ReportForeignKey(Flag, new ContractorOperatorTable(),
				new ReportOnClause("id", "subID", ReportOnClause.ToAlias + ".genID = " + ReportOnClause.AccountID)));
		flagKey.setMinimumImportance(FieldImportance.Low);

		ReportForeignKey csrKey = addOptionalKey(new ReportForeignKey(CustomerService, new UserTable(),
				new ReportOnClause("welcomeAuditor_id")));
		csrKey.setMinimumImportance(FieldImportance.Average);
		csrKey.setCategory(FieldCategory.CustomerServiceRepresentatives);

		ReportForeignKey pqfKey = addOptionalKey(new ReportForeignKey(PQF, new ContractorAuditTable(),
				new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = 1")));
		pqfKey.setMinimumImportance(FieldImportance.Required);

		ReportForeignKey requestedBy = addOptionalKey(new ReportForeignKey(RequestedBy, new AccountTable(),
				new ReportOnClause("requestedByID")));
		requestedBy.setMinimumImportance(FieldImportance.Required);
		requestedBy.setCategory(FieldCategory.RequestingClientSite);

		addOptionalKey(new ReportForeignKey(Watch, new ContractorWatch(), new ReportOnClause("id", "conID",
				ReportOnClause.ToAlias + ".userID = " + ReportOnClause.UserID)));

		addOptionalKey(new ReportForeignKey(Tag, new ContractorTagView(), new ReportOnClause("id", "conID",
				ReportOnClause.ToAlias + ".opID IN (" + ReportOnClause.VisibleAccountIDs + ")")));
	}
}