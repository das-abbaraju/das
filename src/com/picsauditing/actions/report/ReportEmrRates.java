package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmrRates extends ReportAnnualAddendum {

	public ReportEmrRates(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
	}

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
		sql.addField("d.answer");
		sql.addField("c.emrAverage");
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
		excelSheet.addColumn(new ExcelColumn("auditFor", "Year", ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("answer", "Rate", ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("emrAverage", "Average", ExcelCellType.Double));
	}
}
