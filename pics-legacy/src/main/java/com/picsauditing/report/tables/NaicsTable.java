package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Naics;

public class NaicsTable extends AbstractTable {

	public NaicsTable() {
		super("naics");
		addFields(Naics.class);
	}

	public void addJoins() {
	}
}