package com.picsauditing.util.chart;

public class Category extends AbstractElement {
	protected String label;
	protected boolean showLabel;
	protected String toolText;
	
	public String toString() {
		StringBuilder xml = new StringBuilder();
		xml.append("<category");
		append(xml, "label", label);
		append(xml, "showLabel", showLabel);
		append(xml, "toolText", toolText);
		xml.append(" />");
		return xml.toString();
	}
}
