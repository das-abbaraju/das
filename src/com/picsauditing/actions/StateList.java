package com.picsauditing.actions;

@SuppressWarnings("serial")
public class StateList extends AccountActionSupport {
	private String country;
	private String state;

	@Override
	public String execute() throws Exception {

		return SUCCESS;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
