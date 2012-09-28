package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class AccountTable extends AbstractTable {
	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";
	public static final String Contact = "Contact";
	public static final String Naics = "Naics";

	public AccountTable() {
		super("accounts");
		addPrimaryKey(FilterType.AccountID).setCategory(FieldCategory.AccountInformation);
		addFields(Account.class);

		Field creationDate = new Field("CreationDate", "creationDate", FilterType.Date);
		creationDate.setImportance(FieldImportance.Low);
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