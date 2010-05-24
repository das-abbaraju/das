package com.picsauditing.util.chart;

public class TrendLine extends AbstractElement {
	private float startValue;
	private float endValue;
	private String displayValue;
	private String color;
	
	public TrendLine(float startValue, float endValue, String displayValue) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.displayValue = displayValue;
	}

	public String toString() {
		StringBuilder xml = new StringBuilder();
		// Create the <set> start
		xml.append("<line");
		append(xml, "startValue", startValue);
		append(xml, "endValue", endValue);
		append(xml, "displayValue", displayValue);
		append(xml, "color", color);
		xml.append(" />");
		return xml.toString();
	}

	// ///////////// GETTERS & SETTERS //////////////////
	
	public float getStartValue() {
		return startValue;
	}
	
	public void setStartValue(float startValue) {
		this.startValue = startValue;
	}
	
	public float getEndValue() {
		return endValue;
	}
	
	public void setEndValue(float endValue) {
		this.endValue = endValue;
	}
	
	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
