package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
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
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

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
			Report newReport = reportModel.copy(report, permissions);
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
			ReportUser reportUser = reportUserDao.findOne(userId, report.getId());
			favorite = reportUser.isFavorite();
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

			reportModel.updateLastViewedDate(permissions.getUserId(), report);

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
		boolean editable = false;
		int userId = -1;
		String dirtyReportIdParameter = "";

		try {
			dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("userId");
			// Don't trust user input!
			userId = Integer.parseInt(dirtyReportIdParameter);

			if (reportModel.canUserViewAndCopy(permissions, report.getId())) {
				reportModel.connectReportPermissionUser(report.getId(), userId, editable);
				json.put("success", true);
			} else {
				json.put("success", editable);
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
		boolean editable = true;
		int userId = -1;
		String dirtyReportIdParameter = "";

		try {
			dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("userId");
			// Don't trust user input!
			userId = Integer.parseInt(dirtyReportIdParameter);

			if (reportModel.canUserEdit(permissions.getUserId(), report)) {
				reportModel.connectReportPermissionUser(report.getId(), userId, editable);
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

	public String translateValues() {
		try {
			ReportModel.validate(report);

			String fieldName = getRequest().getParameter("fieldName");
			if (Strings.isEmpty(fieldName))
				throw new Exception("You need to pass a fieldName to get the translated field values.");

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable(), permissions);
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			String[] untranslatedValues = getRequest().getParameterValues("value");
			List<String> translatedValues = new ArrayList<String>();

			for (String value : untranslatedValues) {
				String key = field.getI18nKey(value);
				translatedValues.add(getText(key));
			}

			json.put("values", translatedValues);
			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.translateValues()", e);
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
