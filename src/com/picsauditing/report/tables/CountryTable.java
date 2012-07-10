package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class CountryTable extends AbstractTable {

	public CountryTable() {
		super("ref_country", "country", "country", "");
	}

	public void addFields() {
		addField(prefix + "Code", alias + ".isoCode", FilterType.String);
		Field countryName = addField(prefix + "Name", alias + ".isoCode", FilterType.String);
		countryName.setTranslationPrefixAndSuffix("Country", "");
		countryName.setWidth(100);
		addField(prefix + "Currency", alias + ".currency", FilterType.String);
	}

	public void addJoins() {
	}
}
