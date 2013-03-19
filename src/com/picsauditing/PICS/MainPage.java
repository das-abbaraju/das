package com.picsauditing.PICS;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class MainPage {
	public static final String DEBUG_COOKIE_NAME = "debugging";
	public static final String PICS_PHONE_NUMBER = "949-936-4500";

	private final static Logger logger = LoggerFactory.getLogger(MainPage.class);

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

	public boolean isLiveChatEnabled() {
		return "1".equals(getAppPropertyDAO().getProperty(AppProperty.LIVECHAT));
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
		return "1".equals(getAppPropertyDAO().getProperty(AppProperty.SYSTEM_MESSAGE));
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
		String phoneNumber = getPhoneNumberByCountry(country);
		if (Strings.isNotEmpty(phoneNumber)) {
			return phoneNumber;
		}

		Permissions permissions = getPermissions();
		phoneNumber = getPhoneNumberByCountry(permissions.getCountry());
		if (Strings.isNotEmpty(phoneNumber)) {
			return phoneNumber;
		}

		return PICS_PHONE_NUMBER;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	private AppPropertyDAO getAppPropertyDAO() {
		if (appPropertyDAO == null) {
			appPropertyDAO = SpringUtils.getBean("AppPropertyDAO");
		}

		return appPropertyDAO;
	}

	private CountryDAO getCountryDAO() {
		if (countryDAO == null) {
			countryDAO = SpringUtils.getBean("CountryDAO");
		}

		return countryDAO;
	}

	private String getPhoneNumberByCountry(String country) {
		if (Strings.isNotEmpty(country)) {
			try {
				String phoneNumber = getCountryDAO().find(country).getPhone();

				if (Strings.isNotEmpty(phoneNumber)) {
					return phoneNumber;
				}
			} catch (Exception e) {
				logger.error("Error finding phone for country {}\n{}", country, e);
			}
		}

		return null;
	}
}
