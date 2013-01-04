package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.excel.ExcelBuilder;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportData extends PicsApiSupport {

	private static final String PRINT = "print";

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportModel reportModel;

	protected Report report;
	protected String debugSQL = "";
	private SelectSQL sql = null;
	protected ReportDataConverter converter;
	private int limit = 100;
	private int pageNumber = 1;
	private String reportParameters;
	private boolean includeReport;
	private boolean includeColumns;
	private boolean includeFilters;
	private boolean includeData;

	private static final Logger logger = LoggerFactory.getLogger(ReportData.class);

	protected String initialize() throws Exception {
		logger.debug("initializing report " + report.getId());
		ReportModel.validate(report);
		Report reportFromDb = reportDao.find(Report.class, report.getId());
		reportDao.refresh(reportFromDb);
		ReportModel.processReportParameters(report);
		reportModel.updateLastViewedDate(permissions.getUserId(), reportFromDb);

		if (!StringUtils.isEmpty(reportParameters))
			reportFromDb.setParameters(reportParameters);
		
		sql = new SqlBuilder().initializeSql(report, permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report, permissions.getLocale());

		// Not sure if we need this anymore
		json.put("reportID", report.getId());
		return SUCCESS;
	}

	public String execute() {
		try {
			initialize();

			if (includeReport) {
				json.put(ReportUtil.REPORT, report.toJSON(true));
			}

			if (includeColumns) {
				json.put(ReportUtil.COLUMNS, "");
			}

			if (includeFilters) {
				json.put(ReportUtil.FILTERS, "");
			}

			if (includeData) {
				json.put(ReportUtil.REPORT_DATA, getData());
			}

			if (includeSql()) {
				json.put(ReportUtil.SQL, debugSQL);
			}

			json.put(ReportUtil.SUCCESS, true);
		} catch (ReportValidationException rve) {
			logger.warn("Invalid report in ReportDynamic.report()", rve);
			writeJsonError(rve);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	private boolean includeSql() {
		return (permissions.isAdmin() || permissions.getAdminID() > 0);
	}

	private JSONObject getData() throws Exception {
		JSONObject jsonObject = new JSONObject();
		try {
			sql.setPageNumber(limit, pageNumber);
			runQuery();

			converter.convertForExtJS();
			ReportResults reportResults = converter.getReportResults();
			jsonObject.put(ReportUtil.DATA, reportResults.toJson());
			jsonObject.put(ReportUtil.TOTAL, reportResults.size());
		} catch (Exception error) {
			handleErrorForData(error);
		}

		return jsonObject;
	}

	private void handleErrorForData(Exception error) throws Exception {
		logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + debugSQL);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			throw new Exception(error + " debug SQL: " + debugSQL);
		} else {
			throw new Exception("Invalid Query");
		}
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
		builder.addColumns(report.getColumns());
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

	protected void runQuery() throws SQLException {
		debugSQL = sql.toString().replace("\n", " ").replace("  ", " ");

		List<BasicDynaBean> queryResults = reportDao.runQuery(debugSQL, json);
		converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
	}

	protected void writeJsonError(Exception e) {
		// TODO decide if this should be logged, but it shouldn't be rather than
		// output to STDOUT
		// e.printStackTrace();
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		writeJsonError(message);
	}

	protected void writeJsonError(String message) {
		json.put(ReportUtil.SUCCESS, false);
		json.put(ReportUtil.MESSAGE, message);
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

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setReportParameters(String reportParameters) {
		this.reportParameters = reportParameters;
	}

	public void setIncludeReport(boolean includeReport) {
		this.includeReport = includeReport;
	}

	public void setIncludeColumns(boolean includeColumns) {
		this.includeColumns = includeColumns;
	}

	public void setIncludeFilters(boolean includeFilters) {
		this.includeFilters = includeFilters;
	}

	public void setIncludeData(boolean includeData) {
		this.includeData = includeData;
	}
}
