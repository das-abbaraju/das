package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.SimpleReportDefinition;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private Report report;
	private int page = 1;

	private boolean showSQL;
	private SelectSQL sql = new SelectSQL();
	private SqlBuilder builder = new SqlBuilder();

	private void checkReport() {
		if (report == null)
			throw new RuntimeException("Please provide a saved or ad hoc report to run");

		if (report.getModelType() == null)
			throw new RuntimeException("The report is missing its base");

		builder.setReport(report);
	}

	@Override
	public String execute() {
		checkReport();

		{
			SimpleReportDefinition definition = new SimpleReportDefinition(report.getParameters());
			if (!Strings.isEmpty(report.getDevParams())) {
				SimpleReportDefinition devDefinition = new SimpleReportDefinition(report.getDevParams());
				definition.merge(devDefinition);
			}
			builder.setDefinition(definition);

			sql = builder.getSql();
			builder.setPermissions(permissions);
		}

		return SUCCESS;
	}

	public String list() {
		// What's this for? I forgot...
		return "list";
	}

	public String save() {
		checkReport();
		report.setAuditColumns(permissions);
		dao.save(report);
		return SUCCESS;
	}

	public String delete() {
		checkReport();
		dao.remove(report);
		return SUCCESS;
	}

	@Anonymous
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (ModelType base : ModelType.values()) {
			rows.add(base.toString());
		}
		json.put("bases", rows);
		return JSON;
	}

	public String availableFields() {
		checkReport();
		builder.getSql();

		json.put("modelType", report.getModelType().toString());
		json.put("fields", getAvailableFields());

		return JSON;
	}

	public String data() {
		try {
			buildSQL();

			QueryData data = queryData();
			convertToJson(data);
			json.put("success", true);
		} catch (SQLException e) {
			logError(e);
		} catch (Exception e) {
			logError(e);
		} finally {
			if (showSQL && (permissions.isPicsEmployee() || permissions.getAdminID() > 0)) {
				json.put("sql", sql.toString());
				json.put("base", report.getModelType());
				json.put("command", report.getParameters());
			}
		}

		return JSON;
	}

	private void buildSQL() {
		checkReport();

		SimpleReportDefinition definition = new SimpleReportDefinition(report.getParameters());
		if (!Strings.isEmpty(report.getDevParams())) {
			SimpleReportDefinition devDefinition = new SimpleReportDefinition(report.getDevParams());
			definition.merge(devDefinition);
		}
		builder.setDefinition(definition);

		sql = builder.getSql();
		builder.setPermissions(permissions);

		if (page > 1)
			sql.setStartRow((page - 1) * definition.getRowsPerPage());
		sql.setLimit(definition.getRowsPerPage());
		sql.setSQL_CALC_FOUND_ROWS(true);
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

	private void convertToJson(QueryData data) {
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				Object value = row.get(column);
				if (value == null) {

				} else {
					QueryField field = builder.getAvailableFields().get(column);
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
			rows.add(jsonRow);
		}

		json.put("data", rows);
	}

	private void logError(Exception e) {
		json.put("success", false);
		json.put("message", e.getMessage());
		showSQL = true;
	}

	// Getters that need some calculation

	/**
	 * Return a set of fields which can be used client side for defining the report (columns, sorting, grouping and
	 * filtering)
	 */
	public JSONArray getAvailableFields() {
		JSONArray fields = new JSONArray();

		for (QueryField field : builder.getAvailableFields().values()) {
			JSONObject obj = new JSONObject();
			addName(field, obj);
			obj.put("text", translateLabel(field));
			addFilterType(field, obj);
			addHelp(field, obj);
			obj.put("category", translateCategory(field.getCategory()));
			fields.add(obj);
		}
		return fields;
	}

	/**
	 * Returns a list of Store fields with just name and type
	 * 
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Store
	 *      http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
	 */
	public JSONArray getStoreFields() {
		JSONArray fields = new JSONArray();

		for (QueryField field : builder.getIncludedFields()) {
			JSONObject obj = new JSONObject();
			addName(field, obj);
			addFilterType(field, obj);
			fields.add(obj);
		}
		return fields;
	}

	/**
	 * Returns a list of Columns for a Grid with text, dataIndex, etc
	 * 
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
	 */
	public JSONArray getGridColumns() {
		JSONArray fields = new JSONArray();

		fields.add(createRunNumColumn());
		for (QueryField field : builder.getIncludedFields()) {
			field.setLabel(translateLabel(field));
			// if (field.getDataIndex() != null && column.getFunction() != null) {
			// field.setLabel(field.getLabel() + "." + column.getFunction().toString());
			// }
			fields.add(field);
		}
		return fields;
	}

	private void addName(QueryField field, JSONObject obj) {
		obj.put("name", field.getDataIndex());
	}

	private void addFilterType(QueryField field, JSONObject obj) {
		if (field.getType() == ExtFieldType.Auto)
			return;

		obj.put("type", field.getType().toString().toLowerCase());
		if (field.getType() == ExtFieldType.Date)
			obj.put("dateFormat", "time");
	}

	private void addHelp(QueryField field, JSONObject obj) {
		String translatedText = getText("Report." + field.getDataIndex() + ".help");
		if (translatedText != null)
			obj.put("description", translatedText);
	}

	private String translateLabel(QueryField field) {
		String translatedText = getText("Report." + field.getDataIndex());
		if (translatedText == null)
			translatedText = "?" + field.getDataIndex();
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

	private JSONObject createRunNumColumn() {
		JSONObject rowNum = new JSONObject();
		rowNum.put("xtype", "rownumberer");
		rowNum.put("width", 27);
		return rowNum;
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
