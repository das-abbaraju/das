package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class StateList extends AccountActionSupport {
	private String countryString;
	private String stateString;
	private String prefix;
	private boolean needsSuffix = true;

	@Override
	@Anonymous
	public String execute() throws Exception {

		return SUCCESS;
	}
	
	@Anonymous
	public String registration() throws Exception {
		return "registration";
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

	public boolean isNeedsSuffix() {
		return needsSuffix;
	}

	public void setNeedsSuffix(boolean needsSuffix) {
		this.needsSuffix = needsSuffix;
	}

	public String getStatePrefix() {
		if (prefix == null)
			return "state";
		if (prefix.contains("State"))
			return prefix;

		return prefix + "state";
	}
}
