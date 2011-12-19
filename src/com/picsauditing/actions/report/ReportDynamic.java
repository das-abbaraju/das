package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.FieldType;
import com.picsauditing.report.QueryBase;
import com.picsauditing.report.QueryCommand;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.QueryField;
import com.picsauditing.report.QueryRunner;
import com.picsauditing.report.SortableField;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private Report report;
	private boolean showSQL;

	@Override
	public String execute() {
		if (!isReportAndBaseThere())
			return BLANK;

		return SUCCESS;
	}

	public String data() {
		if (!isReportAndBaseThere())
			return BLANK;

		QueryRunner runner = new QueryRunner(report.getBase(), permissions, dao);
		runner.buildQuery(runner.createCommandFromReportParameters(report), false);
		Map<String, QueryField> availableFields = runner.getAvailableFields();

		try {
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

			json.put("success", true);
			json.put("total", runner.getAllRows());
			json.put("data", rows);
		} catch (SQLException e) {
			json.put("success", false);
			json.put("message", e.getMessage());
			showSQL = true;
			System.out.println("Error in Dynamic Report Query: " + runner.getSQL());
		}
		if (showSQL && (permissions.isPicsEmployee() || permissions.getAdminID() > 0))
			json.put("sql", runner.getSQL().replaceAll("\n", " "));
		return JSON;
	}

	public String save() {
		if (!isReportAndBaseThere())
			return BLANK;
		report.setAuditColumns(permissions);
		dao.save(report);
		return SUCCESS;
	}

	public String delete() {
		if (!isReportAndBaseThere())
			return BLANK;
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
		QueryRunner runner = new QueryRunner(report.getBase(), permissions, dao);

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

		QueryRunner runner = new QueryRunner(report.getBase(), permissions, dao);
		runner.buildQuery(runner.createCommandFromReportParameters(report), false);

		for (SortableField column : runner.getColumns()) {
			if (runner.getAvailableFields().keySet().contains(column.field)) {
				QueryField field = runner.getAvailableFields().get(column.field);
				String label = getText("Report." + field.dataIndex);
				if (label != null)
					field.label = label;
				else {
					field.label = "Report." + column.field;
					if (column.function != null) {
						field.label += "." + column.function.toString();
					}
				}
				columns.add(field);
			}
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

	public List<? extends BaseTable> getAvailableReports() {
		return dao.findWhere(Report.class, "id > 0", 100);
	}
}
