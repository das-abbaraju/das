package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.search.SelectCase;

public class AccountTable extends AbstractTable {
	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";

	public AccountTable() {
		super("accounts");
		addPrimaryKey(FieldType.AccountID).setCategory(FieldCategory.AccountInformation);
		addFields(Account.class);

		Field accountName = getField("NAME");
		{
			String dbaName = ReportOnClause.ToAlias + ".dbaName";
			String aName = ReportOnClause.ToAlias + ".name";
			SelectCase sCase = new SelectCase();
			sCase.addCondition(dbaName + " IS NULL", aName);
			sCase.addCondition(dbaName + " = ''", aName);
			sCase.setElse(dbaName);
			accountName.setDatabaseColumnName(sCase.toString());
		}
		// TODO Change the URL depending on the model
		// Maybe we should pass in the URL when we create the table ??
		accountName.setUrl("ContractorView.action?id={AccountID}");

		Field accountLegalName = new Field("LegalName", "name", FieldType.String);
		accountLegalName.setImportance(FieldImportance.Average);
		addField(accountLegalName).setCategory(FieldCategory.AccountInformation);

		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		addField(creationDate).setCategory(FieldCategory.AccountInformation);
	}

	protected void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("id")));
		addJoinKey(new ReportForeignKey(Operator, new OperatorTable(), new ReportOnClause("id")));
		addOptionalKey(new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")))
				.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")))
				.setMinimumImportance(FieldImportance.Average);
	}
}