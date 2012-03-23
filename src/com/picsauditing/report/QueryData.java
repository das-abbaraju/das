package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryData {
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	public QueryData(List<String> fields, List<Object[]> rawData) {
		for (Object[] object : rawData) {
			Map<String, Object> row = new HashMap<String, Object>();
			int columnCounter = 0;
			for (String fieldName : fields) {
				row.put(fieldName, object[columnCounter]);
				columnCounter++;
			}
			data.add(row);
		}
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
}
