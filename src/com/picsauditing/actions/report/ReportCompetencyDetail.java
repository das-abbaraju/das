package com.picsauditing.actions.report;

import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyDetail extends ReportCompetencyByEmployee {

	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN operator_competency oc ON jc.competencyID = oc.id");

		sql.addField("oc.id");
		sql.addField("oc.label");
		sql.addField("CASE ec.skilled WHEN 1 THEN 'Skilled' ELSE '' END skilledValue");
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("label", "Label"));
		excelSheet.addColumn(new ExcelColumn("skilledValue", "Competency"));
	}
}
