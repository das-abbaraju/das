package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.autocomplete.ReportFilterAutocompleter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.business.ReportController;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.DynamicReportUtil;
import com.picsauditing.util.excel.ExcelSheet;

@SuppressWarnings( { "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {

	@Autowired
	private ReportFilterAutocompleter reportFilterAutocompleter;

	private static final boolean FOR_DOWNLOAD = true;

	private Report report;
	private int pageNumber = 1;
	private boolean showSQL;
	private SelectSQL sql = new SelectSQL();
	private SqlBuilder sqlBuilder = new SqlBuilder();
	private String fileType = ".xls";

	private String fieldName = "";
	private String searchQuery = "";

	@Autowired
	ReportController reportController;

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);

	public String execute() {
		String status = SUCCESS;

		if (report == null) {
			try {
				// No matter what junk we get in the url, redirect
				status = setUrlForRedirect("ManageReports.action?viewType=saved");

				String reportId = ServletActionContext.getRequest().getParameter("report");

				if (!DynamicReportUtil.canUserViewAndCopy(permissions.getUserId(), Integer.parseInt(reportId))) {
					String errorMessage = "You do not have permissions to view that report.";
					ActionContext.getContext().getSession().put("errorMessage", errorMessage);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		return status;
	}

	@Deprecated
	public String find() {
		try {
			reportController.validate(report);
			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (Exception e) {
			logger.error("An error occurred while trying to do a find", e);
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String create() {
		try {
			Report newReport = reportController.copy(report, permissions);
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
			reportController.edit(report, permissions);
			json.put("success", true);
			json.put("reportID", report.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			logger.error("An error occurred while editing a report id = {} for user {}",
					report.getId(), permissions.getUserId());
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String data() {
		try {
			reportController.validate(report);

			Definition definition = new Definition(report.getParameters());
			report.setDefinition(definition);
			// TODO remove definition from SqlBuilder
			sqlBuilder.setDefinition(definition);

			sql = sqlBuilder.buildSql(report, permissions, pageNumber);

			translateFilterValueNames(definition.getFilters());

			Map<String, Field> availableFields = ReportController.buildAvailableFields(report.getTable());

			if (definition.getColumns().size() > 0) {
				long startTime = Calendar.getInstance().getTimeInMillis();

				List<BasicDynaBean> queryResults = reportController.runQuery(sql, json);
				convertToJson(queryResults, availableFields);
				json.put("success", true);

				long queryTime = Calendar.getInstance().getTimeInMillis() - startTime;
				if (queryTime > 1000) {
					showSQL = true;
					logger.info("Slow Query: {}", sql.toString());
					logger.info("Time to query: {} ms", queryTime);
				}
			}
		} catch (SQLException e) {
			logError(e);
		} catch (Exception e) {
			logError(e);
		} finally {
			if (showSQL && (permissions.isPicsEmployee() || permissions.getAdminID() > 0)) {
				json.put("sql", sql.toString().replace("`", "").replace("\n", " "));
				json.put("base", report.getModelType().toString());
			}
		}

		return JSON;
	}

	public String list() {
		try {
			if (Strings.isEmpty(fieldName))
				throw new Exception("Please pass a fieldName when calling list");

			reportController.validate(report);

			Map<String, Field> availableFields = ReportController.buildAvailableFields(report.getTable());
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			if (field.getFilterType().isEnum()) {
				json = renderEnumFieldAsJson(field);
			} else if (field.getFilterType().isAutocomplete()) {
				json = reportFilterAutocompleter.getFilterAutocompleteResultsJSON(field.getAutocompleteType(),
						searchQuery, permissions);
			} else {
				throw new Exception(field.getFilterType() + " not supported by list function.");
			}

			json.put("success", true);
		} catch (Exception e) {
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	public String getUserStatus() {
		int userId = permissions.getUserId();

		json.put("is_editable", DynamicReportUtil.canUserEdit(userId, report));

		return JSON;
	}

	@Anonymous
	@Deprecated
	// TODO Remove this; it's not called from the front end
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (ModelType type : ModelType.values()) {
			rows.add(type.toString());
		}
		json.put("bases", rows);

		return JSON;
	}

	public String getReportParameters() throws Exception {
		reportController.validate(report);

		Definition definition = new Definition(report.getParameters());
		report.setDefinition(definition);
		// TODO remove definition from SqlBuilder
		sqlBuilder.setDefinition(definition);

		sql = sqlBuilder.buildSql(report, permissions, pageNumber);

		translateFilterValueNames(definition.getFilters());

		addTranslatedLabelsToReportParameters(report.getDefinition());

		json.put("report", report.toJSON(true));
		json.put("success", true);

		return JSON;
	}

	public String availableFields() {
		try {
			reportController.validate(report);
			Map<String, Field> availableFields = ReportController.buildAvailableFields(report.getTable());

			json.put("modelType", report.getModelType().toString());
			json.put("fields", translateAndJsonify(availableFields));
			json.put("success", true);
		} catch (Exception e) {
			writeJsonErrorMessage(e);
		}

		return JSON;
	}

	// This function is only called by availableFields()
	private JSONArray translateAndJsonify(Map<String, Field> availableFields) {
		JSONArray fieldsJsonArray = new JSONArray();

		for (Field field : availableFields.values()) {
			if (!canSeeQueryField(field))
				continue;

			field.setText(translateLabel(field));

			JSONObject obj = field.toJSONObject();
			obj.put("category", translateCategory(field.getCategory().toString()));

			String help = getText("Report." + field.getName() + ".help");
			if (help != null)
				obj.put("help", help);

			fieldsJsonArray.add(obj);
		}

		return fieldsJsonArray;
	}

	public String download() throws Exception {
		reportController.validate(report);

		Definition definition = new Definition(report.getParameters());
		report.setDefinition(definition);
		// TODO remove definition from SqlBuilder
		sqlBuilder.setDefinition(definition);

		sql = sqlBuilder.buildSql(report, permissions, pageNumber, FOR_DOWNLOAD);

		translateFilterValueNames(definition.getFilters());

		if (definition.getColumns().size() > 0) {
			List<BasicDynaBean> rawData = reportController.runQuery(sql, json);

			ExcelSheet excelSheet = new ExcelSheet();
			excelSheet.setData(rawData);

			excelSheet = sqlBuilder.extractColumnsToExcel(excelSheet);

			String filename = report.getName();
			excelSheet.setName(filename);

			HSSFWorkbook workbook = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			filename += fileType;

			// TODO: Change this to use an output stream handler - Alex to pair with Mike on this
			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			workbook.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
		}

		return SUCCESS;
	}

	private void addTranslatedLabelsToReportParameters(Definition definition) {
		addTranslationLabelsToFields(definition);
		addTranslationLabelsToFilters(definition);
		addTranslationLabelsToSorts(definition);
	}

	private void addTranslationLabelsToFields(Definition definition) {
		if (CollectionUtils.isEmpty(definition.getColumns()))
			return;

		for (Column column : definition.getColumns()) {
			column.getField().setText(translateLabel(column.getField()));
		}
	}

	private void addTranslationLabelsToFilters(Definition definition) {
		if (CollectionUtils.isEmpty(definition.getFilters()))
			return;

		for (Filter filter : definition.getFilters()) {
			filter.getField().setText(translateLabel(filter.getField()));
		}
	}

	private void addTranslationLabelsToSorts(Definition definition) {
		if (CollectionUtils.isEmpty(definition.getSorts()))
			return;

		for (Sort sort : definition.getSorts()) {
//			sort.setFieldName(translateLabel(sort.getField()));
			sort.getField().setText(translateLabel(sort.getField()));
		}
	}

	public String translateLabel(Field field) {
		String translatedText = getText("Report." + field.getName());
		if (translatedText == null) {
			translatedText = "?" + field.getName();
		}

		return translatedText;
	}

	private String translateCategory(String category) {
		String translatedText = getText("Report.Category." + category);

		if (translatedText == null)
			translatedText = getText("Report.Category.General");

		if (translatedText == null)
			translatedText = "?Report.Category.General";

		return translatedText;
	}

	private void writeJsonErrorMessage(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	/**
	 * The purpose of this method is to convert the queryResult into a JSONObject,
	 *
	 * @param queryResults
	 * @param availableFields
	 */
	private void convertToJson(List<BasicDynaBean> queryResults, Map<String, Field> availableFields) {
		JSONArray jsonRows = new JSONArray();

		for (BasicDynaBean row : queryResults) {
			JSONObject jsonRow = new JSONObject();

			for (DynaProperty property : row.getDynaClass().getDynaProperties()) {
				String column = property.getName();
				Object value = row.get(column);

				if (value == null)
					continue;

				Field field = availableFields.get(column.toUpperCase());

				if (field == null) {
					// TODO we get nulls if the column name is custom such
					// as contractorNameCount. Convert this to contractorName
					jsonRow.put(column, value);

				} else if (canSeeQueryField(field)) {

					if (field.isTranslated()) {
						String key = field.getI18nKey(value.toString());
						jsonRow.put(column, getText(key));

					} else if (value instanceof java.sql.Date) {
						java.sql.Date valueAsDate = (java.sql.Date) value;
						jsonRow.put(column, valueAsDate.getTime());

					} else if (value instanceof java.sql.Timestamp) {
						Timestamp valueAsTimestamp = (Timestamp) value;
						jsonRow.put(column, valueAsTimestamp.getTime());

					} else {
						jsonRow.put(column, value.toString());
					}
				}
			}
			jsonRows.add(jsonRow);
		}

		json.put("data", jsonRows);
	}

	private boolean canSeeQueryField(Field field) {
		if (field.getRequiredPermissions().isEmpty())
			return true;

		for (OpPerms requiredPermission : field.getRequiredPermissions()) {
			if (permissions.hasPermission(requiredPermission))
				return true;
		}

		return false;
	}

	// TODO: Refactor, because it seems just like the jsonException method.
	private void logError(Exception e) {
		json.put("success", false);
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		json.put("message", message);
		showSQL = true;
	}

	// TODO: Should the Field object return this?
	private JSONObject renderEnumFieldAsJson(Field field) {
		JSONObject enumResults = new JSONObject();
		JSONArray jsonResult = new JSONArray();
		for (Object enumValue : field.getFieldClass().getEnumConstants()) {
			JSONObject valueJson = new JSONObject();
			valueJson.put("id", enumValue.toString());

			String translationKey = field.getFieldClass().getSimpleName().toString() + "." + enumValue.toString();
			String translatedString = getText(translationKey);
			if (translatedString == null)
				translatedString = enumValue.toString();

			valueJson.put("name", translatedString);
			jsonResult.add(valueJson);
		}

		enumResults.put("result", jsonResult);
		return enumResults;
	}

	// TODO: Find out how this is being used (purpose in the big picture, possibly used for reverse translations)
	private void translateFilterValueNames(List<Filter> filters) {
		if (CollectionUtils.isEmpty(filters))
			return;

		for (Filter filter : filters) {
			if (!filter.isHasTranslations())
				continue;

			String filterValue = Strings.escapeQuotes(filter.getValue());
			if (Strings.isEmpty(filterValue))
				return;

			String[] values = filterValue.split(",");
			String[] translationValueNameArray = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				String translationKey = values[i];
				translationKey = buildTranslationKey(filter.getField(), translationKey);
				translationValueNameArray[i] = getText(translationKey);
			}

			String translatedValueNames = StringUtils.join(translationValueNameArray, ",");
			filter.setValueNames(translatedValueNames);
		}
	}

	private String buildTranslationKey(Field field, String initialTranslationKey) {
		String keyPrefix = field.getPreTranslation();
		String keySuffix = field.getPostTranslation();

		if(!Strings.isEmpty(keyPrefix)) {
			initialTranslationKey = keyPrefix + "." + initialTranslationKey;
		}

		if(!Strings.isEmpty(keySuffix)) {
			initialTranslationKey = initialTranslationKey + "." + keySuffix;
		}

		return initialTranslationKey;
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

	public void setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
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
