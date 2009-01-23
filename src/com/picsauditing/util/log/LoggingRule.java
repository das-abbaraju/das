package com.picsauditing.util.log;

public class LoggingRule {
	protected String name = null;
	protected boolean logged = false;

	public LoggingRule(String name, boolean logged) {
		super();
		this.name = name;
		this.logged = logged;
	}
	public String getName() {
		return name;
	}
	public boolean isLogged() {
		return logged;
	}
}
