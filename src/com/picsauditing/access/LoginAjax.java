package com.picsauditing.access;

import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class LoginAjax extends PicsActionSupport {

	private String callback;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		json = new JSONObject();
		json.put("loggedin", permissions.isLoggedIn());
		return SUCCESS;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
}
