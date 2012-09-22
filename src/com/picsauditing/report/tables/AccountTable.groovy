package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions
import com.picsauditing.report.fields.FilterType

public class AccountTable extends ReportTable {
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";

	AccountTable() {
		super("accounts");
		addOptionalKey(new ReportForeignKey(Contact, new UserTable(), new ReportOnClause("contactID")));
		addOptionalKey(new ReportForeignKey(Naics, new NaicsTable(), new ReportOnClause("naics", "code")));
	}

	protected void defineFields() {
		// addPrimaryKey(FilterType.AccountID).setCategory(FieldCategory.AccountInformation);
		// TODO figure out how to set symanticName when copied into the Model layer
		// addField(new Field(symanticName + "CreationDate", symanticName + ".creationDate", FilterType.Date)).setCategory(FieldCategory.AccountInformation);
		// addFields(com.picsauditing.jpa.entities.Account.class, FieldImportance.Low);
	}
}