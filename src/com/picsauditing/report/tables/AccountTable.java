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
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setWidth(80);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);
		
		addFields(com.picsauditing.jpa.entities.Account.class);
	}

	public void addJoins() {
		addLeftJoin(new UserTable(prefix + "Contact", alias + ".contactID"));
		addLeftJoin(new NaicsTable(prefix + "Naics", alias + ".naics"));
	}
}
