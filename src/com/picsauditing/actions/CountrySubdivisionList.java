package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class CountrySubdivisionList extends AccountActionSupport {
	private String countryString;
	private String countrySubdivisionString;
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

	public String getCountrySubdivisionPrefix() {
		if (prefix == null)
			return "countrySubdivision";
		if (prefix.contains("CountrySubdivision"))
			return prefix;

		return prefix + "countrySubdivision";
	}
}
