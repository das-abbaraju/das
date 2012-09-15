package com.picsauditing.report.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;

import com.picsauditing.report.Column;

public class ReportResults {
	private List<ReportRow> rows = new ArrayList<ReportRow>();

	public ReportResults(Map<String, Column> columnMap, List<BasicDynaBean> results) {
		for (BasicDynaBean dynaBean : results) {
			ReportRow row = new ReportRow(columnMap, dynaBean);
			rows.add(row);
		}
	}

	public List<ReportRow> getRows() {
		return rows;
	}

	@SuppressWarnings("unchecked")
	public JSONArray toJson() {
		JSONArray jsonRows = new JSONArray();
		for (ReportRow row : rows) {
			jsonRows.add(row.toJson());
		}
		return jsonRows;
	}
}
