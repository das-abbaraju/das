package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CountrySubdivision;

@SuppressWarnings("serial")
public class CountrySubdivisionList extends AccountActionSupport {
	private String countryString;
	private String countrySubdivisionString;
	private String prefix;
	private boolean needsSuffix = true;
	private boolean required;

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

	public String getCountrySubdivisionString() {
		return countrySubdivisionString;
	}

	public void setCountrySubdivisionString(String countrySubdivisionString) {
		this.countrySubdivisionString = countrySubdivisionString;
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
	
    public CountrySubdivision getAccountCountrySubdivision() {
        CountrySubdivision value = null;
        value = new CountrySubdivision(countrySubdivisionString);
//        Account account = accountDAO.find(id, "Contractor");
//        if (account != null) {
//                value=account.getCountrySubdivision();
//        }
        return value;
}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getCountrySubdivisionPrefix() {
		if (prefix == null)
			return "countrySubdivision";
		if (prefix.contains("CountrySubdivision"))
			return prefix;

		return prefix + "countrySubdivision";
	}
}
