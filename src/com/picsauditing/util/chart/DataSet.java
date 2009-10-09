package com.picsauditing.util.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataSet extends AbstractElement {
	protected String seriesName;
	protected String color;
	protected String alpha;
	protected String ratio;
	protected Boolean showValues = true;
	protected Boolean dashed;
	protected Boolean includeInLegend = true;
	
	protected Map<String, Category> categories;
	protected Map<String, Set> sets = new HashMap<String, Set>();

	public String toString() {
		StringBuilder xml = new StringBuilder();
		// Create the <set> start
		xml.append("<dataset");
		append(xml, "seriesName", seriesName);
		append(xml, "color", color);
		append(xml, "alpha", alpha);
		append(xml, "ratio", ratio);
		append(xml, "showValues", showValues);
		append(xml, "dashed", dashed);
		append(xml, "includeInLegend", includeInLegend);
		xml.append(">");
		
		for (String category : categories.keySet()) {
			Set set = sets.get(category);
			if (set == null)
				set = new Set();
			xml.append(set.toString());
		}
		xml.append("</dataset>");

		return xml.toString();
	}

	public void setCategories(Map<String, Category> categories) {
		this.categories = categories;
	}
	
	public Map<String, Set> getSets() {
		return sets;
	}

	public void setSets(Map<String, Set> sets) {
		this.sets = sets;
	}

	
	public void addSet(Set set) {
		sets.put(set.getIndex(), set);
	}

	public void addSet(String category, Set set) {
		sets.put(category, set);
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public Boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(Boolean showValues) {
		this.showValues = showValues;
	}

	public Boolean isDashed() {
		return dashed;
	}

	public void setDashed(Boolean dashed) {
		this.dashed = dashed;
	}

	public Boolean isIncludeInLegend() {
		return includeInLegend;
	}

	public void setIncludeInLegend(Boolean includeInLegend) {
		this.includeInLegend = includeInLegend;
	}
}
