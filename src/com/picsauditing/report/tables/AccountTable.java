package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class AccountTable extends ReportTable {
	
	public AccountTable(String name) {
		super("accounts", name);
		joinToContact();
		joinToNaics();
	}

	private void joinToContact() {
		String onClause = symanticName + ".contactID = " + symanticName + "Contact.id";
		ReportJoin join = new ReportJoin(new UserTable(symanticName + "Contact"), onClause);
		join.getTable().setOverrideCategory(FieldCategory.ContactInformation);
		addLeftJoin(join);
	}

	private void joinToNaics() {
		String onClause = "naics.code = " + symanticName + ".naics";
		ReportJoin join = new ReportJoin(new NaicsTable(), onClause);
		addLeftJoin(join);
	}

	public void fill(Permissions permissions) {
		addPrimaryKey(FilterType.AccountID).setCategory(FieldCategory.AccountInformation);
		addField(new Field(symanticName + "CreationDate", symanticName + ".creationDate", FilterType.Date)).setCategory(FieldCategory.AccountInformation);
		addFields(com.picsauditing.jpa.entities.Account.class, FieldImportance.Low);
	}
}