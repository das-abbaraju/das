package com.picsauditing.actions.report;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportActionSupport {

	protected SelectSQL sql = new SelectSQL();

	public ReportCompetencyByEmployee() {
		orderByDefault = "e.lastName, e.firstName, a.name";
	}

	protected void buildQuery() {
		sql = new SelectSQL("employee e");

		sql.addJoin("JOIN accounts a on a.id = e.accountID");
		sql.addJoin("JOIN (SELECT DISTINCT er.employeeID, jc.competencyID FROM employee_role er"
				+ " JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID) jc ON jc.employeeID = e.id");
		sql.addJoin("LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID");

		sql.addGroupBy("e.id");

		if (permissions.isContractor())
			sql.addWhere("a.id = " + permissions.getAccountId());

		PermissionQueryBuilderEmployee builder = new PermissionQueryBuilderEmployee(permissions,
				PermissionQueryBuilderEmployee.SQL);

		sql.addWhere("1 " + builder.toString());

		// sql.addField("e.id");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("a.id AS accountID");
		sql.addField("a.name");
		sql.addField("COUNT(jc.competencyID) AS required");
		sql.addField("SUM(IFNULL(ec.skilled,0)) AS skilled");
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (runReport()) {
			buildQuery();
			run(sql);

			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.clear();

			return returnResult();
		}
		return SUCCESS;
	}

	protected String returnResult() throws IOException {

		if (download) {
			addExcelColumns();
			String filename = this.getClass().getName().replace("com.picsauditing.actions.report.", "");
			excelSheet.setName(filename);
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	protected boolean runReport() {
		return true;
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("firstName", "First Name"));
		excelSheet.addColumn(new ExcelColumn("lastName", "Last Name"));
		excelSheet.addColumn(new ExcelColumn("name", "Account"));
		excelSheet.addColumn(new ExcelColumn("skilled", "Competency", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("required", "Required", ExcelCellType.Integer));
	}

}
