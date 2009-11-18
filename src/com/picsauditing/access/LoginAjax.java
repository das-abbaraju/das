package com.picsauditing.access;

import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class LoginAjax extends PicsActionSupport {
	private JSONObject json = new JSONObject();

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		json.put("loggedin", permissions.isLoggedIn());
		return JSON;
	}
	
	public JSONObject getJson() {
		return json;
	}
}
