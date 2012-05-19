package com.picsauditing.PICS;

import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ErrorLog;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.mail.GridSender;
import com.picsauditing.util.Strings;

public class ExceptionService {
	@Autowired
	private EmailSenderSpring emailSender;
	@Autowired
	protected BasicDAO dao;

	public void logException(Permissions permissions, Exception exception) {
		try {
			ErrorLog error = new ErrorLog();
			error.setAuditColumns(permissions);
			error.setCategory(exception.getClass().getSimpleName());
			error.setMessage(exception.getStackTrace().toString());
			error.setStatus("Pending");
			dao.save(error);
		} catch (Exception e) {
		}
	}

	public void sendExceptionEmail(Permissions permissions, Exception exception) {
		sendExceptionEmail(permissions, exception, "");
	}

	public void sendExceptionEmail(Permissions permissions, Exception exception, String userMessage) {
		HttpServletRequest request = ServletActionContext.getRequest();

		StringBuilder email = new StringBuilder();
		if (Strings.isEmpty(userMessage)) {
			email.append("An error occurred on PICS\n\n");
		} else {
			email.append("A user has reported an error on PICS\n");
		}
		email.append(createExceptionMessage(exception));
		email.append("\n\nServerName: " + request.getLocalName());
		email.append("\nRequestURI: " + request.getRequestURI());
		email.append("\nQueryString: " + request.getQueryString());
		email.append("\nUser IP: " + request.getRemoteAddr());
		if (permissions != null && permissions.isLoggedIn()) {
			email.append("\nName: " + permissions.getName());
			email.append("\nUsername: " + permissions.getUsername());
			email.append("\nAccountID: " + permissions.getAccountId());
			if (permissions.getAdminID() > 0)
				email.append("\nAdmin: " + permissions.getAdminID());
			email.append("\nType: " + permissions.getAccountType());
		} else {
			email.append("\nThe current user was NOT logged in.");
		}

		if (!Strings.isEmpty(userMessage)) {
			email.append("\n\nUser Message:\n");
			email.append(userMessage);
			email.append("\n");
		}

		if (!Strings.isEmpty(exception.getStackTrace().toString())) {
			email.append("\nError Message:\n");
			email.append(exception.getStackTrace().toString());
			email.append("\n");
		}

		email.append("\nHeaders:");
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
		}

		EmailQueue mail = new EmailQueue();
		mail.setSubject("PICS Exception Error"
				+ (permissions != null && permissions.isLoggedIn() ? " - User ID " + permissions.getUserId() : ""));
		mail.setBody(email.toString());
		mail.setToAddresses("errors@picsauditing.com");
		try {
			emailSender.send(mail);
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(ExceptionService.class);
			logger.error("PICS Exception Handler ... sending email via SendGrid");
			GridSender sendMail = new GridSender();
			mail.setFromAddress("\"PICS Exception Handler\"<errors@picsauditing.com>");
			try {
				sendMail.sendMail(mail);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				
			}
			logger.info(mail.getBody());
		}
	}

	private String createExceptionMessage(Exception exception) {
		String message = "";
		if (exception != null) {
			if (exception.getMessage() != null)
				message = exception.getMessage();
			else
				message = exception.toString();
		}
		return message;
	}

}
