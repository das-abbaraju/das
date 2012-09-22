package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Account
import com.picsauditing.report.fields.Field
import com.picsauditing.report.fields.FilterType

public class AccountTable extends AbstractTable {
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";

	AccountTable() {
		super("accounts");

		addPrimaryKey(FilterType.AccountID).setCategory(FieldCategory.AccountInformation);
		addFields(Account.class);
		// addField(new Field("CreationDate", "creationDate", FilterType.Date)).setCategory(FieldCategory.AccountInformation);
	}

	protected void addJoins() {
		addOptionalKey new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")) setMinimumImportance FieldImportance.Average
		addOptionalKey new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")) setMinimumImportance FieldImportance.Average
	}
}