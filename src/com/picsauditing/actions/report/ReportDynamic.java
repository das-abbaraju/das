package com.picsauditing.actions.report;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.QueryBase;
import com.picsauditing.report.QueryCommand;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.QueryField;
import com.picsauditing.report.QueryRunner;
import com.picsauditing.search.Database;

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

	public String data() throws Exception {
		if (!isReportAndBaseThere())
			return BLANK;
		
		QueryRunner runner = new QueryRunner(report.getBase(), permissions);

		Database db = new Database();
		runner.buildQuery(createCommandFromReportParameters());
		QueryData data = runner.run(db);
		JSONArray rows = new JSONArray();

		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				jsonRow.put(column, row.get(column));
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

		for (String alias : runner.getAvailableFields().keySet()) {
			JSONObject obj = new JSONObject();
			obj.put("name", alias);
			String label = getText("Report." + alias);
			if (label != null)
				obj.put("label", label);
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
			String label = getText("Report." + field.dataIndex);
			if (label != null)
				field.label = label;
			columns.add(field);
		}
		/*
		 	[
				{
					hideable : false,
					sortable : false,
					renderer : function(value, metaData, record) {
						return Ext.String
								.format(
										'<a href="ContractorEdit.action?id={0}">Edit</a>',
										record.data.accountID);
					}
				}, {
					text : 'Status',
					dataIndex : 'accountStatus'
				} ]

		 */
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
}
