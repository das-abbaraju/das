package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class HTTP500 {
	private Database database = new Database();
	private Date exceptionOccurred = new Date();
	private HttpServletRequest request;
	private ServletContext context;
	private Throwable exception;

	private boolean showCookies = true;
	private String exceptionStack = StringUtils.EMPTY;
	private String cookies = StringUtils.EMPTY;

	public HTTP500(HttpServletRequest request, ServletContext context, Throwable exception) {
		this.request = request;
		this.context = context;
		this.exception = exception;
	}

	public void saveError() {
		setShowCookies();
		logError();
		insertEmail();
	}

	public boolean isShowCookies() {
		return showCookies;
	}

	public boolean hasRequest() {
		return request != null;
	}

	public boolean hasContext() {
		return context != null;
	}

	public boolean hasException() {
		return exception != null;
	}

	public String getFullInformation() {
		return getCookies() + "\n" + getStackTrace();
	}

	private void setShowCookies() {
		try {
			String[] showCookiesParameter = request.getParameterValues("showCookies");

			if (showCookiesParameter != null && showCookiesParameter.length > 0) {
				showCookies = Boolean.parseBoolean(showCookiesParameter[0]);
				int parsedInt = Integer.parseInt(showCookiesParameter[0]);

				showCookies = showCookies || parsedInt == 1;
			}
		} catch (Exception e) {
		}
	}

	private String getStackTrace() {
		if (hasException()) {
			StringWriter stringWriter = new StringWriter();
			exception.printStackTrace(new PrintWriter(stringWriter));
			exceptionStack = stringWriter.toString();
		}

		return exceptionStack;
	}

	private String getExceptionDate() {
		if (hasException()) {
			return String.format("%tc", exceptionOccurred);
		}

		return StringUtils.EMPTY;
	}

	private String getCookies() {
		if (hasRequest() && Strings.isEmpty(cookies)) {
			for (Cookie cookie : request.getCookies()) {
				cookies += cookie.getName() + ": " + cookie.getValue() + "\n";
			}
		}

		return cookies;
	}

	private void logError() {
		if (hasRequest() && hasContext() && hasException()) {
			context.log("HTTP 500 ERROR page reached, logging exception", exception);
			context.log("HTTP 500 ERROR page reached, logging cookies\n" + getCookies());
		}
	}

	private void insertEmail() {
		try {
			String insertEmailQueue = "INSERT INTO email_queue(status, fromAddress, toAddresses, subject, body, creationDate, createdBy)"
					+ " VALUES ('Pending', 'info@picsauditing.com', 'errors@picsauditing.com', 'HTTP 500 ERROR', 'At "
					+ getExceptionDate() + " an HTTP 500 error occurred.\n" + getFullInformation() + "', NOW(), 1)";

			database.executeInsert(insertEmailQueue);
		} catch (SQLException error) {
		}
	}
}
