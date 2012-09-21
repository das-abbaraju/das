package com.picsauditing.report.tables;

public class ReportJoin {
	private ReportTable fromTable; // ????
	private ReportTable toTable;
	private String onClause; // This may need improvement like an OnClauseBuilder
	private boolean required = true;

	public ReportJoin(ReportTable toTable, String onClause) {
		this.toTable = toTable;
		this.onClause = onClause;
	}

	public void setLeftJoin() {
		required = false;
	}

	public ReportTable getTable() {
		return toTable;
	}
	
	public void setFromTable(ReportTable fromTable) {
		this.fromTable = fromTable;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean requires(ReportJoin join) {
		System.out.println("comparing " + this + " TO " + join);
		return false;
	}

	public String toString() {
		return (required ? "JOIN " : "LEFT JOIN ") + toTable.toString() + " ON " + onClause;
	}
}
