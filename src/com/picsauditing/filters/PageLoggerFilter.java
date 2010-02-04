package com.picsauditing.filters;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;

public final class PageLoggerFilter implements Filter {
	private FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		String environment = System.getProperty("pagelogging.enabled");

		if (filterConfig == null)
			return;

		// Do stuff..

		if (request instanceof HttpServletRequest && environment != null && environment.equals("enabled")) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpSession session = httpRequest.getSession();
			Permissions permissions = (Permissions) session.getAttribute("permissions");

			String uri = httpRequest.getRequestURI().replaceFirst("/(app|picsWeb2)", "");

			Database db = new Database();
			try {
				long id = db.executeInsert("INSERT INTO app_page_logger (startTime, userID, pageName, url) "
						+ "VALUES ('" + new Timestamp(System.currentTimeMillis()) + "', '"
						+ ((permissions != null) ? permissions.getUserId() : "null") + "', '" + uri + "', '"
						+ httpRequest.getRequestURL()
						+ ((httpRequest.getQueryString() != null) ? "?" + httpRequest.getQueryString() : "") + "')");
				request.setAttribute("pics_page_logger_id", id);
			} catch (SQLException e) {
				System.out.println("Failed to insert logging into DB" + e.getMessage());
			}

		}

		// Run next filter if exists
		chain.doFilter(request, response);
	}
}