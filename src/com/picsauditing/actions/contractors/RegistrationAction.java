package com.picsauditing.actions.contractors;

import com.picsauditing.jpa.entities.Country;

public class RegistrationAction extends ContractorActionSupport {

	public Country getContractorCountry() {
		if (contractor != null && contractor.getCountry() != null) {
			return contractor.getCountry();
		}

		return countryDAO.find(Country.US_ISO_CODE);
	}

	@Override
	public String getPicsPhoneNumber() {
		return getContractorCountry().getPhone();
	}
}
