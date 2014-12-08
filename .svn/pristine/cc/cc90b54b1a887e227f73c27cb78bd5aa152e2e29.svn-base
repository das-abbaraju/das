// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.web.configuration.springmvc;

/**
 * Description here!
 *
 *
 * @author
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.klark.common.Constants;
import com.klark.util.CookiesUtil;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    private String redirectPage;

    public String getRedirectPage() {
        return redirectPage;
    }

    public void setRedirectPage(String redirectPage) {
        this.redirectPage = redirectPage;
    }

    // before the actual handler will be executed
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /*String uri = request.getRequestURI();
        if (uri.contains("contact.view") || uri.contains("login")) {
            return true;
        }
        String userauth = CookiesUtil.getCookieVal(Constants.LOGGED_IN_COOKIE, request, response);
        if (org.springframework.util.StringUtils.isEmpty(userauth)) {
            // maintenance time, send to maintenance page
            //TOODO uncomment below
        	response.sendRedirect("login");
            return false;
        }*/
        return true;
    }
}