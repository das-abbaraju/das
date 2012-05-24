package com.picsauditing.actions.qa;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

public interface TabularModel {
	SortedMap<Integer, SortedMap<Integer,Object>> getData();
	Collection<SortedMap<Integer, Object>> getRows();
	Class<?> getColumnClass(int columnIndex);
	int getColumnCount();
	List<String> getColumnNames();
	String getColumnName(int columnIndex);
	void setColumnNames(List<String> columnNames); 
	int getRowCount();
	Object getValueAt(int row, int col) throws IndexOutOfBoundsException;
	void setValueAt(Object value, int row, int col);
}
