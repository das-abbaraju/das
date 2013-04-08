package com.picsauditing.model.l10n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Invoice {

	private static AppPropertyDAO propertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);

	// TODO Need to make this more efficient
	public static Boolean invoiceIsToBeEmailedViaBPROCS(ContractorAccount contractor, User user) {
		String localesToEmailCSV = propertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS).getValue();

		if (localesToEmailCSV == null || localesToEmailCSV.trim().isEmpty()) {
			return false;
		}

		if (user == null || user.getLocale() == null || user.getLocale().getLanguage() == null || contractor == null || contractor.getBillingCountry() == null) {
			return false;
		}

		List<String> localesToEmailStringList = Arrays.asList(localesToEmailCSV.split("\\s*,\\s*"));

		List<Locale> localesToEmail = new ArrayList<Locale>();
		for (String singleLocaleToEmailString : localesToEmailStringList) {
			if (singleLocaleToEmailString.trim().isEmpty() || singleLocaleToEmailString.trim().equals("_")) {
				continue;
			}
			String[] splitSingleLocale = singleLocaleToEmailString.split("_");
			localesToEmail.add(new Locale(splitSingleLocale[0], splitSingleLocale[1]));
		}

		Locale localeToCheck = new Locale(user.getLocale().getLanguage(), contractor.getBillingCountry().getIsoCode());

		if (localesToEmail.contains(localeToCheck)) {
			return true;
		} else {
			return false;
		}
	}
}
