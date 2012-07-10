package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class Country extends BaseTable {

	public Country() {
		super("ref_country", "country", "country", "");
	}

	public void addFields() {
		addField(prefix + "Code", alias + ".isoCode", FilterType.String);
		QueryField countryName = addField(prefix + "Name", alias + ".isoCode", FilterType.String);
		countryName.translate("Country", "");
		countryName.setWidth(100);
		addField(prefix + "Currency", alias + ".currency", FilterType.String);
	}

	public void addJoins() {
	}
}
