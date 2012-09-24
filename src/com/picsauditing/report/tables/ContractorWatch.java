package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorWatch extends AbstractTable {
	public ContractorWatch() {
		super("contractor_watch");
		// 2 problems
		// WHERE (id > 0) = true => WHERE (id > 0)
		addField(new Field("watch", "id > 0", FilterType.Boolean));
	}

	protected void addJoins() {
	}
}