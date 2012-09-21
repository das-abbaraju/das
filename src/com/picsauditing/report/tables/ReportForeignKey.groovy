package com.picsauditing.report.tables;

public class ReportForeignKey {
	private String name;
	private ReportTable toTable;
	private ReportOnClause onClause;
	private boolean required = true;

	public ReportForeignKey(String name, ReportTable toTable, ReportOnClause onClause) {
		this.name = name;
		this.toTable = toTable;
		this.onClause = onClause;
	}

	public String getName() {
		return name;
	}

	public void setRequired() {
		required = false;
	}

	public ReportTable getTable() {
		return toTable;
	}

	// public void setFromTable(ReportTable fromTable) {
	// this.fromTable = fromTable;
	// }

	public boolean isRequired() {
		return required;
	}

	// public boolean requires(ReportForeignKey join) {
	// System.out.println("comparing " + this + " TO " + join);
	// return false;
	// }

	// NOT sure we need this yet
	// TODO needs more 
	public String toJoin() {
		return (required ? "JOIN " : "LEFT JOIN ") + toTable.toString() + " ON " + onClause;
	}

	public String toString() {
		return name;
	}
}
