package com.picsauditing.actions.report;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.SimpleReportDefinition;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private Report report;
	private int page = 1;

	private boolean showSQL;
	private SelectSQL sql = new SelectSQL();
	private SqlBuilder builder = new SqlBuilder();

	private final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);
	@Anonymous
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (ModelType type : ModelType.values()) {
			rows.add(type.toString());
		}
		json.put("bases", rows);
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
			logger.debug("-- filling fields for {}", type);
			Report fakeReport = new Report();
			fakeReport.setModelType(type);
			builder = new SqlBuilder();
			builder.setReport(fakeReport);
			builder.getSql();
			for (QueryField field : builder.getAvailableFields().values()) {
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
			logger.debug("Adding {}", key);
			existing.put(key, translation);
		} else {
			Calendar yesterday = Calendar.getInstance();
			yesterday.add(Calendar.DAY_OF_YEAR, -1);
			if (translation.getLastUsed().after(yesterday.getTime())) {
				logger.info("Already updated {}", key);
				return;
			}
			logger.info("Updating {}", key);
		}
		translation.setLastUsed(new Date());
		translation.setApplicable(true);
		translation.setContentDriven(true);
		dao.save(translation);
	}

	public String list() throws Exception {
		throw new Exception("oops, I thought this wasn't being used anymore");
	}

	public String find() {
		try {
			checkReport();
			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}
		return JSON;
	}

	public String save() {
		try {
			checkReport();

			parseInputStream();

			report.setAuditColumns(permissions);
			dao.save(report);
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}

		return JSON;
	}

	// We probably don't need this anymore
	// Leave it until we're sure how we'll accept the report parameters
	private void parseInputStream() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream(), writer, "UTF-8");
		String params = writer.toString();
		logger.info(params);
	}

	public String availableFields() {
		try {
			checkReport();
			builder.getSql();

			json.put("modelType", report.getModelType().toString());
			json.put("fields", getAvailableFields());
			json.put("success", true);
		} catch (Exception e) {
			jsonException(e);
		}

		return JSON;
	}

	public String delete() throws Exception {
		permissions.tryPermission(OpPerms.Report, OpType.Delete);
		checkReport();
		dao.remove(report);
		return SUCCESS;
	}

	private void checkReport() throws Exception {
		if (report == null)
			throw new RuntimeException("Please provide a saved or ad hoc report to run");

		if (report.getModelType() == null)
			throw new RuntimeException("The report is missing its base");

		builder.setReport(report);
	}

	private void jsonException(Exception e) {
		json.put("success", false);
		json.put("error", e.getCause() + " " + e.getMessage());
	}

	@Override
	public String execute() throws Exception {
		checkReport();
		addDefinition();

		sql = builder.getSql();
		builder.addPermissions(permissions);

		return SUCCESS;
	}

	public boolean isCanEdit() {
		// while we're testing
		if (permissions.isAdmin())
			return true;
		if (!permissions.hasPermission(OpPerms.Report, OpType.Edit))
			return false;
		if (report.getCreatedBy().getId() == permissions.getUserId())
			return true;
		return false;
	}

	private void addDefinition() {
		SimpleReportDefinition definition = new SimpleReportDefinition(report.getParameters());
		builder.setDefinition(definition);
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

	private void buildSQL() throws Exception {
		checkReport();

		addDefinition();

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
			logger.info("Slow Query: {}", sql.toString());
			logger.info("Time to query: {} ms", queryTime);
		}

		return new QueryData(rows);
	}

	private void convertToJson(QueryData data) {
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				Object value = row.get(column);
				if (value == null) {

				} else {

					QueryField field = builder.getAvailableFields().get(column.toUpperCase());
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

	private void logError(Exception e) {
		json.put("success", false);
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}
		json.put("message", message);
		showSQL = true;
	}

	// Getters that need some calculation

	/**
	 * Return a set of fields which can be used client side for defining the
	 * report (columns, sorting, grouping and filtering)
	 */
	public JSONArray getAvailableFields() {
		JSONArray fields = new JSONArray();

		for (QueryField field : builder.getAvailableFields().values()) {
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

	private boolean isCanSeeQueryField(QueryField field) {
		if (field.getRequiredPermissions().size() == 0)
			return true;

		for (OpPerms requiredPermission : field.getRequiredPermissions()) {
			if (permissions.hasPermission(requiredPermission))
				return true;
		}
		return false;
	}

	private String translateLabel(QueryField field) {
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

	// Getters and Setters

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<? extends BaseTable> getAvailableReports() {
		return dao.findWhere(Report.class, "id > 0", 100);
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
	}

}
