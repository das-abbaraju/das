package com.picsauditing.util.chart;

public class DataRow {
	private String series;
	private String label;
	private String index;
	private float value;
	private String displayValue;
	private String link;
	private String toolText;
	private Boolean showValue;

	public DataRow() {
	}

	public DataRow(String label) {
		this.label = label;
	}

	public DataRow(String label, float value) {
		this.label = label;
		this.value = value;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIndex() {
		if (index == null)
			index = label;
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getToolText() {
		return toolText;
	}

	public void setToolText(String toolText) {
		this.toolText = toolText;
	}

	public Boolean getShowValue() {
		return showValue;
	}

	public void setShowValue(Boolean b) {
		showValue = b;
	}

}
