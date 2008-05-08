package com.picsauditing.util.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSet extends AbstractElement {
	protected float minCategory = 0;
	protected float maxCategory = 100;
	protected float categoryDifference = 10;
	
	protected int minIndex = 0;
	protected int maxIndex = 100;

	protected String seriesName;
	protected String color;
	protected String alpha;
	protected String ratio;
	protected boolean showValues = true;
	protected boolean dashed;
	protected boolean includeInLegend = true;

	protected Map<Integer, Set> sets = new HashMap<Integer, Set>();

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
		{
			// Create the <category></category>
			Float category = minCategory;
			while (category <= maxCategory) {
				int index = Math.round(category / categoryDifference);
				Set set = sets.get(index);
				if (set == null) {
					set = new Set();
					set.setValue(0);
				}
				xml.append(set.toString());
				category += categoryDifference;
			}
		}
		xml.append("</dataset>");

		return xml.toString();
	}
	
	public void addToMin(float value) {
		Set set = sets.get(minIndex);
		if (set == null) {
			set = new Set();
			sets.put(minIndex, set);
		}
		set.setValue(set.getValue() + value);
	}

	public void addToMax(float value) {
		Set set = sets.get(maxIndex);
		if (set == null) {
			set = new Set();
			sets.put(maxIndex, set);
		}
		set.setValue(set.getValue() + value);
	}

	public Map<Integer, Set> getSets() {
		return sets;
	}

	public void setSets(Map<Integer, Set> sets) {
		this.sets = sets;
	}

	public void addSet(Integer category, Set set) {
		sets.put(category, set);
	}

	public float getMinCategory() {
		return minCategory;
	}

	public void setMinCategory(float minCategory) {
		this.minCategory = minCategory;
	}

	public float getMaxCategory() {
		return maxCategory;
	}

	public void setMaxCategory(float maxCategory) {
		this.maxCategory = maxCategory;
	}

	public float getCategoryDifference() {
		return categoryDifference;
	}

	public void setCategoryDifference(float categoryDifference) {
		this.categoryDifference = categoryDifference;
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

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public boolean isDashed() {
		return dashed;
	}

	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}

	public boolean isIncludeInLegend() {
		return includeInLegend;
	}

	public void setIncludeInLegend(boolean includeInLegend) {
		this.includeInLegend = includeInLegend;
	}
}
