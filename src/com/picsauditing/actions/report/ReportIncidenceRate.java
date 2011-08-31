package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportIncidenceRate extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.TRIRReport);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowIncidenceRate(true);
		getFilter().setShowIncidenceRateAvg(true);
		getFilter().setShowShaType(true);
		getFilter().setShowShaLocation(true);
		getFilter().setShowCohsStats(true);

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addJoin("JOIN naics n ON n.code = a.naics");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.conID = a.id AND fcc.criteriaID = 559");
		sql.addWhere("(os.recordableTotal*200000/os.manHours >= " + getFilter().getIncidenceRate() + ")");
		sql.addWhere("(os.recordableTotal*200000/os.manHours < " + getFilter().getIncidenceRateMax() + ")");
		sql.addWhere("(c.trirAverage >= " + getFilter().getIncidenceRateAvg() + "AND c.trirAverage < "
				+ getFilter().getIncidenceRateAvgMax() + ")"
				+ (getFilter().getIncidenceRateAvg() == -1.0f ? " OR c.trirAverage IS NULL" : ""));
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("ROUND(fcc.answer, 2) trirAverage");
		sql.addField("os.recordableTotal*200000/os.manHours AS incidenceRate");
		sql.addField("os.verifiedDate");
		sql.addField("os.cad7");
		sql.addField("os.neer");
		sql.addField("n.trir");

		setVerifiedAnnualUpdateFilter("verifiedDate");

		if (getFilter().getCad7() > 0) {
			sql.addWhere("os.cad7 IS NOT NULL AND os.cad7 >= " + getFilter().getCad7());
		}
		if (getFilter().getNeer() > 0) {
			sql.addWhere("os.neer IS NOT NULL AND os.neer >= " + getFilter().getNeer());
		}
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", getText("Filters.label.ForYear"), ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("location", getText("ReportIncidenceRate.Location")));
		excelSheet.addColumn(new ExcelColumn("description", getText("global.Description")));
		excelSheet.addColumn(new ExcelColumn("SHAType", getText("Filters.label.SHAType")));
		excelSheet
				.addColumn(new ExcelColumn("incidenceRate", getText("ReportIncidenceRate.Rate"), ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("trirAverage", getText("global.Average"),
				ExcelCellType.Double));
	}
}
