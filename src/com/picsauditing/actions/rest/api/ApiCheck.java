package com.picsauditing.actions.rest.api;

import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;

import com.picsauditing.access.Api;
import com.picsauditing.actions.PicsActionSupport;

public class ApiCheck extends PicsActionSupport implements ParameterAware {
	private static final long serialVersionUID = 1L;
	protected int valueToEcho;

	public int getValueToEcho() {
		return valueToEcho;
	}

	public void setValueToEcho(int input) {
		this.valueToEcho = input;
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(final String apiKey) {
		this.apiKey = apiKey;
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

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		setApiKey(lookupParam(parameters, "apiKey"));
	}

	private String lookupParam(Map<String, String[]> parameters, String key) {
		String paramValue = null;
		String[] param = parameters.get(key);
		if (param != null && param.length > 0) {
			paramValue = param[0];
		}
		return paramValue;
	}


}
