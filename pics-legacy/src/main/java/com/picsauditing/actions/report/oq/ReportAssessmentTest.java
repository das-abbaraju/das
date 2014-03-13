package com.picsauditing.actions.report.oq;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAssessmentTest extends ReportEmployee {
	@Override
	public String execute() throws Exception {
		loadPermissions();
		// Operators, Corporate, Administrators
		if (!permissions.isAdmin() && !permissions.isOperatorCorporate())
			throw new NoRightsException("Operator, Corporate or PICS Administrator");

		orderByDefault = "test.qualificationType,test.qualificationMethod,a.name,e.lastName";
		getFilter().setShowLimitEmployees(true);
		getFilter().setShowAssessmentCenter(true);
		getFilter().setPermissions(permissions);
		getFilter().setAjax(true);
		getFilter().setDestinationAction("ReportAssessmentTests");

		return super.execute();
	}

	public String data() throws Exception {
		execute();

		return "data";
	}

	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("JOIN assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("JOIN accounts center ON center.id = test.assessmentCenterID");

		sql.addField("a.type accountType");
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

		excelSheet.addColumn(new ExcelColumn("centerName", getText("global.AssessmentCenter")));
		excelSheet.addColumn(new ExcelColumn("test", getText("AssessmentTest")));
		excelSheet.addColumn(new ExcelColumn("inEffect", "In Effect"));
	}
}
