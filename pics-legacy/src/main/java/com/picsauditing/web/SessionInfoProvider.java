package com.picsauditing.web;

import com.picsauditing.access.Permissions;

import java.util.Map;

public interface SessionInfoProvider {

	public static final String REST_ID_PARAM_KEY = "id"; // FIXME: Get this from the container

	public static final String EMPLOYEEGUARD_NAMESPACE = "/employee-guard";
	public static final String PICSORG_NAMESPACE = "/";

	int getUserId();

	int getAccountId();

	int getId();

	Permissions getPermissions();

	NameSpace getNamespace();

	Map<String, Object> getSession();

	void setSession(Map<String, Object> session);

	void putInSession(String key, Object value);

	String getURI();

}
