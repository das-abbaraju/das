package com.picsauditing.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.util.Strings;

public class CookieSupport {
	public static final String SESSION_COOKIE_NAME = "PICS_ORG_SESSION";
	public static final String TARGET_IP_COOKIE_NAME = "BIGipServerPOOL";
	public static final String PRELOGIN_URL_COOKIE_NAME = "from";
	public static final String USE_BETA_COOKIE_NAME = "USE_BETA";

	public static Cookie cookieFromRequest(HttpServletRequest request, String cookieName) {
		if (null == request || Strings.isEmpty(cookieName)) {
			return null;
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static List<Cookie> cookiesFromRequestThatStartWith(HttpServletRequest request, String cookieNameStartsWith) {
		List<Cookie> matchingCookies = new ArrayList<Cookie>();
		if (null == request || Strings.isEmpty(cookieNameStartsWith)) {
			return matchingCookies;
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (!Strings.isEmpty(cookie.getName()) && cookie.getName().startsWith(cookieNameStartsWith)) {
					matchingCookies.add(cookie);
				}
			}
		}
		return matchingCookies;
	}
}
