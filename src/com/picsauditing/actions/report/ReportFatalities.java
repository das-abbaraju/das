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

		sql.addJoin("JOIN pqfdata pd ON pd.auditID = ca.id");
		sql.addJoin("JOIN audit_question aq ON aq.id=pd.questionID");
		sql.addJoin("JOIN audit_category cat ON cat.id=aq.categoryID");

		sql.addField("pd.dateVerified");
		sql.addField("pd.answer AS fatalities");
		sql.addField("cat.uniqueCode AS shaType");
		sql.addWhere("pd.answer > 0");
		sql.addWhere("aq.uniqueCode LIKE '%fatalities%'");
	    
		if (filterOn(getFilter().getShaType())) {
			sql.addWhere("cat.uniqueCode = '" + getFilter().getShaType() + "'");
			if (getFilter().getShaType().equals(OshaType.MSHA) || getFilter().getShaType().equals(OshaType.COHS)) {
				getFilter().setVerifiedAnnualUpdate(0);
			}
		}
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
