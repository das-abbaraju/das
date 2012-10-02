package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorOperatorNumber;


public class ContractorNumberTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Operator = "Operator";

	public ContractorNumberTable() {
		super("contractor_operator_number");
		addFields(ContractorOperatorNumber.class);
	}

	protected void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID","id")));
		addJoinKey(new ReportForeignKey(Operator, new OperatorTable(), new ReportOnClause("opID","id")));
	}
}