package com.picsauditing.actions.report;

import static com.picsauditing.report.ReportJson.*;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.util.Strings;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.PicsSqlException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportService;
import com.picsauditing.report.ReportValidationException;

import javax.persistence.NoResultException;
import java.io.IOException;

@SuppressWarnings("serial")
public class ReportApi extends PicsApiSupport {

	private static final String PRINT = "print";

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	protected ReportService reportService;

	protected int reportId;
	protected String debugSQL = "";
	protected int limit = 50;
	protected int pageNumber = 1;
	protected boolean favorite;
	protected boolean includeReport;
	protected boolean includeColumns;
	protected boolean includeFilters;
	protected boolean includeData;

	private static final Logger logger = LoggerFactory.getLogger(ReportApi.class);
	private static final String LANDING_URL = "ReportApi!favoritesList.action"; // todo: move from ManageReports.favoritesList()

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
			Report newReport = reportService.copy(reportContext, favorite);

			writeJsonSuccess(json);
			json.put(REPORT_ID, newReport.getId());
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
			reportService.favoriteReport(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ReportApi.favorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String unfavorite() {
		try {
			reportService.unfavoriteReport(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			logger.error(nre.toString());
		} catch (Exception e) {
			logger.error("Uncaught exception in ReportApi.unfavorite(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveFavoriteUp() {
		int positionChange = -1;
		try {
			reportService.moveFavorite(permissions.getUserId(), reportId, positionChange);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteUp()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteUp(). ", e);
		}

		return redirectToPreviousView();
	}

	public String moveFavoriteDown() {
		int positionChange = 1;
		try {
			reportService.moveFavorite(permissions.getUserId(), reportId, positionChange);
		} catch (NoResultException nre) {
			logger.warn("No result found in ReportApi.moveFavoriteDown()", nre);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportApi.moveFavoriteDown(). ", e);
		}

		return redirectToPreviousView();
	}

	public String print() throws Exception {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		json = reportService.buildJsonResponse(reportContext);
		reportService.convertForPrinting();

		return PRINT;
	}

	public String download(Report report) {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			json = reportService.buildJsonResponse(reportContext);
			reportService.downloadReport(report);
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return BLANK;
	}

	private String redirectToPreviousView() {
		try {
			String referer = getRequest().getHeader("Referer");
			if (Strings.isEmpty(referer)) {
				referer = LANDING_URL;
			}

			setUrlForRedirect(referer);
		} catch (IOException e) {
			logger.warn("Problem setting URL for redirect in ManageReports.redirectToPreviousView()", e);
		} catch (Exception e) {
			logger.error("Unexpected problem in ManageReports.redirectToPreviousView()");
		}

		return REDIRECT;
	}

	private void handleSqlException(PicsSqlException sqlException) throws Exception {
		writeJsonException(json, sqlException);

		if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
			logger.error("Report:" + reportId + " " + sqlException.getMessage() + " SQL: " + sqlException.getSql());
		}
	}

	protected ReportContext buildReportContext(JSONObject payloadJson) {
		ReportContext reportContext = new ReportContext(payloadJson, reportId, getUser(), permissions, includeReport,
				includeData, includeColumns, includeFilters, limit, pageNumber );
		return reportContext;
	}

	public void setPage(int page) {
		this.pageNumber = page;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
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
