package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorLicenses extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorLicenseReport);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addWhere("ca.auditTypeID = 1"); // PQF
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addPQFQuestion(401);
		sql.addPQFQuestion(755);
		sql.addField("q401.dateVerified AS dateVerified401");
		sql.addField("q755.dateVerified AS dateVerified755");
		sql.addField("q401.comment AS comment401");
		sql.addField("q755.comment AS comment755");
		
		orderByDefault = "a.name";
		
		if (getFilter().isConExpiredLic()) {
			sql.addWhere("q755.answer < '" + DateBean.format(new Date(), "yyyy-MM-dd") + "'");
		}
		if (getFilter().getValidLicense().equals("Valid"))
			sql.addWhere("q401.dateVerified IS NOT NULL");
		if (getFilter().getValidLicense().equals("UnValid"))
			sql.addWhere("q401.dateVerified = NULL");
		if (getFilter().getValidLicense().equals("All"))
			sql.addWhere("1");

		getFilter().setShowAuditType(false);
		getFilter().setShowConLicense(true);
		getFilter().setShowExpiredLicense(true);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(true);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowStatus(false);
	}
	
	@Override
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
	
	@Override
	protected void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("id", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("name", "Contractor Name"));
		excelSheet.addColumn(new ExcelColumn("auditStatus", "PQF"));
		// Primary contact information
		excelSheet.addColumn(new ExcelColumn("contactname", "Primary Contact"));
		excelSheet.addColumn(new ExcelColumn("contactphone", "Phone"));
		excelSheet.addColumn(new ExcelColumn("contactemail", "Email"));
		// License
		excelSheet.addColumn(new ExcelColumn("answer401", "CA License", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("dateVerified401", "Verified", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("comment401", "License Comments"));
		// Expiration
		excelSheet.addColumn(new ExcelColumn("answer755", "Expiration", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("dateVerified755", "Verified", ExcelCellType.Date));
		excelSheet.addColumn(new ExcelColumn("comment755", "Expiration Comments"));
	}
}
