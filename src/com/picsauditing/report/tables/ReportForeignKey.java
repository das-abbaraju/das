package com.picsauditing.report.tables;

public class ReportForeignKey {
	private String name;
	private AbstractTable table;
	private ReportOnClause onClause;
	private FieldCategory category;
	private JoinType joinType = JoinType.LeftJoin;
    //TODO Michael, Should we change this to Required? -Trevor
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

	public JoinType getJoinType() {
		return joinType;
	}
	
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public void setLeftJoin() {
		this.joinType = JoinType.LeftJoin;
	}

	public void setRequiredJoin() {
		this.joinType = JoinType.RequiredJoin;
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
