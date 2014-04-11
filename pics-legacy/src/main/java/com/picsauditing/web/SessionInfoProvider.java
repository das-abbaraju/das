package com.picsauditing.web;

import com.picsauditing.access.Permissions;

import java.util.Map;

public interface SessionInfoProvider {

	int getUserId();

	int getAccountId();

	int getId();

	Permissions getPermissions();

	NameSpace getNamespace();

	Map<String, Object> getSession();

	void setSession(Map<String, Object> session);

	void putInSession(String key, Object value);

}
