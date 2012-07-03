package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class MainPage {
	private AppPropertyDAO appPropertyDAO;

	private Database database = new Database();
	private HttpServletRequest request;
	private HttpSession session;

	private List<SystemMessage> systemMessages;

	private final int LOCALE_INDEX = 2;

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
		return "1".equals(getAppPropertyDAO().getProperty("PICS.liveChat"));
	}

	public boolean isDebugMode() {
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("debugging".equals(cookie.getName())) {
					return Boolean.valueOf(cookie.getValue());
				}
			}
		}

		return false;
	}

	public boolean isDisplaySystemMessage() {
		return "1".equals(getAppPropertyDAO().getProperty("PICS.showInternationalSystemMessages"));
	}

	public Permissions getPermissions() {
		Permissions permissions = new Permissions();

		if (session != null && session.getAttribute("permissions") != null) {
			permissions = (Permissions) session.getAttribute("permissions");
		}

		return permissions;
	}

	public List<SystemMessage> getSystemMessages() {
		if (systemMessages == null) {
			systemMessages = new ArrayList<SystemMessage>();

			if (isDisplaySystemMessage()) {
				try {
					List<BasicDynaBean> results = database.select(createSelectSQLStatement(), false);

					processResults(systemMessages, results);
				} catch (Exception e) {
					PicsLogger.log("Unable to find system messages in app_translations");
				}
			}

			if (systemMessages.isEmpty()) {
				getSystemMessageFromAppProperties(systemMessages);
			}
		}

		return systemMessages;
	}

	private AppPropertyDAO getAppPropertyDAO() {
		if (appPropertyDAO == null) {
			appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
		}

		return appPropertyDAO;
	}

	private String createSelectSQLStatement() {
		SelectSQL sql = new SelectSQL("app_translation a");
		sql.addField("a.msgKey");
		sql.addField("a.msgValue");

		sql.addWhere("a.msgKey LIKE 'SYSTEM.message.%.text'");
		sql.addWhere("LENGTH(a.msgValue) > 0");

		sql.addOrderBy("a.msgKey");

		String sqlString = sql.toString();
		return sqlString;
	}

	/**
	 * We're assuming that the system keys are in this format:
	 * "SYSTEM.message[.x.y.z].&lt;locale&gt;", where x, y, z (and others) are
	 * optional tokens and locale is required.<br />
	 * <br />
	 * e.g. "SYSTEM.message.en" or "SYSTEM.message.situation.readonly.fr"
	 * 
	 * @param systemMessages
	 * @param results
	 */
	private void processResults(List<SystemMessage> systemMessages, List<BasicDynaBean> results) {
		for (BasicDynaBean result : results) {
			if (result.get("msgKey") != null && result.get("msgValue") != null) {
				String msgValue = result.get("msgValue").toString();

				String[] msgKeyParts = result.get("msgKey").toString().split("\\.");

				if (LOCALE_INDEX < msgKeyParts.length) {
					String localeString = msgKeyParts[LOCALE_INDEX];

					Locale locale = new Locale(localeString);
					String link = getTranslation("SYSTEM.message." + localeString);
					systemMessages.add(new SystemMessage(locale, link, msgValue));
				}
			}
		}
	}

	private void getSystemMessageFromAppProperties(List<SystemMessage> systemMessages) {
		Locale locale = TranslationActionSupport.getLocaleStatic();

		if (!locale.getLanguage().equals("en")) {
			systemMessages.add(new SystemMessage(locale, null, getTranslation("global.BetaTranslations")));
		} else {
			AppProperty appProperty = getAppPropertyDAO().find("SYSTEM.MESSAGE");

			if (appProperty != null && !Strings.isEmpty(appProperty.getValue())) {
				systemMessages.add(new SystemMessage(Locale.ENGLISH, null, appProperty.getValue()));
			}
		}
	}

	private String getTranslation(String key) {
		return I18nCache.getInstance().getText(key, TranslationActionSupport.getLocaleStatic());
	}

	public class LocaleComparator implements Comparator<Locale> {
		@Override
		public int compare(Locale o1, Locale o2) {
			return o1.getLanguage().compareTo(o2.getLanguage());
		}
	}

	public class SystemMessage implements Comparable<SystemMessage> {
		private Locale locale = Locale.ENGLISH;
		private String link;
		private String value;

		public SystemMessage(Locale locale, String link, String value) {
			this.locale = locale;
			this.link = link;
			this.value = value;
		}

		public Locale getLocale() {
			return locale;
		}

		public void setLocale(Locale locale) {
			this.locale = locale;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int compareTo(SystemMessage arg0) {
			return locale.getLanguage().compareTo(arg0.getLocale().getLanguage());
		}
	}
}
