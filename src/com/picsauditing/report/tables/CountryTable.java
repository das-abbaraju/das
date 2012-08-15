package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class CountryTable extends AbstractTable {

	public CountryTable() {
		super("ref_country", "country", "country", "");
	}

	public CountryTable(String prefix, String alias, String foreignKey) {
		super("ref_country", prefix, alias, alias + ".isoCode = " + foreignKey);
	}

	public CountryTable(String alias, String foreignKey) {
		super("ref_country", alias, alias, alias + ".isoCode = " + foreignKey);
	}
	
	public void addFields() {
		addFields(com.picsauditing.jpa.entities.Country.class);
		
		Field countryName = addField(prefix + "Name", alias + ".isoCode", FilterType.String);
		countryName.setTranslationPrefixAndSuffix("Country", "");
		countryName.setWidth(100);
	}

	public void addJoins() {
	}
}
