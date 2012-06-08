package com.picsauditing.util.chart;

import java.util.Map;
import java.util.TreeMap;

public class ChartMultiSeries extends Chart {

	protected Map<String, Category> categories = new TreeMap<String, Category>();
	protected Map<String, DataSet> dataSets = new TreeMap<String, DataSet>();

	protected void addData() {
		xml.append("<categories>");
		for (String key : categories.keySet())
			xml.append(categories.get(key).toString());
		xml.append("</categories>");

		for (String key : dataSets.keySet()) {
			dataSets.get(key).setCategories(categories);
			xml.append(dataSets.get(key).toString());
		}
	}

	public boolean hasData() {
		return dataSets.size() > 0;
	}

	public void addCategory(Category category) {
		categories.put(category.getIndex(), category);
	}

	public Category getCategory(String label) {
		return categories.get(label);
	}

	public void addDataSet(DataSet dataSet) {
		dataSets.put(dataSet.getSeriesName(), dataSet);
		empty = false;
	}

	public DataSet getDataSet(String seriesName) {
		return dataSets.get(seriesName);
	}

	public Map<String, Category> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, Category> categories) {
		this.categories = categories;
	}

	public Map<String, DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(Map<String, DataSet> dataSets) {
		this.dataSets = dataSets;
	}
}
