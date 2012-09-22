package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Naics;

public class NaicsTable extends ReportTable {

	public NaicsTable() {
		super("naics");
	}

	public void addJoins() {
	}

	public void addFields() {
		addFields(Naics.class);
	}
}