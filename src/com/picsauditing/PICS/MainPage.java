package com.picsauditing.PICS;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class MainPage {
	private final static Logger logger = LoggerFactory.getLogger(MainPage.class);

	private AppPropertyDAO appPropertyDAO;

	private HttpServletRequest request;
	private HttpSession session;

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
				if ("debugging".equals(cookie.getName())) {
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
		Permissions permissions = null;

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

	private AppPropertyDAO getAppPropertyDAO() {
		if (appPropertyDAO == null) {
			appPropertyDAO = SpringUtils.getBean("AppPropertyDAO");
		}

		return appPropertyDAO;
	}
}
