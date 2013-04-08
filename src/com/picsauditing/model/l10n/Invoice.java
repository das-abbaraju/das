package com.picsauditing.model.l10n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Invoice {

	private static AppPropertyDAO propertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);
	private static List<Locale> localesToEmailInBPROCS = new ArrayList<Locale>();

	static {
		String localesToEmailCSV = propertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS).getValue();
		if (localesToEmailCSV != null || !localesToEmailCSV.trim().isEmpty()) {
			List<String> localesToEmailStringList = Arrays.asList(localesToEmailCSV.split("\\s*,\\s*"));

			for (String singleLocaleToEmailString : localesToEmailStringList) {
				if (singleLocaleToEmailString.trim().isEmpty() || singleLocaleToEmailString.trim().equals("_")) {
					continue;
				}
				String[] splitSingleLocale = singleLocaleToEmailString.split("_");
				localesToEmailInBPROCS.add(new Locale(splitSingleLocale[0], splitSingleLocale[1]));
			}
		}
	}

	public static List<Locale> getLocalesToEmailInBPROCS() {
		return localesToEmailInBPROCS;
	}

	public static Boolean invoiceIsToBeEmailedViaBPROCS(ContractorAccount contractor) {

		if ((contractor == null) || (contractor.getBillingCountry() == null) || (contractor.getBillingCountry().getIsoCode() == null)) {
			return false;
		}

		Locale localeToCheck = LocaleUtil.getLocaleFromCountry(contractor.getBillingCountry().getIsoCode());

		if (localesToEmailInBPROCS.contains(localeToCheck)) {
			return true;
		} else {
			return false;
		}
	}
}
