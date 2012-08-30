package com.picsauditing.report.tables;

public class ContractorOperatorTable extends AbstractTable {

	public ContractorOperatorTable(String parentPrefix, String parentAlias) {
		super("generalcontractors", "contractorOperator", "gc", "gc.subID = " + parentAlias + ".id");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	@Override
	public void addFields() {
		addFields(com.picsauditing.jpa.entities.ContractorOperator.class);
	}

	@Override
	public void addJoins() {
		AccountTable operator = new AccountTable(prefix + "Operator", alias + ".genID");
		operator.setOverrideCategory(FieldCategory.ReportingClientSite);
		addLeftJoin(operator);
	}
}