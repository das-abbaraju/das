package com.picsauditing.actions;

@SuppressWarnings("serial")
public class PageLogger extends PicsActionSupport {
	private boolean loggingEnabled = false;

	public PageLogger() {
		String environment = System.getProperty("pagelogging.enabled");
		if (environment != null && environment.equals("enabled"))
			loggingEnabled = true;
		else
			loggingEnabled = false;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if ("enable".equals(button)) {
			setLoggingEnabled(true);
		} else if ("disable".equals(button)) {
			setLoggingEnabled(false);
		}

		return SUCCESS;
	}

	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	public void setLoggingEnabled(boolean loggingEnabled) {
		if (loggingEnabled)
			System.setProperty("pagelogging.enabled", "enabled");
		else
			System.setProperty("pagelogging.enabled", "disabled");
		this.loggingEnabled = loggingEnabled;
	}
}
