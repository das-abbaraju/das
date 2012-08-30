package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.excel.ExcelColumn;
import com.picsauditing.util.excel.ExcelSheet;

/**
 * This is a controller. It should delegate business concerns and persistence methods.
 */
@SuppressWarnings({ "unchecked", "serial" })
public class ReportDownload extends PicsActionSupport {
	// TODO Consider extending ReportData
	
	@Autowired
	private ReportDAO reportDao;

	private Report report;
	private String fileType = ".xls";

	private static final Logger logger = LoggerFactory.getLogger(ReportDownload.class);

	public String execute() {
		return null;
	}

	public String download() {
		try {
			ReportModel.validate(report);
		} catch (ReportValidationException rve) {
			writeJsonErrorMessage(rve);
			return JSON;
		} catch (Exception e) {
			logger.error(e.toString());
			writeJsonErrorMessage(e);
			return JSON;
		}

		try {
			if (!ReportUtil.hasColumns(report)) {
				logger.warn("User tried to download a report with no columns as an excel spreadsheet. Should we not allow that?");
				return SUCCESS;
			}

			SqlBuilder sqlBuilder = new SqlBuilder();
			SelectSQL sql = sqlBuilder.initializeSql(report.getModel(), report.getDefinition(), permissions);

			exportToExcel(report, reportDao.runQuery(sql, json));
		} catch (SQLException se) {
			logger.error(se.toString());
		} catch (IOException ioe) {
			logger.error(ioe.toString());
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
		}

		return SUCCESS;
	}

	private void exportToExcel(Report report, List<BasicDynaBean> rawData) throws Exception {
		ExcelSheet excelSheet = new ExcelSheet();
		excelSheet.setData(rawData);

		{
			SqlBuilder sqlBuilder = new SqlBuilder();
			SelectSQL sql = sqlBuilder.initializeSql(report.getModel(), report.getDefinition(), permissions);
			for (String field : sql.getFields()) {
				String alias = SelectSQL.getAlias(field);
				excelSheet.addColumn(new ExcelColumn(alias, alias));
			}
		}

		String filename = report.getName();
		excelSheet.setName(filename);

		HSSFWorkbook workbook = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

		filename += fileType;

		// TODO: Change this to use an output stream handler
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

	private void writeJsonErrorMessage(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
