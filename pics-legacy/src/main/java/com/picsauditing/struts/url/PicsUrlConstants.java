package com.picsauditing.struts.url;

public class PicsUrlConstants {

	public static final String USER_MODE_SWITCH_URL = "/users/switch-mode/%s";
	public static final String LOGIN_URL = "/Login.action";

	public static String buildUrl(final String url, final Object... params) {
		return String.format(url, params);
	}

}
