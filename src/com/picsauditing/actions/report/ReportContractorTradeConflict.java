package com.picsauditing.actions.report;

import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorTradeConflict extends ReportAccount {
	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addField("translate('Trade', tParent.id) parentName");
		sql.addField("translate('Trade', tChild.id) childName");

		sql.addJoin("JOIN contractor_trade parent ON parent.conID = a.id");
		sql.addJoin("JOIN ref_trade tParent ON tParent.id = parent.tradeID");
		sql.addJoin("JOIN contractor_trade child ON child.conID = a.id");
		sql.addJoin("JOIN ref_trade tChild ON tChild.id = child.tradeID");

		sql.addWhere("tParent.indexStart < tChild.indexStart AND tParent.indexEnd > tChild.indexEnd");

		addFilterToSQL();
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("parentName", "Parent Trade"));
		excelSheet.addColumn(new ExcelColumn("childName", "Child Trade"));
	}
}
