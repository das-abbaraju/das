package com.picsauditing.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ResultSet {
	private ArrayList<RulesRow> rows = new ArrayList<RulesRow>();
	private HashMap<String, String> columns = new HashMap<String, String>();
	private String returnType = "String";
	
	public void addColumn(String name, String type) throws Exception {
		if (name == null || name.length() == 0) throw new Exception("name must be set");
		
		ArrayList<String> validTypes = new ArrayList<String>();
		validTypes.add("String");
		validTypes.add("Integer");
		validTypes.add("Collection<String>");
		validTypes.add("Collection<Integer>");
		validTypes.add("Boolean");
		Boolean e = true;
		if (validTypes.contains(type))
			columns.put(name, type);
	}
	
	public void addRow(RulesRow row) throws Exception {
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
			parameters.get(key).getClass();
			columns.get(key);
		}
		
		for(RulesRow row : rows) {
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
