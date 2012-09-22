package com.picsauditing.report.tables;

public class NaicsTable extends ReportTable {

	public NaicsTable() {
		super("naics");
	}

	protected void defineFields() {
		addFields(com.picsauditing.jpa.entities.Naics.class, FieldImportance.Average);
	}
}