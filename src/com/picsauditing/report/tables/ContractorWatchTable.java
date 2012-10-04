package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorWatchTable extends AbstractTable {
	public ContractorWatchTable() {
		super("contractor_watch");
		// 2 problems
		// WHERE (id > 0) = true => WHERE (id > 0)
		addField(new Field("watch", "id > 0", FieldType.Boolean));
	}

	protected void addJoins() {
	}
}