package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.fields.Field
import com.picsauditing.report.fields.FilterType

public class AccountTable extends AbstractTable {
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";

	AccountTable() {
		super("accounts");
		
		// addPrimaryKey(FilterType.AccountID).setCategory(FieldCategory.AccountInformation);
		addFields(Account.class);
		// addField(new Field("CreationDate", "creationDate", FilterType.Date)).setCategory(FieldCategory.AccountInformation);
	}
	
	public void addJoins() {
		addOptionalKey(new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")));
		addOptionalKey(new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")));
	}
}