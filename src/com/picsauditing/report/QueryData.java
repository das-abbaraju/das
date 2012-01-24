package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;

public class QueryData {
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	public QueryData(List<BasicDynaBean> rows) {
		for (BasicDynaBean dynaBean : rows) {
			Map<String, Object> row = new HashMap<String, Object>();
			for (DynaProperty column : dynaBean.getDynaClass().getDynaProperties()) {
				row.put(column.getName(), dynaBean.get(column.getName()));
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
