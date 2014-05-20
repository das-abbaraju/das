// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class CookiesUtil {

    private static final String HEADER_HOST = "Host";
    private static final String HEADER_HOST_FORWARDED = "X-Forwarded-Host";
    private static final String HEADER_REFERER = "Referer";
    private static final String HEADER_REFERER_REDIRECT = "se_url";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_ACCEPT = "Accept";

    public static void createSsoCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieName, final String tokens) {
        String domain = getHostHeader(request);
        String cookieValue = tokens;// encodeCookie(tokens);
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static void deleteCookie(final String cookieName, final HttpServletRequest request, final HttpServletResponse response) {
        String domain = getHostHeader(request);
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        if (domain != null)
            cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static String getCookieVal(final String cookieName, final HttpServletRequest request, final HttpServletResponse response) {
        Cookie[] requestCookies = request.getCookies();
        if (requestCookies != null) {
            for (Cookie c : requestCookies) {
                if (c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static String getHostHeader(HttpServletRequest request) {
        if (request == null)
            return null;
        String host = request.getHeader(HEADER_HOST_FORWARDED);
        if (host == null)
            host = request.getHeader(HEADER_HOST);
        return host;
    }

}
