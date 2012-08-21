package com.picsauditing.actions.qa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.picsauditing.util.Strings;

public class TabularData implements TabularModel {
	private List<String> columnNames = new ArrayList<String>();
	private SortedMap<Integer, SortedMap<Integer, Object>> data = new TreeMap<Integer, SortedMap<Integer, Object>>();
	private Map<Integer, String> columnEntityNames = new HashMap<Integer, String>();

	public TabularData() {
	}

	public TabularData(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public SortedMap<Integer, SortedMap<Integer, Object>> getData() {
		return data;
	}

	@Override
	public Collection<SortedMap<Integer, Object>> getRows() {
		return data.values();
	}

	public void setColumnEntityName(int columnNumber, String entityName) {
		columnEntityNames.put(columnNumber, entityName);
	}

	public String getColumnEntityName(int columnNumber) {
		return columnEntityNames.get(columnNumber);
	}

	public String getColumnEntityName() {
		return "Contractor";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(1, columnIndex).getClass();
	}

	@Override
	public int getColumnCount() {
		return (columnNames == null) ? 0 : columnNames.size();
	}

	@Override
	public List<String> getColumnNames() {
		return columnNames;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnNames != null && columnNames.size() >= columnIndex) {
			return columnNames.get(columnIndex);
		}
		// perhaps better to throw NoSuchColumnException
		return Strings.EMPTY_STRING;
	}

	@Override
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) throws IndexOutOfBoundsException {
		Map<Integer, Object> list = data.get(row);
		if (list == null) {
			return null;
		}
		return list.get(col);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		SortedMap<Integer, Object> list = data.get(row);
		if (list == null) {
			list = new TreeMap<Integer, Object>();
			data.put(row, list);
		}
		list.put(col, value);
	}

}
