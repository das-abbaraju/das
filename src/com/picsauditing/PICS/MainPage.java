package com.picsauditing.PICS;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class MainPage {
	public static final String DEBUG_COOKIE_NAME = "debugging";
	public static final String PICS_PHONE_NUMBER = "+1-949-936-4500";
	public static final String PICS_SALES_NUMBER = "1-877-725-3022";
	public static final String PICS_FAX_NUMBER = "949-269-9177";

	private final static Logger logger = LoggerFactory.getLogger(MainPage.class);
    private static String ERROR_FINDING = "Error finding {} for country {}\n{}";
    private static TranslationService translationService = TranslationServiceFactory.getTranslationService();

	private AppPropertyDAO appPropertyDAO;
	private CountryDAO countryDAO;

	private HttpServletRequest request;
	private HttpSession session;
	private Permissions permissions;

	public MainPage() {
	}

	public MainPage(HttpServletRequest request, HttpSession session) {
		this.request = request;
		this.session = session;
	}

	public boolean isPageSecure() {
		if (request != null) {
			if (request.isSecure()) {
				return true;
			} else if (request.getLocalPort() == 443) {
				return true;
			} else if (request.getLocalPort() == 81) {
				return true;
			}
		}

		return false;
	}

	public boolean isDebugMode() {
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (DEBUG_COOKIE_NAME.equals(cookie.getName())) {
					return Boolean.valueOf(cookie.getValue());
				}
			}
		}

		return false;
	}

	public boolean isDisplaySystemMessage() {
		return "1".equals(appPropertyDAO().getProperty(AppProperty.SYSTEM_MESSAGE));
	}

	public Permissions getPermissions() {
		if (permissions != null) {
			return permissions;
		}

		if (session != null && session.getAttribute("permissions") != null) {
			try {
				permissions = (Permissions) session.getAttribute("permissions");
			} catch (Exception e) {
				logger.error("Permissions object was loaded in session but was not valid", e);
			}
		}

		if (permissions == null) {
			permissions = new Permissions();
		}

		return permissions;
	}

	public String getPhoneNumber() {
		return getPhoneNumber(null);
	}

	public String getPhoneNumber(String country) {
		String phoneNumber = phoneNumberByCountry(country);
		if (Strings.isNotEmpty(phoneNumber)) {
			return phoneNumber;
		}

		Permissions permissions = getPermissions();
		phoneNumber = phoneNumberByCountry(permissions.getCountry());
		if (Strings.isNotEmpty(phoneNumber)) {
			return phoneNumber;
		}

		return PICS_PHONE_NUMBER;
	}

	public String getSalesPhoneNumber() {
		return getSalesPhoneNumber(getPermissions().getCountry());
	}

	public String getSalesPhoneNumber(String country) {
		if (Strings.isNotEmpty(country)) {
			try {
				String salesPhoneNumber = countryDAO().find(country).getSalesPhone();

				if (Strings.isNotEmpty(salesPhoneNumber)) {
					return salesPhoneNumber;
				}
			} catch (Exception e) {
				logger.error(ERROR_FINDING, new Object[] {"sales phone", country, e});
			}
		}

		return PICS_SALES_NUMBER;
	}

	public String getFaxNumber() {
		return getFaxNumber(getPermissions().getCountry());
	}

	public String getFaxNumber(String country) {
		if (Strings.isNotEmpty(country)) {
			try {
				String faxNumber = countryDAO().find(country).getFax();

				if (Strings.isNotEmpty(faxNumber)) {
					return faxNumber;
				}
			} catch (Exception e) {
                logger.error(ERROR_FINDING, new Object[] {"fax number", country, e});
			}
		}

		return PICS_FAX_NUMBER;
	}

	public String getCountryI18nKey() {
		return getCountryI18nKey(null);
	}

	public String getCountryI18nKey(String isoCode) {
		if (Strings.isEmpty(isoCode)) {
			isoCode = getPermissions().getCountry();
		}

		if (Strings.isEmpty(isoCode)) {
			return null;
		}

		Country country = countryDAO().find(isoCode);
		if (country != null) {
			return country.getI18nKey();
		}

		return null;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	private AppPropertyDAO appPropertyDAO() {
		if (appPropertyDAO == null) {
			appPropertyDAO = SpringUtils.getBean(SpringUtils.APP_PROPERTY_DAO);
		}

		return appPropertyDAO;
	}

	private CountryDAO countryDAO() {
		if (countryDAO == null) {
			countryDAO = SpringUtils.getBean(SpringUtils.COUNTRY_DAO);
		}

		return countryDAO;
	}

	private String phoneNumberByCountry(String country) {
		if (Strings.isNotEmpty(country)) {
			try {
				String phoneNumber = countryDAO().find(country).getPhone();
				if (Strings.isNotEmpty(phoneNumber)) {
                    return insertI18nPhoneDescriptionsForMultiplePhoneNumbers(country, phoneNumber);
				}
			} catch (Exception e) {
                logger.error(ERROR_FINDING, new Object[] {"phone", country, e});
			}
		}
		return null;
	}

    /*
        See PICS-11790. This is a temporary work-around to deal with the fact that China has two phone numbers
        that need to be differentiated on the UI for the user and that the differentiating labels need to be
        translatable. We want to delay a larger, more robust solution until we have 2 more examples of countries
        with multiple phone numbers at which point the "rule of three" will kick in and we will design something
        more suitable given more full information.

        Please do not be the developer who adds a third one in this method/class/project to deal with another
        countries with more than one phone number. But you can be the second (add to this comment, too).
     */
    private String insertI18nPhoneDescriptionsForMultiplePhoneNumbers(String country, String phoneNumber) {
        if (country == null || phoneNumber == null) {
            return phoneNumber;
        } else if (country.equals(Country.CHINA_ISO_CODE)) {
            String[] twoPhoneNumbers = phoneNumber.split(" ");
            if (twoPhoneNumbers == null || twoPhoneNumbers.length != 2) {
                // something has changed with the China phone value. Since this is a hack, I'm not going to try
                // to recover. Whatever that new value is, that's what the UI gets and we'll likely see a JIRA
                // for it
                return phoneNumber;
            }
            return formatTwoPhoneNumbersWithLabels("Main.Phone.China.Label1", "Main.Phone.China.Label2", twoPhoneNumbers);
        }
        return phoneNumber;
    }

    private String formatTwoPhoneNumbersWithLabels(String label1, String label2, String[] twoPhoneNumbers) {
        StringBuffer adjustedPhone = new StringBuffer();
        adjustedPhone.append(translationService.getText(label1, TranslationActionSupport.getLocaleStatic()));
        adjustedPhone.append(": ");
        adjustedPhone.append(twoPhoneNumbers[0]);
        adjustedPhone.append(" | ");
        adjustedPhone.append(translationService.getText(label2, TranslationActionSupport.getLocaleStatic()));
        adjustedPhone.append(": ");
        adjustedPhone.append(twoPhoneNumbers[1]);
        return adjustedPhone.toString();
    }
}
