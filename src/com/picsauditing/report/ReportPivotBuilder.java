package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.PivotDimension;

public class ReportPivotBuilder {
	private List<Column> columnsIn = new ArrayList<Column>();
	private List<Column> columnsOut = new ArrayList<Column>();
	private Set<String> columnValueSet = new HashSet<String>();
	private Map<Object, Map<Object, List<Object>>> dataTree = new TreeMap<Object, Map<Object, List<Object>>>();

	public ReportPivotBuilder(List<Column> columns) {
		this.columnsIn.addAll(columns);
	}

	@SuppressWarnings("unchecked")
	public JSONArray convertToPivot(JSONArray jsonResults) {
		ReportPivotDefinition definition = new ReportPivotDefinition(columnsIn);
		if (!definition.isPivotable()) {
			// Don't make any changes, just exit
			columnsOut.addAll(columnsIn);
			return jsonResults;
		}

		columnsOut.add(definition.getRow());

		for (Object jsonRowObject : jsonResults) {
			JSONObject jsonRow = (JSONObject) jsonRowObject;
			Object rowValue = jsonRow.get(definition.getRow().getFieldName());
			Object columnValue = jsonRow.get(definition.getColumn().getFieldName());
			Object cellValue = jsonRow.get(definition.getCell().getFieldName());
			getColumn(rowValue, columnValue).add(cellValue);
		}

		JSONArray pivotedResults = new JSONArray();

		Field pivotField = definition.getCell().getField();
		
		for (Object rowValue : this.dataTree.keySet()) {
			System.out.println("rowValue = " + rowValue);
			JSONObject rowObject = new JSONObject();
			rowObject.put(definition.getRow().getFieldName(), rowValue);
			pivotedResults.add(rowObject);
			for (Object colValue : this.dataTree.get(rowValue).keySet()) {
				System.out.println("colValue = " + colValue);
				List<Object> columnValues = getRow(rowValue).get(colValue);
				Object valueObject = null;
				
				switch (definition.getCell().getPivotCellMethod()) {
				case Average:
					valueObject = sum(columnValues, pivotField.getType()) / count(columnValues);
					break;
				case Min:
					valueObject = min(columnValues, pivotField.getType());
					break;
				case Max:
					valueObject = max(columnValues, pivotField.getType());
					break;
				case Sum:
					valueObject = sum(columnValues, pivotField.getType());
					break;
				default:
					valueObject = count(columnValues);
					break;
				}
				rowObject.put(colValue, valueObject);
			}
		}

		for (String columnValue : columnValueSet) {
			Column columnForValue = new Column(columnValue);
			columnForValue.setField(pivotField);
			columnsOut.add(columnForValue);
		}
		System.out.println(this.dataTree);

		return pivotedResults;
	}

	public List<Column> getColumns() {
		return columnsOut;
	}

	private Map<Object, List<Object>> getRow(Object row) {
		if (dataTree.containsKey(row))
			return dataTree.get(row);

		Map<Object, List<Object>> newRow = new TreeMap<Object, List<Object>>();
		dataTree.put(row, newRow);
		return newRow;
	}

	private List<Object> getColumn(Object row, Object column) {
		Map<Object, List<Object>> rowTree = getRow(row);
		if (rowTree.containsKey(column))
			return rowTree.get(column);

		List<Object> newColumn = new ArrayList<Object>();
		rowTree.put(column, newColumn);
		columnValueSet.add(column.toString());
		return newColumn;
	}

	private Object max(List<Object> columnValues, ExtFieldType type) {
		Object max = null;
		for (Object object : columnValues) {
			try {
				if (max == null) {
					max = object;
				}
			} catch (Exception e) {

			}
		}
		return max;
	}

	private Object min(List<Object> columnValues, ExtFieldType type) {
		Object min = null;
		for (Object object : columnValues) {
			try {
				if (min == null) {
					min = object;
				}
			} catch (Exception e) {

			}
		}
		return min;
	}

	private int count(List<Object> columnValues) {
		return columnValues.size();
	}

	private double sum(List<Object> columnValues, ExtFieldType extFieldType) {
		
		double sum = 0;
		for (Object object : columnValues) {
			try {
				sum += Double.parseDouble(object.toString());
			} catch (Exception e) {

			}
		}
		return sum;
	}
}
