package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AccountTable extends AbstractTable {
	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String ContractorOperator = "ContractorOperator";
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";
	public static final String Country = "Country";
	public static final String Invoice = "Invoice";
    public static final String AccountUser = "AccountUser";

    public AccountTable() {
		super("accounts");
		addPrimaryKey(FieldType.AccountID).setCategory(FieldCategory.AccountInformation);
		addFields(Account.class);

		Field accountName = getField("NAME");
		String aName = "TRIM(" + ReportOnClause.ToAlias + ".name)";
		accountName.setDatabaseColumnName(aName);

		Field accountLegalName = new Field("LegalName", "dbaName", FieldType.String);
		accountLegalName.setImportance(FieldImportance.Average);
		addField(accountLegalName).setCategory(FieldCategory.AccountInformation);

		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		addField(creationDate).setCategory(FieldCategory.AccountInformation);
	}

	protected void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("id")));
		addOptionalKey(new ReportForeignKey(Operator, new OperatorTable(), new ReportOnClause("id")));

		ReportForeignKey contractorOperatorKey = addOptionalKey(new ReportForeignKey(ContractorOperator,
				new ContractorOperatorTable(), new ReportOnClause("id", "subID", ReportOnClause.ToAlias + ".genID = "
						+ ReportOnClause.AccountID)));
		contractorOperatorKey.setMinimumImportance(FieldImportance.Required);

		addOptionalKey(new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")))
				.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")))
				.setMinimumImportance(FieldImportance.Average);

		addOptionalKey(new ReportForeignKey(Country, new CountryTable(), new ReportOnClause("country", "isoCode")));

		addOptionalKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("id", "accountID")))
				.setMinimumImportance(FieldImportance.Low);

        addOptionalKey(new ReportForeignKey(AccountUser, new AccountUserTable(), new ReportOnClause("id", "accountID", ReportOnClause.ToAlias + ".role = '" +
                UserAccountRole.PICSAccountRep + "' AND " + ReportOnClause.ToAlias +
                ".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()")))
                .setMinimumImportance(FieldImportance.Required);
	}
}