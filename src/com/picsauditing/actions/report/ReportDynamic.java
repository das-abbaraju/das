package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.autocomplete.ReportFilterAutocompleter;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.Definition;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings( { "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	@Autowired
	private ReportFilterAutocompleter reportFilterAutocompleter;

	private static final String CREATE = "create";
	private static final String EDIT = "edit";
	private static final String DELETE = "delete";

	private Report report;
	private int page = 1;
	private boolean showSQL;
	private SelectSQL sql = new SelectSQL();
	private SqlBuilder builder = new SqlBuilder();

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String find() {
		try {
			ensureValidReport();
			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}
		return JSON;
	}

	public String delete() throws Exception {
		if (isValidUser(DELETE)) {
			permissions.tryPermission(OpPerms.Report, OpType.Delete);
			ensureValidReport();
			dao.remove(report);
		} else {
			json.put("success", false);
			json.put("error", "Invalid User, cannot delete reports that are not your own.");
		}

		return JSON;
	}

	public String edit() {
		if (isValidUser(EDIT)) {
			save(report);
		} else {
			json.put("success", false);
			json.put("error", "Invalid User, cannot edit reports that are not your own.");
		}

		return JSON;
	}

	public String create() {
		if (isValidUser(CREATE)) {
			Report newReport = new Report();
			newReport.setModelType(report.getModelType());
			newReport.setName(report.getName());
			newReport.setDescription(report.getDescription());
			newReport.setParameters(report.getParameters());
			newReport.setSharedWith(report.getSharedWith());

			save(newReport);
		} else {
			json.put("success", false);
			json.put("error", "Invalid User, does not have permission.");
		}

		return JSON;
	}

	public String getUserStatus() {
		json.put("is_developer", permissions.isDeveloperEnvironment());
		json.put("is_owner", isReportOwner());
		json.put("has_permission", permissions.hasPermission(OpPerms.Report, OpType.Edit));
		json.put("user_can_edit", isValidUser(EDIT));
		json.put("user_can_create", isValidUser(CREATE));
		json.put("user_can_delete", isValidUser(DELETE));

		return JSON;
	}

	private boolean isValidUser(String action) {
		if (report.getCreatedBy() == null || isReportOwner() || permissions.isDeveloperEnvironment()) {
			return true;
		} else if (action.equals(CREATE)) {
			if (isBaseReport() || permissions.hasPermission(OpPerms.Report, OpType.Edit))
				return true;
		}

		return false;
	}

	private boolean isBaseReport() {
		return report.getCreatedBy().getId() == User.SYSTEM;
	}

	private boolean isReportOwner() {
		return permissions.getUserId() == report.getCreatedBy().getId();
	}

	private String save(Report report) {
		try {
			ensureValidReport();

			report.setAuditColumns(permissions);
			dao.save(report);
			json.put("success", true);
			json.put("reportID", report.getId());
		} catch (Exception e) {
			jsonException(e);
		}

		return JSON;
	}

	public String data() {
		try {
			buildSQL();

			if (builder.getDefinition().getColumns().size() > 0) {
				QueryData data = queryData();
				convertToJson(data);
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

	// This is in the wrong class, should be in SqlBuilder
	private void buildSQL() throws Exception {
		ensureValidReport();

		addDefinition();

		builder.setReport(report);
		sql = builder.getSql();
		builder.addPermissions(permissions);
		builder.addPaging(page);
	}

	private QueryData queryData() throws SQLException {
		Database db = new Database();
		long queryTime = Calendar.getInstance().getTimeInMillis();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		queryTime = Calendar.getInstance().getTimeInMillis() - queryTime;
		if (queryTime > 1000) {
			showSQL = true;
			System.out.println("Slow Query: " + sql.toString());
			System.out.println("Time to query: " + queryTime + " ms");
		}

		return new QueryData(rows);
	}

	public String availableFields() {
		try {
			ensureValidReport();
			builder.setReport(report);
			builder.getSql();

			json.put("modelType", report.getModelType().toString());
			json.put("fields", getAvailableFields());
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}

		return JSON;
	}

	@Anonymous
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (ModelType type : ModelType.values()) {
			rows.add(type.toString());
		}
		json.put("bases", rows);
		return JSON;
	}

	/**
	 * Return a set of fields which can be used client side for defining the
	 * report (columns, sorting, grouping and filtering)
	 */
	public JSONArray getAvailableFields() {
		JSONArray fields = new JSONArray();

		for (Field field : builder.getAvailableFields().values()) {
			if (isCanSeeQueryField(field)) {
				JSONObject obj = field.toJSONObject();
				obj.put("category", translateCategory(field.getCategory().toString()));
				obj.put("text", translateLabel(field));
				String help = getText("Report." + field.getName() + ".help");
				if (help != null)
					obj.put("help", help);
				fields.add(obj);
			}
		}
		return fields;
	}

	public List<? extends BaseTable> getAvailableReports() {
		return dao.findWhere(Report.class, "id > 0", 100);
	}

	public String getReportParameters() throws Exception {
		buildSQL();
		json.put("report", report.toJSON(true));
		json.put("success", true);
		return JSON;
	}

	@Anonymous
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
			System.out.println("-- filling fields for " + type);
			Report fakeReport = new Report();
			fakeReport.setModelType(type);
			builder = new SqlBuilder();
			builder.setReport(fakeReport);
			builder.getSql();
			for (Field field : builder.getAvailableFields().values()) {
				String key = "Report." + field.getName();
				saveTranslation(existing, key);
				saveTranslation(existing, key + ".help");
			}
		}
		return BLANK;
	}

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

	private String translateLabel(Field field) {
		String translatedText = getText("Report." + field.getName());
		if (translatedText == null)
			translatedText = "?" + field.getName();
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

	private void jsonException(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	private void convertToJson(QueryData data) {
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				Object value = row.get(column);
				if (value == null) {

				} else {

					Field field = builder.getAvailableFields().get(column.toUpperCase());
					if (field == null) {
						// TODO we get nulls if the column name is custom such
						// as contractorNameCount. Convert this to
						// contractorName
						jsonRow.put(column, value);

					} else if (isCanSeeQueryField(field)) {
						if (field.isTranslated()) {
							jsonRow.put(column, getText(field.getI18nKey(value.toString())));
						} else if (value.getClass().equals(java.sql.Date.class)) {
							java.sql.Date value2 = (java.sql.Date) value;
							jsonRow.put(column, value2.getTime());
						} else if (value.getClass().equals(java.sql.Timestamp.class)) {
							Timestamp value2 = (Timestamp) value;
							jsonRow.put(column, value2.getTime());
						} else
							jsonRow.put(column, value);
					}
				}
			}
			rows.add(jsonRow);
		}

		json.put("data", rows);
	}

	private boolean isCanSeeQueryField(Field field) {
		if (field.getRequiredPermissions().isEmpty())
			return true;

		for (OpPerms requiredPermission : field.getRequiredPermissions()) {
			if (permissions.hasPermission(requiredPermission))
				return true;
		}

		return false;
	}

	private void ensureValidReport() throws Exception {
		if (report == null)
			throw new RuntimeException("Please provide a saved or ad hoc report to run");

		if (report.getModelType() == null)
			throw new RuntimeException("The report is missing its base");
	}

	private void addDefinition() {
		Definition definition = new Definition(report.getParameters());
		report.setDefinition(definition);
		builder.setDefinition(definition);
	}

	private void logError(Exception e) {
		json.put("success", false);
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		json.put("message", message);
		showSQL = true;
	}

	public String getList(String fieldName, String searchQuery) {
		try {
			ensureValidReport();
			Field field = builder.getAvailableFields().get(fieldName);
			validate(field);

			JSONObject autoCompleteResults = reportFilterAutocompleter.getFilterAutocompleteResultsJSON(field
					.getAutocompleteType(), searchQuery, permissions);
			json.put("autocompleteResults", autoCompleteResults);
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}
		return JSON;
	}

	private void validate(Field field) throws Exception {
		if (field == null)
			throw new Exception("Available field undefined");
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
	}
}
