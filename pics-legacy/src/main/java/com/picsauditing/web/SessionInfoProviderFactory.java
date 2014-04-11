package com.picsauditing.web;

public class SessionInfoProviderFactory {

	private static SessionInfoProvider sessionInfoProvider = new StrutsSessionInfoProvider();

	// Just for testing
	private static SessionInfoProvider mockSessionInfoProvider = null;

	public static SessionInfoProvider getSessionInfoProvider() {
		if (mockSessionInfoProvider == null) {
			return sessionInfoProvider;
		}

		return mockSessionInfoProvider;
	}
}
