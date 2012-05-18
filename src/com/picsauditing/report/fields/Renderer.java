package com.picsauditing.report.fields;


public class Renderer extends JavaScript {

	private String action = "";
	private String[] parameters;
	
	public Renderer(String action, String[] parameters) {
		this.action = action;
		this.parameters = parameters;
	}
	
	public String toJSONString() {
		StringBuilder js = new StringBuilder();
		js.append("function(value, metaData, record) {return Ext.String.format('<a href=\"");
		js.append(action);
		js.append("</a>'");
		for (String parameter : parameters) {
			js.append(",record.data.").append(parameter.trim());
		}
		js.append(");}");

		return js.toString();
	}

}
