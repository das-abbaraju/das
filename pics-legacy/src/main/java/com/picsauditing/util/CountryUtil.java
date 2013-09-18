package com.picsauditing.util;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Country;

import java.io.IOException;

public class CountryUtil {
	private static CountryDAO countryDAO;

	public static void hydrateBusinessUnitIfNecessary(Account account) {
		if (account.getCountry().getBusinessUnit() == null) {
			// TODO: We should not be initiating daos in static methods. Find a way to do this without SpringUtils.
			if (countryDAO == null) {
				countryDAO = SpringUtils.getBean(SpringUtils.COUNTRY_DAO);
			}
			account.setCountry(countryDAO.find(account.getCountry().getIsoCode()));
		}
	}

	public static Country getCountryFromAccountOrIP(Account account) throws IOException {
		Country country = null;
		if (account != null && account.getCountry() != null) {
			hydrateBusinessUnitIfNecessary(account);
			country = account.getCountry();
		} else {
			Geo geo = new Geo();
			String countryCode = geo.getCountryCodeFromRequestIP();
			country = countryDAO.find(countryCode);
		}
		return country;
	}
}
