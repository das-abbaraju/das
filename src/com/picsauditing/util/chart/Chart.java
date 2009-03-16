package com.picsauditing.util.chart;

abstract public class Chart extends AbstractElement {
	protected StringBuilder xml = new StringBuilder();

	// Chart Titles and Axis Names
	protected String caption;
	protected String subCaption;
	protected String xAxisName;
	protected String yAxisName;

	// Functional Attributes
	protected boolean showValues = false;
	protected boolean showPercentageValues = false;
	protected boolean animation = true;
	protected int palette = 1;
	protected boolean connectNullData = true;
	protected boolean showLabels = true;
	protected boolean rotateLabels = false;
	protected String clickURL;
	protected boolean defaultAnimation = true;
	protected boolean showLegend;

	protected String numberPrefix;
	
	protected boolean empty = true;

	public String toString() {
		xml.append("<chart");

		append(xml, "showLegend", showLegend);
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

		addData();

		xml.append("</chart>");
		return xml.toString();
	}

	abstract protected void addData();

	abstract public boolean hasData();

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

	public boolean isEmpty() {
		return empty;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

}
