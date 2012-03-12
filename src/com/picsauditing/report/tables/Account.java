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
		addField(prefix + "Name", alias + ".name", FilterType.AccountName).setWidth(200);
		addField(prefix + "DBAName", alias + ".dbaName", FilterType.AccountName).setWidth(200);
		addField(prefix + "Status", alias + ".status", FilterType.AccountStatus);
		addField(prefix + "Type", alias + ".type", FilterType.AccountType);
		addField(prefix + "Phone", alias + ".phone", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "Fax", alias + ".fax", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);
		addField(prefix + "Address", alias + ".address", FilterType.String).setCategory(FieldCategory.Contact).setWidth(150);
		addField(prefix + "City", alias + ".city", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "State", alias + ".state", FilterType.StateProvince).setCategory(FieldCategory.Contact).setWidth(60);
		addField(prefix + "Zip", alias + ".zip", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "Country", alias + ".country", FilterType.Country).setCategory(FieldCategory.Contact).setWidth(60);
		addField(prefix + "Website", alias + ".web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { prefix + "Website" });
		addField(prefix + "Reason", alias + ".reason", FilterType.String).setCategory(FieldCategory.Billing).setWidth(200);
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
