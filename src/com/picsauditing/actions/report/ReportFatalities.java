package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportFatalities extends ReportAnnualAddendum {
	
	public ReportFatalities(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.FatalitiesReport);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		
		getFilter().setShowShaType(true);
		getFilter().setShowShaLocation(true);
		setVerifiedAnnualUpdateFilter("verifiedDate");

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		String oshaLog = "(os.SHAType = 'OSHA' && pd.QuestionID = 2064 && pd.answer = 'Yes')";
		String MshaLog = "(os.SHAType = 'MSHA' && pd.QuestionID = 2065 && pd.answer = 'Yes')";
		String CanadianLog = "(os.SHAType = 'COHS' && pd.QuestionID = 2066 && pd.answer = 'Yes')";
		sql.addWhere(oshaLog + " OR " + MshaLog +" OR " + CanadianLog);
		
		sql.addWhere("os.fatalities > 0");
		sql.addField("os.fatalities");
		sql.addField("os.SHAType");
		sql.addField("os.verifiedDate");
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", "Year", ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("SHAType", "SHAType"));
		excelSheet.addColumn(new ExcelColumn("fatalities", "Fatalities", ExcelCellType.Integer));
	}
}
