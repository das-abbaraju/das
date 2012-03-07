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

	protected void addDefaultFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Number).setSuggested();
		addField(prefix + "Name", alias + ".name", FilterType.AccountName).setSuggested().setWidth(180);
	}

	public void addFields() {
		addField(prefix + "Status", alias + ".status", FilterType.AccountStatus);
		addField(prefix + "Type", alias + ".type", FilterType.AccountType);
		addField(prefix + "Phone", alias + ".phone", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "Fax", alias + ".fax", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);
		addField(prefix + "Address", alias + ".address", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "City", alias + ".city", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "State", alias + ".state", FilterType.StateProvince).setCategory(FieldCategory.Contact);
		addField(prefix + "Zip", alias + ".zip", FilterType.String).setCategory(FieldCategory.Contact);
		addField(prefix + "Country", alias + ".country", FilterType.Country).setCategory(FieldCategory.Contact);
		addField(prefix + "Website", alias + ".web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { prefix + "Website" });
		addField(prefix + "DBAName", alias + ".dbaName", FilterType.AccountName);
		addField(prefix + "Reason", alias + ".reason", FilterType.String).setCategory(FieldCategory.Billing);
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
