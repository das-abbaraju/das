package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Country;

public class CountryTable extends AbstractTable {

	public CountryTable() {
		super("ref_country");
		addFields(Country.class);
	}

	protected void addJoins() {
	}
}
