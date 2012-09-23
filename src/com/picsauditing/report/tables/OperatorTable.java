package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class OperatorTable extends AbstractTable {
	public static final String Account = "Account";

	public OperatorTable() {
		super("operators");
		addFields(OperatorAccount.class);

		// addField(prefix + "ID", alias + ".id", FilterType.Integer,
		// FieldCategory.ClientSitePreferences).setWidth(80);
		// addField(prefix + "IsCorporate", alias + ".isCorporate",
		// FilterType.Integer,
		// FieldCategory.ClientSitePreferences).setWidth(80);
		//
		// Field operatorName;
		// operatorName = addField(prefix + "Name", "a.name",
		// FilterType.AccountName, FieldCategory.ClientSitePreferences);
		// operatorName.setUrl("FacilitiesEdit.action?operator={" + prefix +
		// "ID}");
		// operatorName.setWidth(300);
	}

	public void addJoins() {
		addKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id", "id")));
		// type IN ('Operator','Corporate')
	}
}
