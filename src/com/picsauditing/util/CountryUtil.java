package com.picsauditing.util;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Account;

public class CountryUtil {
	private static CountryDAO countryDAO;

	public static void hydrateAccountBusinessUnitIfNecessary(Account account) {
		if (account.getCountry().getCountryContact().getBusinessUnit() == null) {
            // TODO: We should not be initiating daos in static methods. Find a way to do this without SpringUtils.
			if (countryDAO == null) {
				countryDAO = SpringUtils.getBean(SpringUtils.COUNTRY_DAO);
			}
			account.setCountry(countryDAO.find(account.getCountry().getIsoCode()));
		}
	}
}
