package com.picsauditing.report.models;

import com.picsauditing.report.tables.Contractor;

public class QueryAccountContractor extends QueryAccount {
	public QueryAccountContractor() {
		super();
		from.getFields().remove("accountName");
		from.getFields().remove("accountType");

		Contractor contractor = new Contractor();
		from.addJoin(contractor);
		contractor.addFields();
		contractor.addJoins();
	}

}
