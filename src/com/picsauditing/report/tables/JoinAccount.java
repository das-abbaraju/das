package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class JoinAccount extends BaseTable {

	public JoinAccount(String alias, String foreignKey) {
		super("accounts", alias, alias + ".id = " + foreignKey);
	}

	protected void addDefaultFields() {
		addField(alias + "ID", alias + ".id", FilterType.Number).setSuggested();
		addField(alias + "Name", alias + ".name", FilterType.AccountName).setSuggested().setWidth(180);
	}

	public void addFields() {
		addField(alias + "Status", alias + ".status", FilterType.AccountStatus);
		addField(alias + "Type", alias + ".type", FilterType.AccountType);
		addField(alias + "Phone", alias + ".phone", FilterType.String).setCategory(FieldCategory.Contact);
		addField(alias + "Fax", alias + ".fax", FilterType.String).setCategory(FieldCategory.Contact);
		addField(alias + "CreationDate", alias + ".creationDate", FilterType.Date);
		addField(alias + "Address", alias + ".address", FilterType.String).setCategory(FieldCategory.Contact);
		addField(alias + "City", alias + ".city", FilterType.String).setCategory(FieldCategory.Contact);
		addField(alias + "State", alias + ".state", FilterType.StateProvince).setCategory(FieldCategory.Contact);
		addField(alias + "Zip", alias + ".zip", FilterType.String).setCategory(FieldCategory.Contact);
		addField(alias + "Country", alias + ".country", FilterType.Country).setCategory(FieldCategory.Contact);

		addField(alias + "Website", alias + ".web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { alias + "Website" });
		addField(alias + "DBAName", alias + ".dbaName", FilterType.AccountName);
		addField(alias + "Reason", alias + ".reason", FilterType.String).setCategory(FieldCategory.Billing);
		addField(alias + "Onsite", alias + ".onsiteServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(alias + "Offsite", alias + ".offsiteServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(alias + "Transportation", alias + ".transportationServices", FilterType.Boolean).setCategory(FieldCategory.Classification);
		addField(alias + "MaterialSupplier", alias + ".materialSupplier", FilterType.Boolean).setCategory(FieldCategory.Classification);
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("accountContact", "a.contactID"));
		addLeftJoin(new Naics("naics", "a.naics"));
	}
}
