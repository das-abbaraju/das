package com.picsauditing.actions.report;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportEmployee {

	public ReportCompetencyByEmployee() {
		orderByDefault = "e.lastName, e.firstName, a.name";
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN (SELECT DISTINCT er.employeeID, jc.competencyID FROM employee_role er"
				+ " JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID) jc ON jc.employeeID = e.id");
		sql.addJoin("LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID");
		sql.addJoin("JOIN contractor_tag ct ON a.id = ct.conID");
		sql.addWhere("ct.tagID = 142");

		sql.addGroupBy("e.id HAVING required > 0");
		if (permissions.isContractor())
			sql.addWhere("a.id = " + permissions.getAccountId());

		PermissionQueryBuilderEmployee builder = new PermissionQueryBuilderEmployee(permissions,
				PermissionQueryBuilderEmployee.SQL);

		sql.addWhere("1 " + builder.toString());

		sql.addField("COUNT(jc.competencyID) AS required");
		sql.addField("SUM(IFNULL(ec.skilled,0)) AS skilled");
	}

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

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("skilled", "Competency", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("required", "Required", ExcelCellType.Integer));
	}

	public int getRatio(int a, int b) {
		return (int) Math.floor((((float) a) / b) * 100);
	}
}
