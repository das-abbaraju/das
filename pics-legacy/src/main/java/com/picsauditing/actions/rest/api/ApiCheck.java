package com.picsauditing.actions.rest.api;

import com.picsauditing.access.ApiRequired;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsApiSupport;

public class ApiCheck extends PicsApiSupport implements ParameterAware {
	private static final long serialVersionUID = 1L;
	protected int valueToEcho;

	public int getValueToEcho() {
		return valueToEcho;
	}

	public void setValueToEcho(int input) {
		this.valueToEcho = input;
	}

	@Override
	@ApiRequired
	public String execute() {
		json = new JSONObject();
		json.put("ApiCheck", (getValueToEcho()>0)?"Success":"Fail");
		json.put("ValueToEcho", getValueToEcho());
		json.put("Note", (getValueToEcho()>0)?"valueToEcho param is 1 or more":"valueToEcho param is missing or less than 1");
		return JSON;
	}
}