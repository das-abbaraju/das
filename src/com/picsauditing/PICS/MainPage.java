package com.picsauditing.PICS;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.search.Database;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class MainPage {
	private Database database = new Database();
	private HttpServletRequest request;
	private HttpSession session;

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
		Map<Locale, String> systemMessages = new HashMap<Locale, String>();
		try {
			List<BasicDynaBean> results = database.select("SELECT msgKey, msgValue FROM app_translation "
					+ "WHERE msgKey LIKE 'SYSTEM.message.%'", false);

			processResults(systemMessages, results);
		} catch (Exception e) {
			PicsLogger.log("MainPage: Unable to find system messages in app_translations");
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
				int lastIndex = msgKeyParts.length - 1;

				if (lastIndex < 0) {
					lastIndex = 0;
				}

				systemMessages.put(new Locale(msgKeyParts[lastIndex]), msgValue);
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
}
