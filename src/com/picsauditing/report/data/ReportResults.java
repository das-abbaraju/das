package com.picsauditing.report.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.Column;

public class ReportResults {
	private List<ReportRow> rows = new ArrayList<ReportRow>();

	public ReportResults() {
	}

	public ReportResults(Collection<Column> columns, List<BasicDynaBean> results) {
		for (BasicDynaBean dynaBean : results) {
			ReportRow row = new ReportRow(columns, dynaBean);
			rows.add(row);
		}
	}

	public void addRow(ReportRow row) {
		rows.add(row);
	}

	public List<ReportRow> getRows() {
		return rows;
	}
	
	public int size() {
		if (CollectionUtils.isEmpty(rows)) {
			return 0;
		}
		
		return rows.size();
	}

	public Collection<ReportCell> getCells() {
		Collection<ReportCell> cells = new ArrayList<ReportCell>();
		for (ReportRow row : rows) {
			cells.addAll(row.getCells());
		}
		
		return cells;
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
