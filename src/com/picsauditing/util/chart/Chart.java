package com.picsauditing.util.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Chart extends AbstractElement {
	// Multi series variables
	private boolean multiSeries = false;
	protected float minCategory = 0;
	protected float maxCategory = 100;
	protected float categoryDifference = 10;
	
	// Chart Titles and Axis Names
	private String caption;
	private String subCaption;
	private String xAxisName;
	private String yAxisName;

	// Functional Attributes
	private boolean showValues = false;
	private boolean showPercentageValues = false;
	private boolean animation = true;
	private int palette = 1;
	private boolean connectNullData = true;
	private boolean showLabels = true;
	private boolean rotateLabels = false;
	private String clickURL;
	private boolean defaultAnimation = true;
	
	//
	private String numberPrefix;

	protected List<Category> categories = new ArrayList<Category>();
	protected List<DataSet> dataSets = new ArrayList<DataSet>();
	protected List<Set> sets = new ArrayList<Set>();

	public String toString() {
		StringBuilder xml = new StringBuilder();
		// Create the <chart> start
		xml.append("<chart");
		append(xml, "caption", caption);
		append(xml, "subCaption", subCaption);
		append(xml, "xAxisName", xAxisName);
		append(xml, "yAxisName", yAxisName);
		append(xml, "rotateLabels", rotateLabels);
		append(xml, "showLabels", showLabels);
		
		append(xml, "showPercentageValues", showPercentageValues);
		append(xml, "showValues", showValues);
		append(xml, "animation", animation);
		append(xml, "palette", palette);
		
		append(xml, "numberPrefix", numberPrefix);
		
		xml.append(">");

		if (multiSeries) {
			{
				DecimalFormat df = new DecimalFormat("#.0");
				xml.append("<categories>");
				// Create the <category></category>
				float categoryValue = minCategory;
				while(categoryValue <= maxCategory) {
					xml.append("<category");
					
					append(xml, "label", df.format(categoryValue));
					//append(xml, "showLabel", showValues);
					//String toolText = categoryValue.toString() + " to " + new Float(categoryValue+categoryDifference).toString();
					//append(xml, "toolText", toolText);
					xml.append(" />");
					categoryValue += categoryDifference;
				}
				xml.append("</categories>");
			}
			for (DataSet dataSet : dataSets) {
				// Create the <dataset></dataset>
				dataSet.setMaxCategory(maxCategory);
				dataSet.setMinCategory(minCategory);
				dataSet.setCategoryDifference(categoryDifference);
				xml.append(dataSet.toString());
			}
		} else {
			for (Set set : sets)
				xml.append(set.toString());
		}
		// Close the </chart>
		xml.append("</chart>");
		return xml.toString();
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public void addDataSet(DataSet set) {
		dataSets.add(set);
	}

	public void addSet(Set set) {
		sets.add(set);
	}
	
	// ///////////// GETTERS & SETTERS //////////////////
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public boolean isShowPercentageValues() {
		return showPercentageValues;
	}

	public void setShowPercentageValues(boolean showPercentageValues) {
		this.showPercentageValues = showPercentageValues;
	}

	public List<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	public List<Set> getSets() {
		return sets;
	}

	public void setSets(List<Set> sets) {
		this.sets = sets;
	}

	public String getSubCaption() {
		return subCaption;
	}

	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}

	public String getXAxisName() {
		return xAxisName;
	}

	public void setXAxisName(String axisName) {
		xAxisName = axisName;
	}

	public String getYAxisName() {
		return yAxisName;
	}

	public void setYAxisName(String axisName) {
		yAxisName = axisName;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public int getPalette() {
		return palette;
	}

	public void setPalette(int palette) {
		this.palette = palette;
	}

	public boolean isConnectNullData() {
		return connectNullData;
	}

	public void setConnectNullData(boolean connectNullData) {
		this.connectNullData = connectNullData;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public boolean isRotateLabels() {
		return rotateLabels;
	}

	public void setRotateLabels(boolean rotateLabels) {
		this.rotateLabels = rotateLabels;
	}

	public String getClickURL() {
		return clickURL;
	}

	public void setClickURL(String clickURL) {
		this.clickURL = clickURL;
	}

	public boolean isDefaultAnimation() {
		return defaultAnimation;
	}

	public void setDefaultAnimation(boolean defaultAnimation) {
		this.defaultAnimation = defaultAnimation;
	}

	public String getNumberPrefix() {
		return numberPrefix;
	}

	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public boolean isMultiSeries() {
		return multiSeries;
	}

	public void setMultiSeries(boolean multiSeries) {
		this.multiSeries = multiSeries;
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

}
