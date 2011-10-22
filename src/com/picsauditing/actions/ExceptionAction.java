package com.picsauditing.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.mail.GridSender;

@SuppressWarnings("serial")
public class ExceptionAction extends PicsActionSupport {
	@Autowired
	private EmailSenderSpring emailSender;

	private String user = "info@picsauditing.com";
	private String password = "e3r4t5";

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			loadPermissions();
			Exception exception = (Exception) ActionContext.getContext().getValueStack().findValue("exception");

			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpSession session = ServletActionContext.getRequest().getSession();

			String message = "";
			@SuppressWarnings("unused")
			String cause = "Undetermined";
			String stacktrace = "";

			Date currentTime = new Date();
			// if the session hasn't been alive for a second, then redirect to the home page
			// when encountering an exception, otherwise write an email out
			if ((currentTime.getTime() - session.getCreationTime()) < 1000) {
				String redirectURL = "http://www.picsorganizer.com/";
				try {
					response.sendRedirect(redirectURL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (exception != null) {
					if (exception.getMessage() != null)
						message = exception.getMessage();
					else
						message = exception.toString();
					if (exception.getCause() != null)
						cause = exception.getCause().getMessage();

					StringWriter sw = new StringWriter();
					exception.printStackTrace(new PrintWriter(sw));
					stacktrace = sw.toString();
				}// if

				StringBuilder email = new StringBuilder();
				email.append("An error occurred on PICS\n\n");
				email.append(message);
				email.append("\n\nServerName: " + request.getServerName());
				email.append("\nRequestURI: " + request.getRequestURI());
				email.append("\nQueryString: " + request.getQueryString());
				email.append("\nRemoteAddr: " + request.getRemoteAddr());
				if (permissions.isLoggedIn()) {
					email.append("\nName: " + permissions.getName());
					email.append("\nUsername: " + permissions.getUsername());
					email.append("\nAccountID: " + permissions.getAccountId());
					if (permissions.getAdminID() > 0)
						email.append("\nAdmin: " + permissions.getAdminID());
					email.append("\nType: " + permissions.getAccountType());
				} else {
					email.append("\nThe current user was NOT logged in.");
				}

				if (stacktrace.length() > 0) {
					email.append("\n\nTrace:\n");
					email.append(stacktrace);
				}
				email.append("\n\n");
				for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
					String headerName = (String) e.nextElement();
					email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
				}
				EmailQueue mail = new EmailQueue();
				mail.setSubject("PICS Exception Error"
						+ (permissions.isLoggedIn() ? " - User ID " + permissions.getUserId() : ""));
				mail.setBody(email.toString());
				mail.setToAddresses("errors@picsauditing.com");
				try {
					emailSender.send(mail);
				} catch (Exception e) {
					System.out.println("PICS Exception Handler ... sending email via SendGrid");
					GridSender sendMail = new GridSender(user, password);
					mail.setFromAddress("\"PICS Exception Handler\"<info@picsauditing.com>");
					try {
						sendMail.sendMail(mail);
					} catch (MessagingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println(mail.getBody());
				}
			}
		} catch (Exception e) {
		}

		return "Exception";
	}

	@SuppressWarnings("unchecked")
	public String sendExceptionEmail() {
		try {
			loadPermissions();
			Exception exception = (Exception) ActionContext.getContext().getValueStack().findValue("exception");
			HttpServletRequest request = ServletActionContext.getRequest();

			int priority = Integer.parseInt(request.getParameter("priority"));
			String message = request.getParameter("user_message");
			String to_address = request.getParameter("to_address");
			String from_address = request.getParameter("from_address");
			String user_name = request.getParameter("user_name");

			StringBuilder email = new StringBuilder();
			email.append("A user has reported an error on PICS\n");
			email.append("\nRemoteAddr: " + request.getRemoteAddr());
			if (permissions.isLoggedIn()) {
				email.append("\nName: " + permissions.getName());
				email.append("\nUsername: " + permissions.getUsername());
				email.append("\nAccountID: " + permissions.getAccountId());
				if (permissions.getAdminID() > 0)
					email.append("\nAdmin: " + permissions.getAdminID());
				email.append("\nType: " + permissions.getAccountType());
			} else {
				email.append("\nName: " + user_name);
				email.append("\nThe current user was NOT logged in.");
			}

			if (message != null && (!message.equals("") || !message.equals("undefined"))) {
				email.append("\n\nUser Message:\n");
				email.append(message);
				email.append("\n");
			}

			if (exception != null) {
				email.append("\nError Message:\n");
				email.append("");
				email.append("\n");
			}
			email.append("\nHeaders:");

			for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
				String headerName = (String) e.nextElement();
				email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
			}
			EmailQueue mail = new EmailQueue();
			mail.setSubject("PICS Exception Error"
					+ (permissions.isLoggedIn() ? " - User ID " + permissions.getUserId() : ""));
			mail.setBody(email.toString());
			mail.setToAddresses(to_address);
			if (permissions.isLoggedIn()) {
				mail.setFromAddress(permissions.getEmail());
				if (!from_address.equals("undefined"))
					mail.setBccAddresses(from_address);
			} else
				mail.setFromAddress(from_address);
			mail.setPriority(priority * 10 + 50);

			try {
				emailSender.send(mail);
			} catch (Exception e) {
				System.out.println("PICS Exception Handler ... sending email via SendGrid");
				GridSender sendMail = new GridSender(user, password);
				mail.setFromAddress("\"PICS Exception Handler\"<info@picsauditing.com>");
				sendMail.sendMail(mail);
				System.out.println(mail.getBody());
			}
		} catch (Exception e) {
			return ERROR;
		}

		return SUCCESS;
	}
}
