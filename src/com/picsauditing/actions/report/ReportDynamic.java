package com.picsauditing.actions.report;

import java.sql.Timestamp;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.FieldType;
import com.picsauditing.report.QueryBase;
import com.picsauditing.report.QueryCommand;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.QueryField;
import com.picsauditing.report.QueryRunner;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private Report report;
	private boolean showSQL;
	private int id;

	@Override
	public String execute() {
		if (!isReportAndBaseThere())
			return BLANK;

		return SUCCESS;
	}

	public String data() throws Exception {
		findReport();
		if (!isReportAndBaseThere())
			return BLANK;

		QueryRunner runner = new QueryRunner(report.getBase(), permissions);
		runner.buildQuery(createCommandFromReportParameters());
		Map<String, QueryField> availableFields = runner.getAvailableFields();

		QueryData data = runner.run();
		JSONArray rows = new JSONArray();
		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				Object value = row.get(column);
				if (value == null) {

				} else {
					QueryField field = availableFields.get(column);
					if (field.isTranslated()) {
						jsonRow.put(column, getText(field.getI18nKey(value.toString())));
					} else if (value.getClass().equals(java.sql.Timestamp.class)) {
						Timestamp value2 = (Timestamp) value;
						jsonRow.put(column, value2.getTime());
					} else
						jsonRow.put(column, value);
				}
			}
			rows.add(jsonRow);
		}

		json.put("total", runner.getAllRows());
		json.put("data", rows);
		if (showSQL && (permissions.isPicsEmployee() || permissions.getAdminID() > 0))
			json.put("sql", runner.getSQL().replaceAll("\n", " "));
		return JSON;
	}

	private QueryCommand createCommandFromReportParameters() {
		QueryCommand command = new QueryCommand();
		if (report.getParameters() != null) {
			JSONObject obj = (JSONObject) JSONValue.parse(report.getParameters());
			command.fromJSON(obj);
		}
		return command;
	}

	public String save() {
		if (!isReportAndBaseThere())
			return BLANK;
		report.setAuditColumns(permissions);
		dao.save(report);
		return SUCCESS;
	}

	public void findReport() {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		report = dao.find(Report.class, id);
		if (report == null)
		{
			report = new Report();
		}
	}

	public String delete() {
		findReport();
		dao.remove(report);
		return SUCCESS;
	}

	/**
	 * ReportDyn!availableFields.action?base=Contractors return [{name: "accountID", label: "AccountID", visible: true,
	 * dataType: Integer},{...}]
	 * 
	 * @return
	 */
	public String availableFields() {
		if (!isReportAndBaseThere())
			return BLANK;

		json.put("base", report.getBase().toString());
		json.put("fields", getAvailableFields());

		return JSON;
	}

	public JSONArray getAvailableFields() {
		QueryRunner runner = new QueryRunner(report.getBase(), permissions);

		JSONArray fields = new JSONArray();

		for (QueryField field : runner.getAvailableFields().values()) {
			JSONObject obj = new JSONObject();
			obj.put("name", field.dataIndex);
			if (field.type != FieldType.Auto) {
				obj.put("type", field.type.toString().toLowerCase());
				if (field.type == FieldType.Date)
					obj.put("dateFormat", "time");
			}
			fields.add(obj);
		}
		return fields;
	}

	public JSONArray getGridColumns() {
		JSONArray columns = new JSONArray();

		JSONObject rowNum = new JSONObject();
		rowNum.put("xtype", "rownumberer");
		rowNum.put("width", 27);
		columns.add(rowNum);

		QueryRunner runner = new QueryRunner(report.getBase(), permissions);
		runner.buildQuery(createCommandFromReportParameters());

		for (QueryField field : runner.getAvailableFields().values()) {
			String label = getText("Report.GlobalColumn." + field.dataIndex);
			if (label != null)
				field.label = label;
			columns.add(field);
		}
		return columns;
	}

	private boolean isReportAndBaseThere() {
		if (report == null) {
			addActionError("Please provide a saved or ad hoc report to run");
			return false;
		}
		if (report.getBase() == null) {
			addActionError("The report is missing its base");
			return false;
		}
		return true;
	}

	@Anonymous
	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (QueryBase base : QueryBase.values()) {
			rows.add(base.toString());
		}
		json.put("bases", rows);
		return JSON;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
