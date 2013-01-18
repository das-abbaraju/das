package com.picsauditing.actions.report;

import static com.picsauditing.report.ReportJson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.converter.AvailableFieldsToExtJSConverter;
import com.picsauditing.report.converter.ExtJSToReportConverter;
import com.picsauditing.report.converter.ReportToExtJSConverter;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelBuilder;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportApi extends PicsApiSupport {

	private static final String PRINT = "print";

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportModel reportModel;

	private int reportId;
	protected Report report;
	protected String debugSQL = "";
	private SelectSQL sql = null;
	protected ReportDataConverter converter;
	private int limit = 100;
	private int pageNumber = 1;
	private boolean includeReport;
	private boolean includeColumns;
	private boolean includeFilters;
	private boolean includeData;

	private static final Logger logger = LoggerFactory.getLogger(ReportApi.class);

	protected void initialize() throws Exception {
		// if data is based on report in database then lookup from db (no json paramters present)
		// if modified report(incoming json is present) then build report from json
		// validate
		// update last viewed

		JSONObject jsonReport = attemptToGetReportDataFromRequest();
		if (shouldLoadReportFromJson(jsonReport)) {
			buildReportFromJson(jsonReport);
		} else {
			loadReportFromDatabase(reportId);
		}

		ReportModel.validate(report);
		reportModel.updateLastViewedDate(getUser(), report);

		// FIXME: This is a problem that will cause a Ninja save that the refresh above
		//        was fixing
//		if (StringUtils.isNotEmpty(reportParameters)) {
//			report.setParameters(reportParameters);
//		}

		sql = new SqlBuilder().initializeSql(report, permissions);
		logger.debug("Running report {0} with SQL: {1}", report.getId(), sql.toString());

		ReportUtil.addTranslatedLabelsToReportParameters(report, permissions.getLocale());
	}

	private JSONObject attemptToGetReportDataFromRequest() {
		JSONObject json = getJsonFromRequestPayload();
		JSONObject jsonReport = null;

		if (JSONUtilities.isNotEmpty(json)) {
			jsonReport = (JSONObject) json.get(ReportJson.LEVEL_REPORT);
		}

		return jsonReport;
	}

	private void loadReportFromDatabase(int reportId) throws Exception {
		report = reportDao.find(Report.class, reportId);

		if (report == null) {
			throw new RecordNotFoundException("Report " + reportId + " was not found in the database");
		}

		reportModel.legacyConvertParametersToReport(report);
	}

	private void buildReportFromJson(JSONObject jsonReport) {
		report = ExtJSToReportConverter.convertToReport(jsonReport);
		report.setId(reportId);
	}

	private boolean shouldLoadReportFromJson(JSONObject reportJson) {
		return JSONUtilities.isNotEmpty(reportJson) && includeData;
	}

	public String execute() {
		try {
			initialize();

			if (includeReport) {
				json.put(LEVEL_REPORT, ReportToExtJSConverter.toJSON(report));
			}

			AbstractModel model = ModelFactory.build(report.getModelType(), permissions);
			if (includeColumns) {
				json.put(LEVEL_COLUMNS, AvailableFieldsToExtJSConverter.getColumns(model, permissions));
			}

			if (includeFilters) {
				json.put(LEVEL_FILTERS, AvailableFieldsToExtJSConverter.getFilters(model, permissions));
			}

			if (includeData) {
				json.put(LEVEL_RESULTS, getData());
			}

			json.put(ReportJson.EXT_JS_SUCCESS, true);
		} catch (ReportValidationException rve) {
			logger.error("Invalid report in ReportDynamic.report()", rve);
			writeJsonError(rve);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String sqlFunctions() {
		Map<String, String> map = ReportUtil.getTranslatedFunctionsForField(permissions.getLocale(), null);
		json.put("functions", ReportUtil.convertTranslatedFunctionstoJson(map));
		return SUCCESS;
	}

	private boolean includeSql() {
		return (permissions.isAdmin() || permissions.getAdminID() > 0);
	}

	private JSONObject getData() throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (includeSql()) {
			String debugSQL = sql.toString().replace("\n", " ").replace("  ", " ");
			jsonObject.put(ReportUtil.SQL, debugSQL);
		}

		try {
			sql.setPageNumber(limit, pageNumber);
			runQuery();

			// TODO have this take something or return something
			converter.convertForExtJS();
			ReportResults reportResults = converter.getReportResults();
			jsonObject.put(LEVEL_DATA, reportResults.toJson());
			jsonObject.put(RESULTS_TOTAL, json.get(RESULTS_TOTAL));
			json.remove(RESULTS_TOTAL);
		} catch (Exception error) {
			handleErrorForData(error);
		}

		return jsonObject;
	}

	private void handleErrorForData(Exception error) throws Exception {
		logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + sql);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			throw new Exception(error + " debug SQL: " + sql.toString());
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
		List<BasicDynaBean> queryResults = reportDao.runQuery(sql.toString(), json);
		converter = new ReportDataConverter(report.getColumns(), queryResults);
		converter.setLocale(permissions.getLocale());
	}

	protected void writeJsonError(Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		writeJsonError(message);
	}

	protected void writeJsonError(String message) {
		json.put(ReportJson.EXT_JS_SUCCESS, false);
		json.put(ReportJson.EXT_JS_MESSAGE, message);
	}

	private JSONObject getJsonFromRequestPayload() {
		JSONObject jsonObject = new JSONObject();
		HttpServletRequest request = getRequest();
		if (request == null) {
			return jsonObject;
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = request.getReader();
			jsonObject = parseJsonFromRequest(bufferedReader, request);
		} catch (Exception e) {
			logger.error("There was an error parsing the JSON from the request", e);
		} finally {
			closeBufferedReader(bufferedReader);
		}

		return jsonObject;
	}

	private void closeBufferedReader(BufferedReader bufferedReader) {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e) {
			logger.error("There was an error closing the bufferedReader", e);
		}
	}

	private JSONObject parseJsonFromRequest(BufferedReader bufferedReader, HttpServletRequest request) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (bufferedReader == null) {
			return jsonObject;
		}

		StringBuilder jsonString = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			jsonString.append(line);
		}

		if (Strings.isEmpty(jsonString.toString())) {
			return jsonObject;
		}

		return (JSONObject) JSONValue.parse(jsonString.toString());
	}

	public Report getReport() {
		return report;
	}

//	public void setReport(Report report) {
//		this.report = report;
//	}

	public ReportResults getResults() {
		return converter.getReportResults();
	}

	public void setPage(int page) {
		this.pageNumber = page;
	}

	public void setLimit(int limit) {
		this.limit = limit;
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

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}
