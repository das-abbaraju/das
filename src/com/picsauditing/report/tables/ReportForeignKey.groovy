package com.picsauditing.report.tables;

public class ReportForeignKey {
	private String name;
	private AbstractTable toTable;
	private ReportOnClause onClause;
	private boolean required = true;
	private FieldImportance minimumImportance = FieldImportance.Low;

	public ReportForeignKey(String name, AbstractTable toTable, ReportOnClause onClause) {
		this.name = name;
		this.toTable = toTable;
		this.onClause = onClause;
	}

	public String getName() {
		return name;
	}

	public AbstractTable getTable() {
		return toTable;
	}

	public ReportOnClause getOnClause() {
		return onClause;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired() {
		required = false;
	}
	
	

	public String toString() {
		return name;
	}
}
