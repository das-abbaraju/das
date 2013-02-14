package com.picsauditing.actions.report;

import static com.picsauditing.report.ReportJson.*;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.*;

import com.picsauditing.report.data.ReportResults;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.dao.ReportDAO;

import javax.persistence.NoResultException;

@SuppressWarnings("serial")
public class ReportApi extends PicsApiSupport {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	protected ReportService reportService;

	protected int reportId;
	protected String debugSQL = "";
	protected int limit = 50;
	protected int pageNumber = 1;
	protected boolean includeReport;
	protected boolean includeColumns;
	protected boolean includeFilters;
	protected boolean includeData;

	protected boolean editable;
	protected int shareId;
	private ReportResults reportResults;
	private Report report;

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
			reportService.favoriteReport(permissions.getUserId(), reportId);

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
			reportService.unfavoriteReport(permissions.getUserId(), reportId);

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
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			report = reportService.createOrLoadReport(reportContext);

			reportResults = reportService.buildReportResultsForPrinting(reportContext, report);
		} catch (Exception e) {
			// TODO log this
			logger.error("Error while printing report", e);
		}

		return PRINT;
	}

	public String download() {
		JSONObject payloadJson = getJsonFromRequestPayload();
		ReportContext reportContext = buildReportContext(payloadJson);

		try {
			report = reportService.createOrLoadReport(reportContext);

			reportResults = reportService.buildReportResultsForPrinting(reportContext, report);

			reportService.downloadReport(report);
		} catch (Exception e) {
			logger.error("Error while downloading report", e);
		}

		return BLANK;
	}

	public String shareWithUser() {
		try {
			reportService.shareReportWithUser(shareId, reportId, permissions, editable);

			writeJsonShareSuccess(json, getText("Report.ShareSuccess.UserMessage"));
		} catch (ReportPermissionException rpe) {
			writeJsonErrorMessage(json, rpe.getMessage());
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String shareWithGroup() {
		try {
			reportService.shareReportWithUser(shareId, reportId, permissions, editable);

			writeJsonShareSuccess(json, getText("Report.ShareSuccess.GroupMessage"));
		} catch (ReportPermissionException rpe) {
			writeJsonErrorMessage(json, rpe.getMessage());
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String shareWithAccount() {
		try {
			reportService.shareReportWithAccount(shareId, reportId, permissions);

			writeJsonShareSuccess(json, getText("Report.ShareSuccess.AccountMessage"));
		} catch (ReportPermissionException rpe) {
			writeJsonErrorMessage(json, rpe.getMessage());
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	public String unshare() {
//		int id = -1;
//		String type = "";
//		String dirtyParameter = "";
//
//		try {
//			dirtyParameter = ServletActionContext.getRequest().getParameter("id");
//			id = Integer.parseInt(dirtyParameter);
//
//			if (permissionService.canUserEditReport(permissions, reportId) && id != permissions.getUserId()) {
//				if ("user".equalsIgnoreCase(type) || "group".equalsIgnoreCase(type)) {
//					reportService.disconnectReportPermissionUser(id, reportId);
//				} else if ("account".equalsIgnoreCase(type)) {
//					reportService.disconnectReportPermissionAccount(id, reportId);
//				} else {
//					throw new IllegalArgumentException("Invalid type for unsharing.");
//				}
//
//				json.put("success", true);
//			} else {
//				json.put("success", false);
//			}
//		} catch (NumberFormatException nfe) {
//			logger.error("Bad url parameter(" + dirtyParameter + ")", nfe);
//			writeJsonException(json, nfe);
//		} catch (Exception e) {
//			logger.error("Unexpected exception", e);
//			writeJsonException(json, e);
//		}

		return JSON;
	}

	public String editPermissions() {
//		int id = -1;
//		boolean editable = false;
//		String dirtyParameter = "";
//
//		try {
//			dirtyParameter = ServletActionContext.getRequest().getParameter("id");
//			id = Integer.parseInt(dirtyParameter);
//
//			dirtyParameter = ServletActionContext.getRequest().getParameter("editable");
//			editable = Boolean.parseBoolean(dirtyParameter);
//
//			if (permissionService.canUserEditReport(permissions, report.getId())) {
//				reportService.setEditPermissions(permissions, id, report.getId(), editable);
//				json.put("success", true);
//			} else {
//				json.put("success", false);
//			}
//		} catch (NumberFormatException nfe) {
//			logger.error("Bad url parameter(" + dirtyParameter + ")", nfe);
//			writeJsonException(json, nfe);
//		} catch (Exception e) {
//			logger.error("Unexpected exception", e);
//			writeJsonException(json, e);
//		}

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

	@SuppressWarnings("unchecked")
	private void writeJsonShareSuccess(JSONObject json, String translatedMessage) {
		json.put(EXT_JS_TITLE, getText("Report.ShareSuccess.Title"));
		json.put(EXT_JS_HTML, translatedMessage);
		json.put(EXT_JS_SUCCESS, true);
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

	public void setShareId(int shareId) {
		this.shareId = shareId;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public ReportResults getReportResults() {
		return reportResults;
	}

	public Report getReport() {
		return report;
	}
}
