package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmrRates extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.EMRReport);
		super.checkPermissions();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowEmrRange(true);

		sql.addJoin("JOIN pqfdata d ON d.auditID = ca.id");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.criteriaID = " + FlagCriteria.EMR_AVERAGE_ID
				+ " AND fcc.conID = a.id");
		sql.addField("d.answer");
		sql.addField("fcc.answer emrAverage");
		sql.addWhere("d.questionID = " + AuditQuestion.EMR);
		sql.addWhere("d.answer >= " + getFilter().getMinEMR());
		sql.addWhere("d.answer < " + getFilter().getMaxEMR());
		sql.addWhere("d.answer > ''");
		sql.addField("d.dateVerified");

		setVerifiedAnnualUpdateFilter("dateVerified");
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", getText("ReportEmrRates.header.Year"), ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("answer", getText("ReportEmrRates.header.Rate"), ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("emrAverage", getText("global.Average"), ExcelCellType.Double));
	}
}
