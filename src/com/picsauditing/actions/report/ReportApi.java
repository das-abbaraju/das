package com.picsauditing.actions.report;

import static com.picsauditing.report.ReportJson.REPORT_ID;
import static com.picsauditing.report.ReportJson.writeJsonException;
import static com.picsauditing.report.ReportJson.writeJsonSuccess;

import java.util.List;

import javax.persistence.NoResultException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.WidgetUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.WidgetUser;
import com.picsauditing.report.PicsSqlException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.data.ReportResults;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.service.ReportPreferencesService;
import com.picsauditing.service.ReportService;

@SuppressWarnings("serial")
public class ReportApi extends PicsApiSupport {

	@Autowired
	protected ReportService reportService;
	@Autowired
	private ReportPreferencesService reportPreferencesService;
	@Autowired
	private WidgetUserDAO widgetUserDAO;

	protected int reportId;
	protected int widgetId;
	protected String debugSQL = "";
	protected int limit = 50;
	protected int pageNumber = 1;
	protected boolean includeReport;
	protected boolean includeColumns;
	protected boolean includeFilters;
	protected boolean includeData;

	private ReportResults reportResults;
	private Report report;
	private String reportJson;

	private ModelType type;
	private String fieldId;

	private static final String PRINT = "print";
	private static final Logger logger = LoggerFactory.getLogger(ReportApi.class);

	public String execute() throws Exception {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			json = reportService.buildJsonResponse(reportContext);
		} catch (ReportValidationException rve) {
			logger.error("Invalid report in ReportApi.execute()", rve);
			writeJsonException(json, rve);
		} catch (PicsSqlException pse) {
			handleSqlException(pse);
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String copy() {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			Report newReport = reportService.copy(reportContext);

			writeJsonCopySuccess(json, newReport.getId());
		} catch (NoRightsException nre) {
			writeJsonException(json, nre);
		} catch (Exception e) {
			logger.error("An error occurred copying report id = {} for user {}", reportId, permissions.getUserId());
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String save() {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			reportService.save(reportContext);

			writeJsonSuccess(json);
		} catch (NoRightsException nre) {
			writeJsonException(json, nre);
		} catch (Exception e) {
			logger.error("An error occurred saving report id = {} for user {}", reportId, permissions.getUserId());
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String favorite() {
		try {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), reportId);

			reportPreferencesService.favoriteReport(reportUser);

			writeJsonSuccess(json);
		} catch (NoResultException nre) {
			writeJsonException(json, nre);
			logger.error(nre.toString());
		} catch (Exception e) {
			writeJsonException(json, e);
			logger.error("Uncaught exception in ReportApi.favorite(). ", e);
		}

		return JSON;
	}

	public String unfavorite() {
		try {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(permissions.getUserId(), reportId);

			reportPreferencesService.unfavoriteReport(reportUser);

			writeJsonSuccess(json);
		} catch (NoResultException nre) {
			writeJsonException(json, nre);
			logger.error(nre.toString());
		} catch (Exception e) {
			writeJsonException(json, e);
			logger.error("Uncaught exception in ReportApi.unfavorite(). ", e);
		}

		return JSON;
	}

	public String print() {
		JSONObject payloadJson = (JSONObject) JSONValue.parse(reportJson);
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			report = reportService.buildReportFromJson(payloadJson, reportId);

			reportResults = reportService.buildReportResultsForPrinting(reportContext, report);
		} catch (Exception e) {
			logger.error("Error while printing report", e);
		}

		return PRINT;
	}

	public String download() {
		JSONObject payloadJson = (JSONObject) JSONValue.parse(reportJson);
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			report = reportService.buildReportFromJson(payloadJson, reportId);

			reportResults = reportService.buildReportResultsForPrinting(reportContext, report);

			reportService.downloadReport(report, reportResults);
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return BLANK;
	}

	public String info() {
		try {
			json = reportService.buildJsonReportInfo(reportId);
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return JSON;
	}

	public String chart() {
		ReportContext reportContext = buildReportContext(null);

		try {
			List<WidgetUser> widgetUsers = widgetUserDAO.findByUser(permissions);
			for (WidgetUser user : widgetUsers) {

				if (user.getWidget() != null && user.getWidget().getWidgetID() == widgetId) {
					reportId = user.getWidget().getReportID().intValue();
					break;
				}
			}

			if (reportId != 0) {
				report = reportService.loadReportFromDatabase(reportId);

				json = reportService.buildReportResultsForChart(reportContext, report);
			}
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return JSON;
	}

	public String buildSqlFunctions() {
		try {
			json = reportService.buildSqlFunctionsJson(type, fieldId, permissions);

			writeJsonSuccess(json);
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	private void handleSqlException(PicsSqlException sqlException) throws Exception {
		writeJsonException(json, sqlException);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			logger.error("Report:" + reportId + " " + sqlException.getMessage() + " SQL: " + sqlException.getSql());
		}
	}

	protected ReportContext buildReportContext(JSONObject payloadJson) {
		ReportContext reportContext = new ReportContext(payloadJson, reportId, getUser(), permissions, includeReport,
				includeData, includeColumns, includeFilters, limit, pageNumber);
		return reportContext;
	}

	@SuppressWarnings("unchecked")
	private void writeJsonCopySuccess(JSONObject json, int newReportId) {
		writeJsonSuccess(json);
		json.put(REPORT_ID, newReportId);
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

	public void setWidgetId(int widgetId) {
		this.widgetId = widgetId;
	}

	public ReportResults getReportResults() {
		return reportResults;
	}

	public Report getReport() {
		return report;
	}

	public void setType(ModelType type) {
		this.type = type;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public void setReportJson(String reportJson) {
		this.reportJson = reportJson;
	}

}
