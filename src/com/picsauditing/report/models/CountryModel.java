package com.picsauditing.report.models;

import com.picsauditing.report.tables.CountryTable;

/**
 * Sample URL: 
 * ReportDynamic!data.action?report.modelType=Country&report.parameters={"columns":[{"name":"countryCode"},{"name":"countryName"}]}
 */
public class CountryModel extends AbstractModel {
	public CountryModel() {
		super();
		rootTable = new CountryTable();
		rootTable.addFields();
		rootTable.addJoins();
		
		defaultSort = rootTable.getAlias() + ".isoCode";
		
		parentTable = rootTable;
	}
}
