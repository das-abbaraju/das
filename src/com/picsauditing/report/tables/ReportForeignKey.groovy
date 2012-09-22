package com.picsauditing.report.tables;

public class ReportForeignKey {
	String name;
	AbstractTable table;
	ReportOnClause onClause;
	boolean required = true;
	FieldImportance minimumImportance = FieldImportance.Low;

	public ReportForeignKey(String name, AbstractTable toTable, ReportOnClause onClause) {
		this.name = name;
		this.table = toTable;
		this.onClause = onClause;
	}

	public void setRequired() {
		required = false;
	}
	
	public String toString() {
		return name;
	}
}
