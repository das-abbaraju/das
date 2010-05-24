package com.picsauditing.util.chart;

abstract public class Chart extends AbstractElement {
	protected StringBuilder xml = new StringBuilder();

	// Chart Titles and Axis Names
	protected String caption;
	protected String subCaption;
	protected String xAxisName;
	protected String yAxisName;
	protected String pYAxisName;
	protected String sYAxisName;

	// Functional Attributes
	protected Boolean showValues = false;
	protected Boolean showPercentageValues = false;
	protected Boolean animation = true;
	protected Integer palette = 1;
	protected Boolean connectNullData = true;
	protected Boolean showLabels = true;
	protected Boolean rotateLabels = false;
	protected String clickURL;
	protected Boolean defaultAnimation = true;
	protected Boolean showLegend;
	protected Integer yAxisMinValue;
	protected Integer yAxisMaxValue;
	protected Integer yAxisNameWidth;

	// Formatting Attibutes
	protected String numberPrefix;
	protected String numberSuffix;
	
	protected boolean empty = true;

	public String toString() {
		xml.append("<chart");

		append(xml, "showLegend", showLegend);
		append(xml, "caption", caption);
		append(xml, "subCaption", subCaption);
		append(xml, "xAxisName", xAxisName);
		append(xml, "yAxisName", yAxisName);
		append(xml, "PYAxisName", pYAxisName);
		append(xml, "SYAxisName", sYAxisName);
		append(xml, "rotateLabels", rotateLabels);
		append(xml, "showLabels", showLabels);

		append(xml, "showPercentageValues", showPercentageValues);
		append(xml, "showValues", showValues);
		append(xml, "animation", animation);
		append(xml, "palette", palette);

		append(xml, "yAxisMinValue", yAxisMinValue);
		append(xml, "yAxisMaxValue", yAxisMaxValue);
		append(xml, "yAxisNameWidth", yAxisNameWidth);

		append(xml, "numberPrefix", numberPrefix);
		append(xml, "numberSuffix", numberSuffix);

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

	public Boolean isShowPercentageValues() {
		return showPercentageValues;
	}

	public void setShowPercentageValues(Boolean showPercentageValues) {
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
	
	public String getPYAxisName() {
		return pYAxisName;
	}
	
	public void setPYAxisName(String pYAxisName) {
		this.pYAxisName = pYAxisName;
	}
	
	public String getSYAxisName() {
		return sYAxisName;
	}
	
	public void setSYAxisName(String sYAxisName) {
		this.sYAxisName = sYAxisName;
	}

	public Boolean isAnimation() {
		return animation;
	}

	public void setAnimation(Boolean animation) {
		this.animation = animation;
	}

	public Integer getPalette() {
		return palette;
	}

	public void setPalette(Integer palette) {
		this.palette = palette;
	}

	public Boolean isConnectNullData() {
		return connectNullData;
	}

	public void setConnectNullData(Boolean connectNullData) {
		this.connectNullData = connectNullData;
	}

	public Boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(Boolean showLabels) {
		this.showLabels = showLabels;
	}

	public Boolean isRotateLabels() {
		return rotateLabels;
	}

	public void setRotateLabels(Boolean rotateLabels) {
		this.rotateLabels = rotateLabels;
	}

	public String getClickURL() {
		return clickURL;
	}

	public void setClickURL(String clickURL) {
		this.clickURL = clickURL;
	}

	public Boolean isDefaultAnimation() {
		return defaultAnimation;
	}

	public void setDefaultAnimation(Boolean defaultAnimation) {
		this.defaultAnimation = defaultAnimation;
	}

	public String getNumberPrefix() {
		return numberPrefix;
	}

	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

	public Boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(Boolean showValues) {
		this.showValues = showValues;
	}

	public boolean isEmpty() {
		return empty;
	}

	public Boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(Boolean showLegend) {
		this.showLegend = showLegend;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public String getyAxisName() {
		return yAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public Integer getyAxisMinValue() {
		return yAxisMinValue;
	}

	public void setyAxisMinValue(Integer yAxisMinValue) {
		this.yAxisMinValue = yAxisMinValue;
	}

	public Integer getyAxisMaxValue() {
		return yAxisMaxValue;
	}

	public void setyAxisMaxValue(Integer yAxisMaxValue) {
		this.yAxisMaxValue = yAxisMaxValue;
	}

	public Integer getyAxisNameWidth() {
		return yAxisNameWidth;
	}

	public void setyAxisNameWidth(Integer yAxisNameWidth) {
		this.yAxisNameWidth = yAxisNameWidth;
	}

	public String getNumberSuffix() {
		return numberSuffix;
	}

	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

}
