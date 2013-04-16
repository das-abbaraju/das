package com.picsauditing.model.l10n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class InvoiceLocaleUtil {

	private static AppPropertyDAO propertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);
	private static List<Locale> localesToEmailInvoicesInBPROCS = new ArrayList<Locale>();
	private static CountryDAO countryDAO = SpringUtils.getBean(SpringUtils.COUNTRY_DAO);

	static {
		AppProperty appProperty = propertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS);

		String localesToEmailCSV = null;
		if (appProperty != null) {
			localesToEmailCSV = appProperty.getValue();
		}

		if (Strings.isNotEmpty(localesToEmailCSV)) {
			List<String> localesToEmailStringList = Arrays.asList(localesToEmailCSV.split("\\s*,\\s*"));

			for (String singleLocaleToEmailString : localesToEmailStringList) {
				if (singleLocaleToEmailString.trim().isEmpty() || singleLocaleToEmailString.trim().equals("_")) {
					continue;
				}
				String[] splitSingleLocale = singleLocaleToEmailString.split("_");
				localesToEmailInvoicesInBPROCS.add(new Locale(splitSingleLocale[0], splitSingleLocale[1]));
			}
		}
	}

	public static Boolean invoiceIsToBeEmailedViaBPROCS(ContractorAccount contractor) {

		if ((contractor == null) || (contractor.getBillingCountry() == null) || (contractor.getBillingCountry().getIsoCode() == null)) {
			return false;
		}

		Locale localeToCheck = countryDAO.findLocaleByCountryISO(contractor.getBillingCountry().getIsoCode());

		if (localesToEmailInvoicesInBPROCS.contains(localeToCheck)) {
			return true;
		} else {
			return false;
		}
	}
}