package com.picsauditing.PICS;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
	private Database database = new Database();
	private HttpServletRequest request;
	private HttpSession session;

	private final int LOCALE_INDEX = 2;

	public MainPage(HttpServletRequest request, HttpSession session) {
		this.request = request;
		this.session = session;
	}

	public Permissions getPermissions() {
		Permissions permissions = new Permissions();

		if (session != null && session.getAttribute("permissions") != null) {
			permissions = (Permissions) session.getAttribute("permissions");
		}

		return permissions;
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

	public Map<Locale, String> getSystemMessages() {
		Map<Locale, String> systemMessages = new TreeMap<Locale, String>(new LocaleComparator());
		try {
			List<BasicDynaBean> results = database.select(createSelectSQLStatement(), false);

			processResults(systemMessages, results);
		} catch (Exception e) {
			PicsLogger.log("Unable to find system messages in app_translations");
		}

		if (systemMessages.isEmpty()) {
			getSystemMessageFromAppProperties(systemMessages);
		}

		return systemMessages;
	}

	public boolean isLiveChatEnabled() {
		AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");

		AppProperty liveChatState = appPropertyDAO.find("PICS.liveChat");
		return liveChatState != null && "1".equals(liveChatState.getValue());
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
	private void processResults(Map<Locale, String> systemMessages, List<BasicDynaBean> results) {
		for (BasicDynaBean result : results) {
			if (result.get("msgKey") != null && result.get("msgValue") != null) {
				String msgKey = result.get("msgKey").toString();
				String msgValue = result.get("msgValue").toString();

				String[] msgKeyParts = msgKey.split("\\.");

				if (LOCALE_INDEX < msgKeyParts.length) {
					systemMessages.put(new Locale(msgKeyParts[LOCALE_INDEX]), msgValue);
				}
			}
		}
	}

	private void getSystemMessageFromAppProperties(Map<Locale, String> systemMessages) {
		AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
		AppProperty appProperty = appPropertyDAO.find("SYSTEM.MESSAGE");

		Locale locale = TranslationActionSupport.getLocaleStatic();

		if (!locale.getLanguage().equals("en")) {
			systemMessages.put(locale, I18nCache.getInstance().getText("global.BetaTranslations", locale));
		} else if (appProperty != null && !Strings.isEmpty(appProperty.getValue())) {
			systemMessages.put(new Locale("en"), appProperty.getValue());
		}
	}

	public class LocaleComparator implements Comparator<Locale> {
		@Override
		public int compare(Locale o1, Locale o2) {
			return o1.getLanguage().compareTo(o2.getLanguage());
		}
	}
}
