package com.picsauditing.report.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.picsauditing.report.Column;

public class ReportRow {
	private Map<Column, ReportCell> cells = new HashMap<Column, ReportCell>();

	public ReportRow(Collection<Column> columns, BasicDynaBean dynaBean) {
		for (Column column : columns) {
			Object value = dynaBean.get(column.getFieldName());
			cells.put(column, new ReportCell(this, column, value));
		}
	}
	
	public ReportRow(Map<Column, Object> rowData) {
		for (Column column : rowData.keySet()) {
			Object value = rowData.get(column);
			cells.put(column, new ReportCell(this, column, value));
		}
	}
	
	public Collection<ReportCell> getCells() {
		return cells.values();
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject jsonRow = new JSONObject();

		for (ReportCell cell : cells.values()) {
			Object value = cell.getValue();
			if (value != null) {
				jsonRow.put(cell.getColumn().getFieldName(), value);
			}
		}
		return jsonRow;
	}

	public ReportCell getCellByColumn(Column column) {
		return cells.get(column);
	}

}
