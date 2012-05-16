package com.picsauditing.report.models;

import com.picsauditing.report.tables.CountryTable;

/**
 * Sample URL: 
 * ReportDynamic!data.action?report.modelType=Country&report.parameters={"rowsPerPage":1000,"columns":[{"name":"countryCode"},{"name":"countryName"}]}
 */
public class CountryModel extends BaseModel {
	public CountryModel() {
		super();
		primaryTable = new CountryTable();
		primaryTable.addFields();
		primaryTable.addJoins();
		
		defaultSort = primaryTable.getAlias() + ".isoCode";
	}
}
