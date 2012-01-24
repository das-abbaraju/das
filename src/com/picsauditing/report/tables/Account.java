package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class Account extends BaseTable {

	public Account() {
		super("accounts", "a", "");
	}

	public Account(String alias, String foreignKey) {
		super("accounts", alias, alias + ".id = " + foreignKey);
	}

	protected void addDefaultFields() {
		addField("accountID", "a.id", FilterType.Number);
		addField("accountName", "a.name", FilterType.AccountName);
	}

	public void addFields() {
		addField("accountStatus", "a.status", FilterType.Enum);
		addField("accountType", "a.type", FilterType.Enum);
		addField("accountPhone", "a.phone", FilterType.String);
		addField("accountFax", "a.fax", FilterType.String);
		addField("accountCreationDate", "a.creationDate", FilterType.Date);
		addField("accountAddress", "a.address", FilterType.String);
		addField("accountCity", "a.city", FilterType.String);
		addField("accountState", "a.state", FilterType.String);
		addField("accountZip", "a.zip", FilterType.String);
		addField("accountCountry", "a.country", FilterType.String);

		addField("accountWebsite", "a.web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { "accountWebsite" });
		addField("accountDBAName", "a.dbaName", FilterType.AccountName);
		addField("accountNameIndex", "a.nameIndex", FilterType.AccountName);
		addField("accountReason", "a.reason", FilterType.String);
		addField("accountOnsite", "a.onsiteServices", FilterType.Boolean);
		addField("accountOffsite", "a.offsiteServices", FilterType.Boolean);
		addField("accountTransportation", "a.transportationServices", FilterType.Boolean);
		addField("accountMaterialSupplier", "a.materialSupplier", FilterType.Boolean);
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("accountContact", "a.contactID"));
		addLeftJoin(new Naics("naics", "a.naics"));
	}
}
