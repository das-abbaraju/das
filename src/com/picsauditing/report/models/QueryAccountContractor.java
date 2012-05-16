package com.picsauditing.report.models;

import com.picsauditing.report.tables.Contractor;

public class QueryAccountContractor extends QueryAccount {
	public QueryAccountContractor() {
		super();
		primaryTable.removeField("accountName");
		primaryTable.removeField("accountType");
		
		Contractor contractor = new Contractor(primaryTable.getPrefix(), primaryTable.getAlias());
		primaryTable.addAllFieldsAndJoins(contractor);
	}

}
