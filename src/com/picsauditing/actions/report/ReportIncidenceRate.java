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
		getFilter().setShowShaType(true);
		getFilter().setShowShaLocation(true);
		getFilter().setShowCohsStats(true);

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addWhere("(os.recordableTotal*200000/os.manHours > " + getFilter().getIncidenceRate() + ")");
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("c.trirAverage");
		sql.addField("os.recordableTotal*200000/os.manHours AS incidenceRate");
		sql.addField("os.verifiedDate");
		sql.addField("os.cad7");
		sql.addField("os.neer");
		
		setVerifiedAnnualUpdateFilter("verifiedDate");
		
		if(getFilter().getCad7() > 0) {
			sql.addWhere("os.cad7 IS NOT NULL AND os.cad7 >= " + getFilter().getCad7());
		}
		if(getFilter().getNeer() > 0) {
			sql.addWhere("os.neer IS NOT NULL AND os.neer >= "+ getFilter().getNeer());
		}
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
