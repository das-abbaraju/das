package com.picsauditing.actions.rest.api;

import java.util.Map;

import com.picsauditing.actions.PicsApiSupport;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;

import com.picsauditing.access.Api;
import com.picsauditing.actions.PicsActionSupport;

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
	@Api
	public String execute() {
		json = new JSONObject();
		json.put("ApiCheck", (getValueToEcho()>0)?"Success":"Fail");
		json.put("ValueToEcho", getValueToEcho());
		json.put("Note", (getValueToEcho()>0)?"valueToEcho param is 1 or more":"valueToEcho param is missing or less than 1");
		return JSON;
	}
}
