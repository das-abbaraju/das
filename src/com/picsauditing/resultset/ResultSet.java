package com.picsauditing.resultset;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSet {
	private ArrayList<ResultSetRow> rows = new ArrayList<ResultSetRow>();
	private HashMap<String, String> columns = new HashMap<String, String>();
	private String returnType = "String";
	
	public void addColumn(String name, String type) throws Exception {
		if (name == null || name.length() == 0) throw new Exception("name must be set");
		
		ArrayList<String> validTypes = new ArrayList<String>();
		validTypes.add("String");
		validTypes.add("Integer");
		
		if (validTypes.contains(type))
			columns.put(name, type);
	}
	
	public void addRow(ResultSetRow row) throws Exception {
		for(String key : row.getQuestions().keySet()) {
			if (!columns.containsKey(key)) {
				throw new Exception("Invalid column name in ResultSetRow");
			}
		}
		rows.add(row);
	}
	
	public Object evaluate(HashMap<String, Object> parameters) {
		for(String key : parameters.keySet()) {
			// TODO verify that all parameters match a valid column and the types are correct 
			
		}
		
		for(ResultSetRow row : rows) {
			if (row.equals(parameters)) {
				if (this.returnType.equals("String"))
					return row.getValue().toString();
				if (this.returnType.equals("Integer"))
					return Integer.parseInt(row.getValue().toString());
				return row.getValue();
			}
		}
		return null;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
