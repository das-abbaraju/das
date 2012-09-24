package com.picsauditing.report.tables;

public class ReportForeignKey {
	private String name;
	private AbstractTable table;
	private ReportOnClause onClause;
	private FieldCategory category;
	private boolean required = true;
	private FieldImportance minimumImportance = FieldImportance.Low;

	public ReportForeignKey(String name, AbstractTable toTable, ReportOnClause onClause) {
		this.name = name;
		this.table = toTable;
		this.onClause = onClause;
	}

	public String getName() {
		return name;
	}

	public AbstractTable getTable() {
		return table;
	}

	public ReportOnClause getOnClause() {
		return onClause;
	}

	public FieldCategory getCategory() {
		return category;
	}

	public void setCategory(FieldCategory category) {
		this.category = category;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public FieldImportance getMinimumImportance() {
		return minimumImportance;
	}

	public void setMinimumImportance(FieldImportance minimumImportance) {
		this.minimumImportance = minimumImportance;
	}

	public String toString() {
		return name;
	}
}
