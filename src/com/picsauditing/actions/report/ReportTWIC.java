package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportTWIC extends ReportAccount {

	protected SelectSQL sql = new SelectSQL();

	public ReportTWIC(){
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	protected void buildQuery() {
		super.buildQuery();
		
		getFilter().setShowAccountName(true);
		getFilter().setShowIndustry(true);
		getFilter().setShowOperator(true);
		getFilter().setShowTrade(true);
		getFilter().setShowRegistrationDate(false);
		
		sql = new SelectSQL("employee e");

		sql.addField("e.firstName");
		sql.addField("e.id");
		sql.addField("a.id AS aID");
		sql.addField("e.lastName");
		sql.addField("e.title");
		sql.addField("a.name");
		sql.addField("e.twicExpiration");
		
		sql.addJoin("JOIN accounts a on a.id = e.accountID");
		
		System.out.println(sql.toString());
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

}
