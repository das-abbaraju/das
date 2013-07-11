package com.picsauditing.actions;

import com.picsauditing.PICS.MainPage;
import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.util.Strings;
import org.json.simple.JSONObject;

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

	@SuppressWarnings("unchecked")
	@Anonymous
	public String phone() {
		json = new JSONObject();

		json.put("country", getText("Country"));
		json.put("picsPhoneNumber", MainPage.PICS_PHONE_NUMBER);

		Country country = countryDAO.find(countryString);
		if (country != null) {
			json.put("country", getText(country.getI18nKey()));

			if (Strings.isNotEmpty(country.getSalesPhone())) {
				json.put("picsPhoneNumber", country.getSalesPhone());
			} else if (Strings.isNotEmpty(country.getPhone())) {
				json.put("picsPhoneNumber", country.getPhone());
			}
		}

		return JSON;
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
		if (account != null && account.getCountrySubdivision() != null) {
			return account.getCountrySubdivision();
		}

		if (Strings.isNotEmpty(getCountrySubdivisionString())) {
			return new CountrySubdivision(getCountrySubdivisionString());
		}

		return null;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getCountrySubdivisionPrefix() {
		if (Strings.isEmpty(prefix)) {
			return "countrySubdivision";
		}

		if (prefix.contains("CountrySubdivision")) {
			return prefix;
		}

		return prefix + "countrySubdivision";
	}
}
