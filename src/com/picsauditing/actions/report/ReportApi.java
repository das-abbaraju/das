package com.picsauditing.actions.report;

import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.access.OpPerms;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.PicsSqlException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportService;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.util.Strings;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportApi extends PicsApiSupport {

	private static final String PRINT = "print";

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	protected ReportService reportService;

	protected int reportId;
	protected String debugSQL = "";
	protected ReportDataConverter converter;
	protected int limit = 50;
	protected int pageNumber = 1;
	protected boolean includeReport;
	protected boolean includeColumns;
	protected boolean includeFilters;
	protected boolean includeData;

	private static final Logger logger = LoggerFactory.getLogger(ReportApi.class);

	public String execute() throws Exception {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			json = reportService.buildJsonResponse(reportContext);
		} catch (ReportValidationException rve) {
			logger.error("Invalid report in ReportDynamic.report()", rve);
			writeJsonError(rve);
		} catch (PicsSqlException pse) {
			handleSqlException(pse);
		} catch (Exception e) {
			// TODO remove this
			e.printStackTrace();
			writeJsonError(e);
		}

		return JSON;
	}

	public String print() throws Exception {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);
		json = reportService.buildJsonResponse(reportContext);
		reportService.convertForPrinting();

		return PRINT;
	}

	public String download(Report report) {
		try {
			JSONObject payloadJson = getJsonFromRequestPayload();
			ReportContext reportContext = buildReportContext(payloadJson);
			json = reportService.buildJsonResponse(reportContext);
			reportService.downloadReport(report);

		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return BLANK;
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

	protected JSONObject getJsonFromRequestPayload() {
		JSONObject jsonObject = new JSONObject();
		HttpServletRequest request = getRequest();
		if (request == null) {
			return jsonObject;
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = request.getReader();
			jsonObject = parseJsonFromInput(bufferedReader);
		} catch (Exception e) {
			logger.error("There was an sqlException parsing the JSON from the request", e);
		} finally {
			closeBufferedReader(bufferedReader);
		}

		return jsonObject;
	}

	private void handleSqlException(PicsSqlException sqlException) throws Exception {
		writeJsonError(sqlException);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			logger.error("Report:" + reportId + " " + sqlException.getMessage() + " SQL: " + sqlException.getSql());
		}
	}

	private void closeBufferedReader(BufferedReader bufferedReader) {
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e) {
			logger.error("There was an sqlException closing the bufferedReader", e);
		}
	}

	private JSONObject parseJsonFromInput(BufferedReader bufferedReader) throws Exception {
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

	protected ReportContext buildReportContext(JSONObject payloadJson) {
		ReportContext reportContext = new ReportContext(payloadJson, reportId, getUser(), permissions, includeReport,
				includeData, includeColumns, includeFilters, limit, pageNumber );
		return reportContext;
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
