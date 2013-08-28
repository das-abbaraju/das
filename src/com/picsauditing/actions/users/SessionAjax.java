package com.picsauditing.actions.users;

import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

public class SessionAjax extends PicsActionSupport {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Anonymous
	public String getUserSession() {
		json = new JSONObject();

		if (sessionCookieIsValidAndNotExpired()) {
			json = getSessionExpiration();
		} else {
			json.put("error", "No User Session Found");
		}

		return JSON;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getSessionExpiration() {
		json = new JSONObject();

		if (permissions != null) {
			json.put("sessionDuration", permissions.getSessionCookieTimeoutInSeconds());
		} else {
			json.put("error", "Permissions object was null.");
		}

		return json;
	}

	// The session timeout is reset in the SecurityInterceptor
	@SuppressWarnings("unchecked")
	public String resetTimeout() {
		json = new JSONObject();

		json.put("result", "success");

		return JSON;
	}
}
