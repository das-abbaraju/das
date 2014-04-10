package com.picsauditing.web;

import java.util.Map;

public interface SessionInfoProvider {

	int getUserId();

	int getAccountId();

	int getId();

	NameSpace getNamespace();

	Map<String, Object> getSession();

	void setSession(Map<String, Object> session);

	void putInSession(String key, Object value);

}
