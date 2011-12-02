package com.picsauditing.report;

import org.json.simple.JSONAware;

public class JavaScript implements JSONAware {
	private String script;

	public JavaScript(String script) {
		this.script = script;
	}

	public String toJSONString() {
		return script;
	}
}
