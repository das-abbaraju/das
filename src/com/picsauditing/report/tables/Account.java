package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class Account extends BaseTable {

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
		addField(prefix + "Onsite", alias + ".onsiteServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(prefix + "Offsite", alias + ".offsiteServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(prefix + "Transportation", alias + ".transportationServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(prefix + "MaterialSupplier", alias + ".materialSupplier", FilterType.Boolean).setCategory(FieldCategory.Classification);
	}

	public void addJoins() {
		addLeftJoin(new User(prefix + "Contact", alias + ".contactID"));
		addLeftJoin(new Naics(prefix + "Naics", alias + ".naics"));
	}
}
