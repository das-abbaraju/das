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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.autocomplete.ReportFilterAutocompleter;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.Sort;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.business.ReportController;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.Database;
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

	@Deprecated
	public String find() {
		try {
			reportController.validate(report);
			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
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
			// TODO add logging
			e.printStackTrace();
			jsonException(e);
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
			// TODO add logging
			e.printStackTrace();
			jsonException(e);
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

			Map<String, Field> availableFields = reportController.buildAvailableFields(report.getBaseModel().getPrimaryTable());

			if (definition.getColumns().size() > 0) {
				QueryData data = queryData();
				convertToJson(data, availableFields);
				json.put("success", true);
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

//			sqlBuilder.initializeSql();
//			Field field = sqlBuilder.getAvailableFields().get(fieldName.toUpperCase());

			Map<String, Field> availableFields = reportController.buildAvailableFields(report.getBaseModel().getPrimaryTable());
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
			jsonException(e);
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
	// TODO Not called from the front end
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

	public String download() throws Exception {
		reportController.validate(report);

		Definition definition = new Definition(report.getParameters());
		report.setDefinition(definition);
		// TODO remove definition from SqlBuilder
		sqlBuilder.setDefinition(definition);

		sql = sqlBuilder.buildSql(report, permissions, pageNumber, FOR_DOWNLOAD);

		translateFilterValueNames(definition.getFilters());

		if (definition.getColumns().size() > 0) {
			List<BasicDynaBean> rawData = runSQL();

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

	/**
	 * Return a set of fields which can be used client side for defining the
	 * report (columns, sorting, grouping and filtering)
	 */
	@Deprecated
	// TODO possibly move to new ReportController.java class (?)
	public JSONArray translateAndJsonify(Map<String, Field> availableFields) {
		JSONArray fieldsJsonArray = new JSONArray();

//		for (Field field : sqlBuilder.getAvailableFields().values()) {
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

	private void addTranslatedLabelsToReportParameters(Definition definition) {
		if (definition.getColumns().size() > 0) {
			for (Column column : definition.getColumns()) {
				column.getField().setText(translateLabel(column.getField()));
			}
		}
		if (definition.getFilters().size() > 0) {
			for (Filter filter : definition.getFilters()) {
				filter.getField().setText(translateLabel(filter.getField()));
			}
		}
		if (definition.getSorts().size() > 0) {
			for (Sort sort : definition.getSorts()) {
				sort.getField().setText(translateLabel(sort.getField()));
			}
		}
	}

	@Deprecated
	private void saveTranslation(Map<String, AppTranslation> existing, String key) {
		AppTranslation translation = existing.get(key);

		if (translation == null) {
			translation = new AppTranslation();
			translation.setKey(key);
			translation.setLocale("en");
			translation.setQualityRating(TranslationQualityRating.Bad);
			translation.setValue("?" + key);
			translation.setAuditColumns(permissions);
			System.out.println("Adding " + key);
			existing.put(key, translation);
		} else {
			Calendar yesterday = Calendar.getInstance();
			yesterday.add(Calendar.DAY_OF_YEAR, -1);
			if (translation.getLastUsed().after(yesterday.getTime())) {
				System.out.println("Already updated " + key);
				return;
			}
			System.out.println("Updating " + key);
		}

		translation.setLastUsed(new Date());
		translation.setApplicable(true);
		translation.setContentDriven(true);
		dao.save(translation);
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

	// TODO: Change the name of this
	private void jsonException(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	// TODO refactor this mess
	private void convertToJson(QueryData data, Map<String, Field> availableFields) {
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				Object value = row.get(column);
				if (value == null)
					continue;

				Field field = availableFields.get(column.toUpperCase());
//				Field field = sqlBuilder.getAvailableFields().get(column.toUpperCase());

				if (field == null) {
					// TODO we get nulls if the column name is custom such
					// as contractorNameCount. Convert this to
					// contractorName
					jsonRow.put(column, value);
				} else if (canSeeQueryField(field)) {
					if (field.isTranslated()) {
						jsonRow.put(column, getText(field.getI18nKey(column)));
					} else if (value.getClass().equals(java.sql.Date.class)) {
						java.sql.Date value2 = (java.sql.Date) value;
						jsonRow.put(column, value2.getTime());
					} else if (value.getClass().equals(java.sql.Timestamp.class)) {
						Timestamp value2 = (Timestamp) value;
						jsonRow.put(column, value2.getTime());
					} else {
						jsonRow.put(column, value);
					}
				}
			}
			rows.add(jsonRow);
		}

		json.put("data", rows);
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

//	private void ensureValidReport() throws Exception {
//		if (report == null)
//			throw new RuntimeException("Please provide a saved or ad hoc report to run");
//
//		if (report.getModelType() == null)
//			throw new RuntimeException("The report is missing its base");
//
//		new JSONParser().parse(report.getParameters());
//	}

	// TODO: Refactor, because it seems just like the jsonException method. WTF?
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
			valueJson.put("name", getText(translationKey));
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

			if (Strings.isEmpty(filter.getValue()))
				return;

			String[] values = filter.getValue().split(",");
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

		if(!Strings.isEmpty(keySuffix)){
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

	// SQL stuff at bottom of file

	// This is in the wrong class, should be in SqlBuilder
//	private void buildSQL(boolean download) throws Exception {
//		reportController.validate(report);
//
//		Definition definition = new Definition(report.getParameters());
//		report.setDefinition(definition);
//		builder.setDefinition(definition);
//
//		builder.setBaseModelFromReport(report);
//		sql = builder.initializeSql();
//		builder.addPermissions(permissions);
//
//		// TODO: rowsPerPage can be added later
//		if (!download)
//			builder.setPaging(page, report.getRowsPerPage());
//
//		List<Filter> filters = builder.getDefinition().getFilters();
//		if (filters != null && !filters.isEmpty()) {
//			translateFilterValueNames(filters);
//		}
//	}

	// TODO: Rewrite this to PROPERLY log the timing (without System.out)
	private QueryData queryData() throws SQLException {
		long queryTime = Calendar.getInstance().getTimeInMillis();
		List<BasicDynaBean> rawData = runSQL();
		QueryData queryData = new QueryData(rawData);

		queryTime = Calendar.getInstance().getTimeInMillis() - queryTime;
		if (queryTime > 1000) {
			showSQL = true;
			logger.info("Slow Query: {}", sql.toString());
			logger.info("Time to query: {} ms", queryTime);
		}

		return queryData;
	}

	private List<BasicDynaBean> runSQL() throws SQLException {
		Database db = new Database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		return rows;
	}

	// TODO: Remove this once we figure out what to do with this and why it is doing the same
	// this as the i18n cache
	@Deprecated
	public String fillTranslations() {
		List<AppTranslation> existingList = dao
				.findWhere(AppTranslation.class, "locale = 'en' AND key LIKE 'Report.%'");

		Map<String, AppTranslation> existing = new HashMap<String, AppTranslation>();

		for (AppTranslation translation : existingList) {
			existing.put(translation.getKey(), translation);
		}

		for (FieldCategory category : FieldCategory.values()) {
			saveTranslation(existing, "Report.Category." + category);
		}

		for (ModelType type : ModelType.values()) {
			System.out.println("-- filling fields for " + type); // TODO: Remove this in favor of logging
			Report fakeReport = new Report();
			fakeReport.setModelType(type);

//			sqlBuilder = new SqlBuilder();
//			sqlBuilder.initializeSql(fakeReport.getBaseModel());
			Map<String, Field> availableFields = reportController.buildAvailableFields(fakeReport.getBaseModel().getPrimaryTable());
			for (Field field : availableFields.values()) {
				String key = "Report." + field.getName();
				saveTranslation(existing, key);
				saveTranslation(existing, key + ".help");
			}
		}

		return BLANK;
	}

	@Deprecated
	public String availableFields() {
		try {
			reportController.validate(report);
//			sqlBuilder.initializeSql(report.getBaseModel());
			Map<String, Field> availableFields = reportController.buildAvailableFields(report.getBaseModel().getPrimaryTable());

			json.put("modelType", report.getModelType().toString());
			json.put("fields", translateAndJsonify(availableFields));
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}

		return JSON;
	}
}
