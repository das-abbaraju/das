package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.CohsStatistics;
import com.picsauditing.jpa.entities.OshaStatistics;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportFatalities extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.FatalitiesReport);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowShaType(true);
		getFilter().setShowShaLocation(true);
		setVerifiedAnnualUpdateFilter("pd.dateVerified");

		sql.addJoin("JOIN pqfdata pd ON pd.auditID = ca.id AND pd.questionID IN (8812, 10010, 8841)");
		sql.addField("pd.dateVerified");
		sql.addField("pd.answer AS fatalities");
		sql.addField("CASE WHEN pd.questionID = 8812 THEN 'OSHA' WHEN pd.questionID = 10010 THEN 'MSHA' ELSE 'COHS' END AS shaType");
		sql.addWhere("pd.answer > 0");
	    
		if (filterOn(getFilter().getShaType())) {
			int questionID = 0;

			if (getFilter().getShaType().equals(OshaType.OSHA))
				questionID = OshaStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR;
			else if (getFilter().getShaType().equals(OshaType.MSHA))
				questionID = 10010;
			else if (getFilter().getShaType().equals(OshaType.COHS))
				questionID = CohsStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR;

			sql.addWhere("pd.questionID = " + questionID);

			if (getFilter().getShaType().equals(OshaType.MSHA) || getFilter().getShaType().equals(OshaType.COHS)) {
				getFilter().setVerifiedAnnualUpdate(0);
			}
		}

		sql.addGroupBy("shaType");
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(
				new ExcelColumn("auditFor", getText("ReportFatalities.header.Year"), ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("SHAType", getText("ReportFatalities.header.SHAType")));
		excelSheet.addColumn(new ExcelColumn("fatalities", getText("ReportFatalities.header.Fatalities"),
				ExcelCellType.Integer));
	}
}
