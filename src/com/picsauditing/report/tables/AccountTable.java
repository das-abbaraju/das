package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class AccountTable extends AbstractTable {

	public AccountTable() {
		super("accounts", "account", "a", "");
	}

	public AccountTable(String prefix, String alias, String foreignKey) {
		super("accounts", prefix, alias, alias + ".id = " + foreignKey);
	}

	public AccountTable(String alias, String foreignKey) {
		super("accounts", alias, alias, alias + ".id = " + foreignKey);
	}
	
	public void addFields() {
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date).setCategory(FieldCategory.AccountDetails);
		
		addFields(com.picsauditing.jpa.entities.Account.class);
	}

	public void addJoins() {
		UserTable primaryContact = new UserTable(prefix + "Contact", alias + ".contactID");
		primaryContact.setOverrideCategory(FieldCategory.AccountInformation);
		addLeftJoin(primaryContact);
		
		NaicsTable naicsStatistics = new NaicsTable(prefix + "Naics", alias + ".naics");
		addLeftJoin(naicsStatistics);
	}
}