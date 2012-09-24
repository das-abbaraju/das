package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperator;

public class ContractorOperatorTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String Contractor = "Contractor";

	public ContractorOperatorTable() {
		super("generalcontractors");
		addFields(ContractorOperator.class);
	}

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		addKey(operator);
		
		addKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("subID")));
	}
}