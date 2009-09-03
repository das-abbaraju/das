package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportIncidenceRate extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.FatalitiesReport);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowIncidenceRate(true);

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addWhere("(os.recordableTotal*200000/os.manHours > " + getFilter().getIncidenceRate() + ")");
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("c.trirAverage");
		sql.addField("os.recordableTotal*200000/os.manHours AS incidenceRate");
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", "Year", ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("location", "Location"));
		excelSheet.addColumn(new ExcelColumn("description", "Description"));
		excelSheet.addColumn(new ExcelColumn("SHAType", "SHAType"));
		excelSheet.addColumn(new ExcelColumn("incidenceRate", "Rate", ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("trirAverage", "Average", ExcelCellType.Double));
	}
}
