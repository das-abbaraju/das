package com.picsauditing.util.chart;

public class Set extends AbstractElement {
	protected String label;
	protected float value = 0;
	
	protected String borderColor;
	protected String borderAlpha;
	protected String isSliced;
	protected String color;
	protected String link;
	protected String toolText;
	protected boolean dashed = false;
	protected String alpha;
	
	public String toString() {
		StringBuilder xml = new StringBuilder();
		// Create the <set> start
		xml.append("<set");
		append(xml, "label", label);
		append(xml, "value", value);
		append(xml, "color", color);
		append(xml, "toolText", toolText);
		append(xml, "dashed", dashed);
		append(xml, "link", link);
		xml.append(" />");
		return xml.toString();
	}


	/////////////// GETTERS & SETTERS //////////////////

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

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getBorderAlpha() {
		return borderAlpha;
	}

	public void setBorderAlpha(String borderAlpha) {
		this.borderAlpha = borderAlpha;
	}

	public String getIsSliced() {
		return isSliced;
	}

	public void setIsSliced(String isSliced) {
		this.isSliced = isSliced;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getToolText() {
		return toolText;
	}

	public void setToolText(String toolText) {
		this.toolText = toolText;
	}

	public boolean isDashed() {
		return dashed;
	}

	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

}
