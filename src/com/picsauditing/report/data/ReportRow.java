package com.picsauditing.report.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.springframework.util.CollectionUtils;

import com.picsauditing.report.Column;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

public class ReportRow {
	private Map<Column, ReportCell> cells = new HashMap<Column, ReportCell>();

	public ReportRow(Collection<Column> columns, BasicDynaBean dynaBean) {
		for (Column column : columns) {
			if (column.getField() != null) {
				try {
					Object value = dynaBean.get(column.getFieldName());
					cells.put(column, new ReportCell(this, column, value));
					// TODO do this more efficiently by getting the list of
					// dependent columns first
				} catch (IllegalArgumentException e) {
				}
				addDependentFields(column, dynaBean);
			} else {
				cells.put(column, new ReportCell(this, column, null));
			}
		}
	}

	private void addDependentFields(Column column, BasicDynaBean dynaBean) {
		Set<String> dependentFields = column.getField().getDependentFields();
		for (String fieldName : dependentFields) {
			if (!containsColumnWithFieldName(fieldName)) {
				try {
					Object value = dynaBean.get(fieldName);
					Column newColumn = new Column(fieldName);
					newColumn.setField(new Field(fieldName));

					cells.put(newColumn, new ReportCell(this, newColumn, value));
				} catch (IllegalArgumentException e) {
				}
			}
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

	private boolean containsColumnWithFieldName(String fieldName) {
		if (CollectionUtils.isEmpty(cells) || Strings.isEmpty(fieldName)) {
			return false;
		}

		for (Column column : cells.keySet()) {
			if (fieldName.equals(column.getFieldName())) {
				return true;
			}
		}

		return false;
	}

}
