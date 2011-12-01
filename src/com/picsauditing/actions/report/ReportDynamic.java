package com.picsauditing.actions.report;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.report.QueryBase;
import com.picsauditing.report.QueryCommand;
import com.picsauditing.report.QueryData;
import com.picsauditing.report.QueryRunner;
import com.picsauditing.search.Database;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {
	private QueryBase base;
	private QueryCommand command = new QueryCommand();

	@Override
	public String execute() throws Exception {
		if (base == null) {
			addActionError("Missing Base");
			return SUCCESS;
		}
		QueryRunner runner = new QueryRunner(base, permissions);

		Database db = new Database();
		QueryData data = runner.run(command, db);
		JSONArray rows = new JSONArray();

		for (Map<String, Object> row : data.getData()) {
			JSONObject jsonRow = new JSONObject();
			for (String column : row.keySet()) {
				jsonRow.put(column, row.get(column));
			}
			rows.add(jsonRow);
		}

		json.put("totalRows", runner.getAllRows());
		json.put("data", rows);
		return JSON;
	}

	public String availableBases() {
		JSONArray rows = new JSONArray();
		for (QueryBase base : QueryBase.values()) {
			rows.add(base.toString());
		}
		json.put("bases", rows);
		return JSON;
	}

	/**
	 * ReportDyn!availableFields.action?base=Contractors return [{name: "accountID", label: "AccountID", visible: true,
	 * dataType: Integer},{...}]
	 * 
	 * @return
	 */
	public String availableFields() {
		if (base == null) {
			json.put("message", "Missing Base");
			return JSON;
		}
		json.put("base", base.toString());
		
		QueryRunner runner = new QueryRunner(base, permissions);
		JSONArray fields = new JSONArray();

		for (String alias : runner.getAvailableFields().keySet()) {
			JSONObject obj = new JSONObject();
			obj.put("name", alias);
			String label = getText("Report." + alias);
			if (label != null)
				obj.put("label", label);
			fields.add(obj);
		}

		json.put("fields", fields);
		return JSON;
	}

	public QueryBase getBase() {
		return base;
	}

	public void setBase(QueryBase base) {
		this.base = base;
	}

	public QueryCommand getCommand() {
		return command;
	}

	public void setCommand(QueryCommand command) {
		this.command = command;
	}

}
