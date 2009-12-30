package com.picsauditing.actions;

@SuppressWarnings("serial")
public class StateList extends AccountActionSupport {
	private String countryString;
	private String stateString;
	private String prefix;

	@Override
	public String execute() throws Exception {

		return SUCCESS;
	}

	public String getCountryString() {
		return countryString;
	}

	public void setCountryString(String countryString) {
		this.countryString = countryString;
	}

	public String getStateString() {
		return stateString;
	}

	public void setStateString(String stateString) {
		this.stateString = stateString;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
