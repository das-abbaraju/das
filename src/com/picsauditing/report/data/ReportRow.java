package com.picsauditing.report.data;

import java.util.*;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

public class ReportRow {
	private Map<Column, ReportCell> cells = new HashMap<Column, ReportCell>();

	public ReportRow(Map<Column, Object> rowData) {
		for (Column column : rowData.keySet()) {
			Object value = rowData.get(column);
			cells.put(column, new ReportCell(column, value));
		}
	}

	public Collection<ReportCell> getCells() {
		return cells.values();
	}

    public ReportCell getCellByColumn(Column column) {
        return cells.get(column);
    }

	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject jsonRow = new JSONObject();

		for (ReportCell cell : cells.values()) {
			Object value = cell.getValue();
			jsonRow.put(cell.getColumn().getName(), value);
		}

		return jsonRow;
	}
}
