package com.picsauditing.util;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Account;

public class CountryUtil {
	private static CountryDAO countryDAO;

	public static void hydrateAccountBusinessUnitIfNecessary(Account account) {
		if (account.getCountry().getBusinessUnit() == null) {
			if (countryDAO == null) {
				countryDAO = SpringUtils.getBean(SpringUtils.COUNTRY_DAO);
			}
			account.setCountry(countryDAO.find(account.getCountry().getIsoCode()));
		}
	}
}
