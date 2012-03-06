package com.picsauditing.actions.report;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.SimpleReportDefinition;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.SimpleReportColumn;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private Report report;
	private int page = 1;

	private boolean showSQL;
	private SelectSQL sql = new SelectSQL();
	private SqlBuilder builder = new SqlBuilder();

	@Anonymous
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (ModelType base : ModelType.values()) {
			rows.add(base.toString());
		}
		json.put("bases", rows);
		return JSON;
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
		System.out.println(params);
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

			QueryData data = queryData();
			convertToJson(data);
			json.put("success", true);
		} catch (SQLException e) {
			logError(e);
		} catch (Exception e) {
			logError(e);
		} finally {
			if (showSQL && (permissions.isPicsEmployee() || permissions.getAdminID() > 0)) {
				json.put("sql", sql.toString().replace("`", "").replace("\n", " "));
				json.put("base", report.getModelType().toString());
				// json.put("command", new JsonRaw(report.getParameters()));
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
					QueryField field = builder.getAvailableFields().get(column.toUpperCase());
					if (isCanSeeQueryField(field)) {
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
				JSONObject obj = new JSONObject();
				obj.put("name", field.getDataIndex());
				obj.put("text", translateLabel(field));
				addFilterType(field, obj);
				addHelp(field, obj);
				obj.put("category", translateCategory(field.getCategory().toString()));
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

	/**
	 * Returns a list of Store fields with just name and type
	 * 
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Store
	 *      http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
	 */
	public JSONArray getStoreFields() {
		JSONArray fields = new JSONArray();
		for (SimpleReportColumn column : builder.getIncludedColumns()) {
			JSONObject obj = new JSONObject();
			addName(column, obj);
			addFilterType(column, obj);
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

		fields.add(createRowNumColumn());
		for (SimpleReportColumn column : builder.getIncludedColumns()) {
			QueryField field = getQueryFieldFromSimpleColumn(column);
			if (isCanSeeQueryField(field)) {
				field.setLabel(translateLabel(column));
				fields.add(field);
			}
		}
		return fields;
	}

	private void addName(SimpleReportColumn column, JSONObject obj) {
		obj.put("name", column.getName());
	}

	private void addFilterType(SimpleReportColumn column, JSONObject obj) {
		QueryField field = getQueryFieldFromSimpleColumn(column);
		addFilterType(field, obj);
	}

	private void addFilterType(QueryField field, JSONObject obj) {
		obj.put("filterType", field.getFilterType().toString());

		if (field.getType() == ExtFieldType.Auto)
			return;

		obj.put("type", field.getType().toString().toLowerCase());
		if (field.getType() == ExtFieldType.Date)
			obj.put("dateFormat", "time");
	}

	private QueryField getQueryFieldFromSimpleColumn(SimpleReportColumn column) {
		return builder.getAvailableFields().get(column.getAvailableFieldName().toUpperCase());
	}

	private void addHelp(QueryField field, JSONObject obj) {
		String translatedText = getText("Report." + field.getDataIndex() + ".help");
		if (translatedText != null)
			obj.put("help", translatedText);
	}

	private String translateLabel(SimpleReportColumn column) {
		QueryField field = getQueryFieldFromSimpleColumn(column);
		String translatedText = translateLabel(field);
		if (column.getFunction() != null) {
			// TODO I'm not completely happy about how we're naming columns with
			// Functions
			// We may want to support the user entering the label manually
			// Until we work with this more, I'm just going to append the name
			// of the Function
			translatedText += " " + column.getFunction().toString();
		}
		return translatedText;
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

	private JSONObject createRowNumColumn() {
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
