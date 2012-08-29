package com.picsauditing.actions.report;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportUserDAO reportUserDao;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);

	public String create() {
		try {
			Report newReport = reportModel.copy(report, new User(permissions.getUserId()));
			json.put("success", true);
			json.put("reportID", newReport.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			logger.error("An error occurred while copying a report for user {}", permissions.getUserId(), e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String edit() {
		try {
			reportModel.edit(report, permissions);
			json.put("success", true);
			json.put("reportID", report.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			logger.error("An error occurred while editing a report id = {} for user {}", report.getId(),
					permissions.getUserId());
			writeJsonError(e);
		}

		return JSON;
	}

	public String configuration() {
		int userId = permissions.getUserId();
		boolean editable = false, favorite = false;

		try {
			editable = reportModel.canUserEdit(userId, report);
			ReportUser userReport = reportUserDao.findOne(userId, report.getId());
			favorite = userReport.isFavorite();
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.configuration()", e);
		}

		json.put("editable", editable);
		json.put("favorite", favorite);

		return JSON;
	}

	public String report() {
		try {
			ReportModel.validate(report);

			reportUserDao.updateLastOpened(permissions.getUserId(), report.getId());

			SqlBuilder sqlBuilder = new SqlBuilder();
			sqlBuilder.initializeSql(report.getModel(), report.getDefinition(), permissions);

			ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

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

	public String availableFields() {
		try {
			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable(), permissions);

			json.put("modelType", report.getModelType().toString());
			json.put("fields", ReportUtil.translateAndJsonify(availableFields, permissions, permissions.getLocale()));
			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String share() {
		int userId = -1;
		String dirtyReportIdParameter = "";

		try {
			dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("userId");
			// Don't trust user input!
			userId = Integer.parseInt(dirtyReportIdParameter);

			if (reportModel.canUserViewAndCopy(permissions.getUserId(), report.getId())) {
				reportModel.connectReportToUser(report, userId);
				json.put("success", true);
			} else {
				json.put("success", false);
			}
		} catch (NumberFormatException nfe) {
			logger.error("Bad url parameter(" + dirtyReportIdParameter + ") passed to ReportDynamic.report()", nfe);
			writeJsonError(nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String shareEditable() {
		int userId = -1;
		String dirtyReportIdParameter = "";

		try {
			dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("userId");
			// Don't trust user input!
			userId = Integer.parseInt(dirtyReportIdParameter);

			if (reportModel.canUserEdit(permissions.getUserId(), report)) {
				reportModel.connectReportToUserEditable(report, userId);
				json.put("success", true);
			} else {
				json.put("success", false);
			}
		} catch (NumberFormatException nfe) {
			logger.error("Bad url parameter(" + dirtyReportIdParameter + ") passed to ReportDynamic.report()", nfe);
			writeJsonError(nfe);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	private void writeJsonError(Exception e) {
		json.put("success", false);
		json.put("message", e.toString());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
}
