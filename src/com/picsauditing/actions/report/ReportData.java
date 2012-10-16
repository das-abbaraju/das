package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.NoResultException;
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
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.excel.ExcelBuilder;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportData extends PicsActionSupport {
	private static final String PRINT = "print";

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportUserDAO reportUserDao;

	private Report report;

	private String debugSQL = "";
	private SelectSQL sql = null;
	private ReportDataConverter converter;

	private int pageNumber = 1;

	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);

	private String initialize() throws Exception {
		logger.debug("initializing report " + report.getId());
		ReportModel.validate(report);
		Report reportFromDb =reportDao.find(Report.class, report.getId());
		reportDao.refresh(reportFromDb);
		reportModel.updateLastViewedDate(permissions.getUserId(), reportFromDb);
		sql = new SqlBuilder().initializeSql(report, permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

		// Not sure if we need this anymore
		json.put("reportID", report.getId());
		return SUCCESS;
	}

	public String report() {
		try {
			initialize();
			configuration();

			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (ReportValidationException rve) {
			logger.warn("Invalid report in ReportDynamic.report()", rve);
			writeJsonError(rve);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String configuration() {
		json.put("favorite", false);
		json.put("editable", false);

		try {
			ReportUser reportUser = reportUserDao.findOne(permissions.getUserId(), report.getId());
			json.put("editable", reportModel.canUserEdit(permissions, report));
			json.put("favorite", reportUser.isFavorite());
		} catch (NoResultException e) {
			logger.info("No ReportUser entry for " + permissions.getUserId() + " AND " + report.getId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.configuration()", e);
		}

		return JSON;
	}

	public String extjs() throws Exception {
		data();
		if (this.pageNumber == 1)
			report();
		
		return JSON;
	}

	public String data() throws Exception {
		try {
			initialize();
			sql.setPageNumber(report.getRowsPerPage(), pageNumber);
			runQuery();

			converter.convertForExtJS();
			json.put("data", converter.getReportResults().toJson());

			if (permissions.isAdmin() || permissions.getAdminID() > 0) {
				json.put("sql", debugSQL);
			}
			
			json.put("success", true);
		} catch (ReportValidationException error) {
			writeJsonError(error.getMessage());
		} catch (Exception error) {
			logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + debugSQL);
			if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
				writeJsonError(error);
				json.put("sql", debugSQL);
			} else {
				writeJsonError("Invalid Query");
			}
		}

		return JSON;
	}

	public String print() throws Exception {
		initialize();
		runQuery();
		converter.convertForPrinting();

		return PRINT;
	}

	public String download() {
		try {
			initialize();
			runQuery();
			converter.convertForPrinting();
			HSSFWorkbook workbook = buildWorkbook();
			writeFile(report.getName() + ".xls", workbook);
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}
		
		return BLANK;
	}

	private HSSFWorkbook buildWorkbook() {
		logger.info("Building XLS File");
		ExcelBuilder builder = new ExcelBuilder();
		builder.addColumns(report.getDefinition().getColumns());
		builder.addSheet(report.getName(), converter.getReportResults());
		return builder.getWorkbook();
	}

	private void writeFile(String filename, HSSFWorkbook workbook) throws IOException {
		logger.info("Streaming XLS File to response");
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workbook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

	private void runQuery() throws SQLException {
		debugSQL = sql.toString().replace("\n", " ").replace("  ", " ");

		List<BasicDynaBean> queryResults = reportDao.runQuery(debugSQL, json);
		converter = new ReportDataConverter(report.getDefinition().getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
	}

	private void writeJsonError(Exception e) {
		e.printStackTrace();
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		writeJsonError(message);
	}

	private void writeJsonError(String message) {
		json.put("success", false);
		json.put("message", message);
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ReportResults getResults() {
		return converter.getReportResults();
	}

	public void setPage(int page) {
		this.pageNumber = page;
	}
}
