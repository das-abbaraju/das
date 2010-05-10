package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportTrirRates extends ReportAnnualAddendum {
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.TRIRReport);
		super.checkPermissions();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		getFilter().setShowTrirRange(true);
		getFilter().setShowAuditFor(false);
		getFilter().setShowVerifiedAnnualUpdates(false);
		getFilter().setShowShaTypeFlagCriteria(true);
		
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.conID = a.id");
		sql.addJoin("JOIN flag_criteria fc ON fc.id = fcc.criteriaID");
		sql.addField("fcc.answer");
		sql.addField("fc.oshaType AS SHAType");
		sql.addWhere("fc.multiYearScope = 'ThreeYearAverage'");
		sql.addWhere("fc.oshaRateType = 'TrirAbsolute'");
		sql.addWhere("fcc.answer >= " + getFilter().getMinTRIR());
		sql.addWhere("fcc.answer < " + getFilter().getMaxTRIR());
		sql.addGroupBy("a.name");

		if (filterOn(getFilter().getShaTypeFlagCriteria()))
			sql.addWhere("fc.oshaType = '"+ getFilter().getShaTypeFlagCriteria() +"'");
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("SHAType", "Type"), 30);
		excelSheet.addColumn(new ExcelColumn("answer", "TRIR/RIF Average", ExcelCellType.Integer), 30);
	}
}
