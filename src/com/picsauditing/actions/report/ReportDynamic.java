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
import com.picsauditing.report.fields.FieldType;
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

	public JSONArray getAvailableFields() {
		JSONArray fields = new JSONArray();

		for (QueryField field : builder.getAvailableFields().values()) {
			JSONObject obj = new JSONObject();
			obj.put("name", field.getDataIndex());
			if (field.getType() != FieldType.Auto) {
				obj.put("type", field.getType().toString().toLowerCase());
			}
			fields.add(obj);
		}
		return fields;
	}

	public JSONArray getStoreFields() {
		JSONArray fields = new JSONArray();

		for (QueryField field : builder.getIncludedFields()) {
			JSONObject obj = new JSONObject();
			obj.put("name", field.getDataIndex());
			if (field.getType() != FieldType.Auto) {
				obj.put("type", field.getType().toString().toLowerCase());
				if (field.getType() == FieldType.Date)
					obj.put("dateFormat", "time");
			}
			fields.add(obj);
		}
		return fields;
	}

	public JSONArray getGridColumns() {
		JSONArray fields = new JSONArray();

		JSONObject rowNum = new JSONObject();
		rowNum.put("xtype", "rownumberer");
		rowNum.put("width", 27);
		fields.add(rowNum);

		for (QueryField field : builder.getIncludedFields()) {
			String label = getText("Report." + field.getDataIndex());
			if (label != null)
				field.setLabel(label);
			else {
				field.setLabel("?" + field.getDataIndex());
				// if (field.getDataIndex() != null && column.getFunction() != null) {
				// field.setLabel(field.getLabel() + "." + column.getFunction().toString());
				// }
			}
			fields.add(field);
		}
		return fields;
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
