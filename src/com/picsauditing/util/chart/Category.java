package com.picsauditing.util.chart;

public class Category extends AbstractElement {
	protected String index;
	protected String label;
	protected Boolean showLabels;
	protected String toolText;
	
	public String toString() {
		StringBuilder xml = new StringBuilder();
		xml.append("<category");
		append(xml, "label", label);
		append(xml, "showLabels", showLabels);
		append(xml, "toolText", toolText);
		xml.append(" />");
		return xml.toString();
	}

	public String getIndex() {
		if (index == null)
			index = label;
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean isShowLabel() {
		return showLabels;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabels = showLabel;
	}

	public String getToolText() {
		return toolText;
	}

	public void setToolText(String toolText) {
		this.toolText = toolText;
	}
	
	
}
