package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Country;

public class CountryTable extends AbstractTable {

	public CountryTable() {
		super("ref_country");
		// super("ref_country", alias, alias, alias + ".isoCode = " +
		// foreignKey);

		addFields(Country.class);

		// Field countryName = addField(prefix + "Name", alias + ".isoCode",
		// FilterType.String);
		// countryName.setTranslationPrefixAndSuffix("Country", "");
		// countryName.setWidth(100);
	}

	protected void addJoins() {
	}
}
