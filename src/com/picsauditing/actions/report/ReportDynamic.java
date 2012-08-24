package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.autocomplete.ReportFilterAutocompleter;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelSheet;

/**
 * This is a controller. Do not use any DAOs from its parent. This should
 * delegate business concerns and persistence methods.
 */
@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportFilterAutocompleter reportFilterAutocompleter;

	// TODO remove this boolean flag
	private static final boolean FOR_DOWNLOAD = true;

	private Report report;
	private int pageNumber = 1;
	private SqlBuilder sqlBuilder = new SqlBuilder();
	private String fileType = ".xls";

	private String fieldName = "";
	private String searchQuery = "";

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);

	public String execute() {
		String status = SUCCESS;

		if (report == null) {
			// No matter what junk we get in the url, redirect
			try {
				status = setUrlForRedirect(ManageReports.MY_REPORTS_URL);

				String dirtyReportIdParameter = ServletActionContext.getRequest().getParameter("report");
				// Don't trust user input!
				int reportId = Integer.parseInt(dirtyReportIdParameter);

				if (!reportModel.canUserViewAndCopy(permissions.getUserId(), reportId)) {
					String errorMessage = "You do not have permissions to view that report.";
					ActionContext.getContext().getSession().put("errorMessage", errorMessage);
				}
			} catch (NumberFormatException nfe) {
				// Someone typed junk into the url
				logger.warn(nfe.toString());
			} catch (IOException ioe) {
				// Someone typed junk into the url
				logger.warn("Problem with setUrlForRedirect() for not logged in user.", ioe);
			} catch (Exception e) {
				// Probably a null pointer
				logger.error(e.toString());
			}
		}

		return status;
	}

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
			writeJsonErrorMessage(e);
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
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String data() {
		try {
			ReportModel.validate(report);

			// TODO remove definition from SqlBuilder
			sqlBuilder.setDefinition(report.getDefinition());

			SelectSQL sql = sqlBuilder.buildSql(report, permissions, pageNumber);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable());

			if (ReportUtil.hasColumns(report)) {
				List<BasicDynaBean> queryResults = reportDao.runQuery(sql, json);

				JSONArray queryResultsAsJson = ReportUtil.convertQueryResultsToJson(queryResults, availableFields,
						permissions, permissions.getLocale());
				json.put("data", queryResultsAsJson);

				json.put("success", true);
			}
		} catch (ReportValidationException rve) {
			writeJsonError(rve);
		} catch (SQLException se) {
			writeJsonError(se);
		} catch (Exception e) {
			writeJsonError(e);
		}

		return JSON;
	}

	public String list() {
		try {
			if (Strings.isEmpty(fieldName))
				throw new Exception("Please pass a fieldName when calling list");

			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable());
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			if (field.getFilterType().isEnum()) {
				json = field.renderEnumFieldAsJson(permissions.getLocale());
			} else if (field.getFilterType().isAutocomplete()) {
				json = reportFilterAutocompleter.getFilterAutocompleteResultsJSON(field.getAutocompleteType(),
						searchQuery, permissions);
			} else if (field.getFilterType().isLowMedHigh()) {
				json = field.renderLowMedHighFieldAsJson(permissions.getLocale());
			} else {
				throw new Exception(field.getFilterType() + " not supported by list function.");
			}

			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String translateValue() {
		try {
			if (Strings.isEmpty(fieldName))
				throw new Exception("Please pass a fieldName when calling list");

			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable());
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			JSONArray jsonArray = new JSONArray();

			String translatedString = ReportUtil.getText(field.getI18nKey(searchQuery), permissions.getLocale());

			JSONObject translationMap = new JSONObject();
			translationMap.put("key", searchQuery);

			if (translatedString == null) {
				translatedString = searchQuery;
			}

			translationMap.put("value", translatedString);

			jsonArray.add(translationMap);

			json.put("result", jsonArray);

			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String configuration() {
		int userId = permissions.getUserId();

		json.put("editable", reportModel.canUserEdit(userId, report));

		return JSON;
	}

	public String report() {
		try {
			ReportModel.validate(report);

			reportDao.updateLastOpened(permissions.getUserId(), report.getId());

			// TODO remove definition from SqlBuilder
			sqlBuilder.setDefinition(report.getDefinition());

			// TODO find out what else this method is doing besides building sql
			sqlBuilder.buildSql(report, permissions, pageNumber);

			ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (ReportValidationException rve) {
			logger.warn("Invalid report in ReportDynamic.report()", rve);
			writeJsonErrorMessage(rve);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String availableFields() {
		try {
			ReportModel.validate(report);

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(report.getTable());

			json.put("modelType", report.getModelType().toString());
			json.put("fields", ReportUtil.translateAndJsonify(availableFields, permissions, permissions.getLocale()));
			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonErrorMessage(e);
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
				reportDao.connectReportToUser(report, userId);
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
				reportDao.connectReportToUserEditable(report, userId);
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

			// TODO remove definition from SqlBuilder
			sqlBuilder.setDefinition(report.getDefinition());

			// TODO remove FOR_DOWNLOAD boolean flag
			SelectSQL sql = sqlBuilder.buildSql(report, permissions, pageNumber, FOR_DOWNLOAD);

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

		excelSheet = sqlBuilder.extractColumnsToExcel(excelSheet);

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

	// TODO: Refactor, because it seems just like the jsonException method.
	private void writeJsonError(Exception e) {
		json.put("success", false);
		String message = e.getMessage();

		if (message == null) {
			message = e.toString();
		}

		json.put("message", message);
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setPage(int page) {
		this.pageNumber = page;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
}
