package com.picsauditing.report.models;

import com.picsauditing.report.tables.Contractor;

public class QueryAccountContractor extends QueryAccount {
	public QueryAccountContractor() {
		super();
		from.removeField("accountName");
		from.removeField("accountType");

		Contractor contractor = new Contractor(from.getAlias());
		from.addAllFieldsAndJoins(contractor);
	}

}
