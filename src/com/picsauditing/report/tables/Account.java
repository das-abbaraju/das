package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class Account extends BaseReportTable {

	public Account() {
		super("accounts", "account", "a", "");
	}

	public Account(String prefix, String alias, String foreignKey) {
		super("accounts", prefix, alias, alias + ".id = " + foreignKey);
	}

	public Account(String alias, String foreignKey) {
		super("accounts", alias, alias, alias + ".id = " + foreignKey);
	}
	
	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setWidth(80);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);
		
		addFields(com.picsauditing.jpa.entities.Account.class);
	}

	public void addJoins() {
		addLeftJoin(new User(prefix + "Contact", alias + ".contactID"));
		addLeftJoin(new Naics(prefix + "Naics", alias + ".naics"));
	}
}
