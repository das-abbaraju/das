package com.picsauditing.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.perf4j.logback.servlet.GraphingServlet;

import com.picsauditing.access.Permissions;

public class PicsGraphingServlet extends GraphingServlet {
	private static final long serialVersionUID = 5921523737350021428L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	Permissions permissions = (Permissions)request.getSession().getAttribute("permissions");
    	if (permissions == null || !permissions.isAdmin()) {
    		response.sendRedirect("/");
    		//throw new ServletException("You must be logged in and an admin to access this page");
    	}
    	super.doGet(request, response);
    }
}
