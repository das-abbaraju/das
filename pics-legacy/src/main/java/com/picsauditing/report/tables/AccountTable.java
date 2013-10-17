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
	public static final String AccountManager = "AccountManager";
	public static final String SalesRep = "SalesRep";
    public static final String Note = "Note";
    public static final String LastLogin = "LastLogin";
    public static final String ContractorRenewalPredictor = "ContractorRenewalPredictor";

    public AccountTable() {
		super("accounts");
		addPrimaryKey(FieldType.AccountID);
		addFields(Account.class);

		Field accountName = getField("NAME");
		String aName = "TRIM(" + ReportOnClause.ToAlias + ".name)";
		accountName.setDatabaseColumnName(aName);

		Field accountLegalName = new Field("LegalName", "dbaName", FieldType.String);
		accountLegalName.setImportance(FieldImportance.Average);
		addField(accountLegalName);

        Field creationDate = addCreationDate();
        creationDate.setImportance(FieldImportance.Average);
	}

	protected void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("id"))).setMinimumImportance(FieldImportance.Average);
        addOptionalKey(new ReportForeignKey(Operator, new OperatorTable(), new ReportOnClause("id")));

		ReportForeignKey contractorOperatorKey = addOptionalKey(new ReportForeignKey(ContractorOperator,
				new ContractorOperatorTable(), new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".opID = "
						+ ReportOnClause.AccountID)));

		addOptionalKey(new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")))
				.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")))
				.setMinimumImportance(FieldImportance.Average);

		addOptionalKey(new ReportForeignKey(Country, new CountryTable(), new ReportOnClause("country", "isoCode"))).setMinimumImportance(FieldImportance.Average);

		addOptionalKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("id", "accountID"))).setMinimumImportance(FieldImportance.Average);

		addOptionalKey(new ReportForeignKey(AccountManager, new AccountUserTable(), new ReportOnClause("id", "accountID", ReportOnClause.ToAlias + ".role = '" +
				UserAccountRole.PICSAccountRep + "' AND " + ReportOnClause.ToAlias +
				".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()")));

		addOptionalKey(new ReportForeignKey(SalesRep, new AccountUserTable(), new ReportOnClause("id", "accountID", ReportOnClause.ToAlias + ".role = '" +
				UserAccountRole.PICSSalesRep + "' AND " + ReportOnClause.ToAlias +
				".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()")));

        ReportForeignKey lastLogin = new ReportForeignKey(LastLogin, new AccountLastLoginView(), new ReportOnClause("id", "accountID"));
        lastLogin.setMinimumImportance(FieldImportance.Average);
        addOptionalKey(lastLogin);

        addRequiredKey(new ReportForeignKey(Note, new NoteTable(), new ReportOnClause("id", "accountID")));
        addRequiredKey(new ReportForeignKey(ContractorRenewalPredictor, new ContractorRenewalPredictionView(), new ReportOnClause("id", "conID")));

    }
}