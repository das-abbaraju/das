package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;

public class NaicsTable extends ReportTable {

	public NaicsTable() {
		super("naics", "accountNaics");
	}

	public void fill(Permissions permissions) {
		addFields(com.picsauditing.jpa.entities.Naics.class, FieldImportance.Average);
	}
}