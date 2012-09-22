package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperator;

public class ContractorOperatorTable extends AbstractTable {
	public static final String Operator = "Operator";

	public ContractorOperatorTable() {
		super("generalcontractors");
		addFields(ContractorOperator.class);
	}

	public void addJoins() {
		addOptionalKey(new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID")));
		// operator.setOverrideCategory(FieldCategory.ReportingClientSite);
	}
}