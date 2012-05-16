package com.picsauditing.report.models;

import com.picsauditing.report.tables.Country;

/**
 * Sample URL: 
 * ReportDynamic!data.action?report.modelType=Country&report.parameters={"rowsPerPage":1000,"columns":[{"name":"countryCode"},{"name":"countryName"}]}
 */
public class QueryCountry extends ModelBase {
	public QueryCountry() {
		super();
		primaryTable = new Country();
		primaryTable.addFields();
		primaryTable.addJoins();
		
		defaultSort = primaryTable.getAlias() + ".isoCode";
	}
}
