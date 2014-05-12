package com.picsauditing.employeeguard.models;

public class AboutModel {
	private String os;
	private String browser;
	private String environment;
	private String appservername;
	private String dbservername;
	private String time;


	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getAppservername() {
		return appservername;
	}

	public void setAppservername(String appservername) {
		this.appservername = appservername;
	}

	public String getDbservername() {
		return dbservername;
	}

	public void setDbservername(String dbservername) {
		this.dbservername = dbservername;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
