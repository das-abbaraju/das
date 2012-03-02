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
		addField("accountID", "a.id", FilterType.Number).setSuggested();
		addField("accountName", "a.name", FilterType.AccountName).setSuggested().setWidth(180);
	}

	public void addFields() {
		addField("accountStatus", "a.status", FilterType.AccountStatus);
		addField("accountType", "a.type", FilterType.AccountType);
		addField("accountPhone", "a.phone", FilterType.String).setCategory("Contact");
		addField("accountFax", "a.fax", FilterType.String).setCategory("Contact");
		addField("accountCreationDate", "a.creationDate", FilterType.Date);
		addField("accountAddress", "a.address", FilterType.String).setCategory("Contact");
		addField("accountCity", "a.city", FilterType.String).setCategory("Contact");
		addField("accountState", "a.state", FilterType.StateProvince).setCategory("Contact");
		addField("accountZip", "a.zip", FilterType.String).setCategory("Contact");
		addField("accountCountry", "a.country", FilterType.Country).setCategory("Contact");

		addField("accountWebsite", "a.web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { "accountWebsite" });
		addField("accountDBAName", "a.dbaName", FilterType.AccountName);
		// addField("accountNameIndex", "a.nameIndex", FilterType.AccountName);
		addField("accountReason", "a.reason", FilterType.String).setCategory("Billing");
		addField("accountOnsite", "a.onsiteServices", FilterType.Boolean).setCategory("Classification");
		addField("accountOffsite", "a.offsiteServices", FilterType.Boolean).setCategory("Classification");
		addField("accountTransportation", "a.transportationServices", FilterType.Boolean).setCategory("Classification");
		addField("accountMaterialSupplier", "a.materialSupplier", FilterType.Boolean).setCategory("Classification");
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("accountContact", "a.contactID"));
		addLeftJoin(new Naics("naics", "a.naics"));
	}
}
