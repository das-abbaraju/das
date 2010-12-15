package com.picsauditing.actions.report.oq;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAssessmentTest extends ReportEmployee {
	private String subHeading = "Assessment Test Report";
	
	@Override
	public String execute() throws Exception {
		loadPermissions();
		// Operators, Corporate, Administrators
		if (!permissions.isAdmin() && !permissions.isOperatorCorporate())
			throw new NoRightsException("Operator, Corporate or PICS Administrator");
		
		orderByDefault = "test.qualificationType,test.qualificationMethod,a.name,e.lastName";
		// Do we need to look up employees by SSN?
		getFilter().setShowSsn(false);
		getFilter().setShowLimitEmployees(true);
		getFilter().setShowAssessmentCenter(true);
		getFilter().setPermissions(permissions);
		getFilter().setAjax(true);
		getFilter().setDestinationAction("ReportAssessmentTests");
		
		return super.execute();
	}
	
	protected void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("JOIN assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("JOIN assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("JOIN accounts center ON center.id = test.assessmentCenterID");
		
		sql.addField("center.name centerName");
		sql.addField("CONCAT(test.qualificationMethod, ': ', test.qualificationType, ' - ', test.description) test");
		sql.addField("CASE WHEN ar.effectiveDate < NOW() AND ar.expirationDate > NOW() THEN 'Yes' ELSE 'No' END inEffect");
	}
	
	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();
		
		if (filterOn(getFilter().getAssessmentCenters()))
			sql.addWhere("center.id IN (" + Strings.implode(getFilter().getAssessmentCenters()) + ")");
	}
	
	protected void addExcelColumns() {
		super.addExcelColumns();
		
		excelSheet.addColumn(new ExcelColumn("centerName", "Assessment Center"));
		excelSheet.addColumn(new ExcelColumn("test", "Assessment Test"));
		excelSheet.addColumn(new ExcelColumn("inEffect", "In Effect"));
	}
	
	public String getSubHeading() {
		return subHeading;
	}
}
