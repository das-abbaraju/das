package com.picsauditing.actions.contractors;

@SuppressWarnings("serial")
public class BillingAddress extends ContractorActionSupport {

	protected String country;

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		return SUCCESS;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
