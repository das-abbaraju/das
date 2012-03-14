package com.picsauditing.report.models;

import com.picsauditing.report.tables.Country;

/**
 * Sample URL: 
 * ReportDynamic!data.action?report.modelType=Country&report.parameters={"rowsPerPage":1000,"columns":[{"name":"countryCode"},{"name":"countryName"}]}
 */
public class QueryCountry extends ModelBase {
	public QueryCountry() {
		super();
		from = new Country();
		from.addFields();
		from.addJoins();
		
		defaultSort = from.getAlias() + ".isoCode";
	}
}
