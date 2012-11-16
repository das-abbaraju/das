package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String CustomerService = "CustomerService";
	public static final String RecommendedCSR = "RecommendedCSR";
	public static final String PQF = "PQF";
	public static final String Flag = "Flag";
	public static final String RequestedBy = "RequestedBy";
	public static final String Watch = "Watch";
	public static final String Tag = "Tag";
	public static final String ContractorStatistics = "ContractorStatistics";
	public static final String ContractorTrade = "ContractorTrade";

	public ContractorTable() {
		super("contractor_info");
		addFields(ContractorAccount.class);
		Field docuGUARD = new Field("DocuGUARD", "EXISTS(SELECT * FROM contractor_fee cf WHERE cf.conID = "
				+ ReportOnClause.ToAlias + ".id AND cf.currentAmount > 0 AND cf.feeClass = 'DocuGUARD')",
				FieldType.Boolean);
		docuGUARD.setCategory(FieldCategory.AccountInformation);
		docuGUARD.setWidth(100);
		addField(docuGUARD);
		Field auditGUARD = new Field("AuditGUARD", "EXISTS(SELECT * FROM contractor_fee cf WHERE cf.conID = "
				+ ReportOnClause.ToAlias + ".id AND cf.currentAmount > 0 AND cf.feeClass = 'AuditGUARD')",
				FieldType.Boolean);
		auditGUARD.setCategory(FieldCategory.AccountInformation);
		auditGUARD.setWidth(100);
		addField(auditGUARD);
		Field insureGUARD = new Field("InsureGUARD", "EXISTS(SELECT * FROM contractor_fee cf WHERE cf.conID = "
				+ ReportOnClause.ToAlias + ".id AND cf.currentAmount > 0 AND cf.feeClass = 'InsureGUARD')",
				FieldType.Boolean);
		insureGUARD.setCategory(FieldCategory.AccountInformation);
		insureGUARD.setWidth(100);
		addField(insureGUARD);
		Field employeeGUARD = new Field("EmployeeGUARD", "EXISTS(SELECT * FROM contractor_fee cf WHERE cf.conID = "
				+ ReportOnClause.ToAlias + ".id AND cf.currentAmount > 0 AND cf.feeClass = 'EmployeeGUARD')",
				FieldType.Boolean);
		employeeGUARD.setCategory(FieldCategory.AccountInformation);
		employeeGUARD.setWidth(100);
		addField(employeeGUARD);
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

		addOptionalKey(new ReportForeignKey(Watch, new ContractorWatchTable(), new ReportOnClause("id", "conID",
				ReportOnClause.ToAlias + ".userID = " + ReportOnClause.UserID)));

		addOptionalKey(new ReportForeignKey(Tag, new ContractorTagView(), new ReportOnClause("id", "conID",
				ReportOnClause.ToAlias + ".opID IN (" + ReportOnClause.VisibleAccountIDs + ")")));

		addRequiredKey(new ReportForeignKey(ContractorStatistics, new ContractorStatisticsView(), new ReportOnClause(
				"id", "conID")));

		addOptionalKey(new ReportForeignKey(ContractorTrade, new ContractorTradeTable(), new ReportOnClause("id",
				"conID")));

		ReportForeignKey recommendedCsrKey = addOptionalKey(new ReportForeignKey(RecommendedCSR, new UserTable(),
				new ReportOnClause("recommendedCsrID")));
		recommendedCsrKey.setMinimumImportance(FieldImportance.Required);
		recommendedCsrKey.setCategory(FieldCategory.CustomerServiceRepresentatives);
	}
}