package com.picsauditing.report.tables;

public class ReportJoin {
	// private ReportTable fromTable;
	private ReportTable toTable;
	private String onClause;
	private boolean required = true;

	public ReportJoin(ReportTable toTable, String onClause) {
		// this.fromTable = fromTable;
		this.toTable = toTable;
		this.onClause = onClause;
	}

	public void setLeftJoin() {
		required = false;
	}

	public ReportTable getTable() {
		return toTable;
	}

	public boolean isRequired() {
		return required;
	}

	public String toString() {
		return (required ? "JOIN " : "LEFT JOIN ") + toTable.toString() + " ON " + onClause;
	}

	public boolean requires(ReportJoin join) {
		// TODO Auto-generated method stub
		return false;
	}
}
