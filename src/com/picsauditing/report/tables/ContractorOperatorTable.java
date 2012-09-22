package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperator;

public class ContractorOperatorTable extends ReportTable {
	public static final String Operator = "Operator";

	public ContractorOperatorTable() {
		super("generalcontractors");
	}

	public void addJoins() {
		addOptionalKey(new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID")));
		// operator.setOverrideCategory(FieldCategory.ReportingClientSite);
	}

	public void addFields() {
		addFields(ContractorOperator.class);
	}

}