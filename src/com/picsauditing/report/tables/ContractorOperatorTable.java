package com.picsauditing.report.tables;

public class ContractorOperatorTable extends ReportTable {
	public static final String Operator = "Operator";

	public ContractorOperatorTable() {
		super("generalcontractors");
		addOptionalKey(new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("genID")));
		// operator.setOverrideCategory(FieldCategory.ReportingClientSite);
	}

	protected void defineFields() {
		// addFields(com.picsauditing.jpa.entities.ContractorOperator.class);
	}

}