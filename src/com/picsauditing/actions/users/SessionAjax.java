package com.picsauditing.actions.users;

import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;

public class SessionAjax extends PicsActionSupport {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public String getUserSessionTimeout() {
		json = new JSONObject();

		if (permissions != null) {
			json.put("sessionTimeoutInSeconds", permissions.getSessionCookieTimeoutInSeconds());
		} else {
			json.put("error", "Permissions object was null.");
		}

		return JSON;
	}

	// The session timeout is reset in the SecurityInterceptor
	@SuppressWarnings("unchecked")
	public String resetTimeout() {
		json = new JSONObject();

		json.put("result", "success");

		return JSON;
	}
}
