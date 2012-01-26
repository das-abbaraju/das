package com.picsauditing.report.fields;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.Strings;

public class SimpleReportColumn implements JSONable {
	private String name;
	private QueryFunction function = null;
	private String option;
	// We are thinking about adding the render field to support custom renderers per report
	private String renderer = null;

	public SimpleReportColumn() {
	}

	public SimpleReportColumn(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		if (function != null) {
			json.put("function", function.toString());
			if (!Strings.isEmpty(option))
				json.put("option", option);
		}
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.name = (String) json.get("name");
		Object functionObj = json.get("function");
		if (functionObj != null) {
			this.function = QueryFunction.valueOf(functionObj.toString());
			this.option = (String) json.get("option");
		}
	}

	public String getName() {
		return name;
	}

	public String getAvailableFieldName() {
		if (function == null)
			return name;
		int startOfFunction = name.lastIndexOf(function.toString());
		
		return name.substring(0, startOfFunction);
	}

	public void setName(String field) {
		this.name = field;
	}

	public QueryFunction getFunction() {
		return function;
	}

	public void setFunction(QueryFunction function) {
		this.function = function;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

}
