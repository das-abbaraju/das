package com.picsauditing.actions;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.jboss.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ErrorLog;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.mail.GridSender;

@SuppressWarnings("serial")
public class ExceptionAction extends PicsActionSupport {
	@Autowired
	private EmailSenderSpring emailSender;
	@Autowired
	private BasicDAO dao;

	private Exception exception;
	private String exceptionStack;
	private int priority = 1;
	private String user_message;
	private String to_address = "errors@picsauditing.com";
	private String from_address;
	private String user_name;
	private String user = "info@picsauditing.com";
	private String password = "e3r4t5";

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			loadPermissions();

			try {
				ErrorLog error = new ErrorLog();
				error.setAuditColumns(permissions);
				error.setCategory(getException().getClass().getSimpleName());
				error.setMessage(getExceptionStack());
				error.setPriority(getPriority());
				error.setStatus("Pending");
				dao.save(error);
			} catch (Exception e) {
			}

			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpSession session = ServletActionContext.getRequest().getSession();

			String message = "";
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
					stacktrace = getExceptionStack();
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

				if (!Strings.isEmpty(stacktrace)) {
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
					mail.setFromAddress("\"PICS Exception Handler\"<errors@picsauditing.com>");
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
			HttpServletRequest request = ServletActionContext.getRequest();

			StringBuilder email = new StringBuilder();
			email.append("A user has reported an error on PICS\n");
			email.append("\nRemoteAddr: " + request.getRemoteAddr());
			if (permissions.isLoggedIn()) {
				email.append("\nName: " + permissions.getName());
				email.append("\nUsername: " + permissions.getUsername());
				email.append("\nAccountID: " + permissions.getAccountId());
				setFrom_address(permissions.getEmail());
				if (permissions.getAdminID() > 0)
					email.append("\nAdmin: " + permissions.getAdminID());
				email.append("\nType: " + permissions.getAccountType());
			} else {
				email.append("\nName: " + user_name);
				email.append("\nThe current user was NOT logged in.");
			}

			if (!Strings.isEmpty(user_message)) {
				email.append("\n\nUser Message:\n");
				email.append(user_message);
				email.append("\n");
			}

			if (!Strings.isEmpty(exceptionStack)) {
				email.append("\nError Message:\n");
				email.append(exceptionStack);
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
				if (!Strings.isEmpty(getFrom_address()))
					mail.setBccAddresses(getFrom_address());
			} else
				mail.setFromAddress(getFrom_address());
			mail.setPriority(priority * 10 + 50);

			try {
				emailSender.send(mail);
			} catch (Exception e) {
				System.out.println("PICS Exception Handler ... sending email via SendGrid");
				GridSender sendMail = new GridSender(user, password);
				mail.setFromAddress("\"PICS Exception Handler\"<errors@picsauditing.com>");
				sendMail.sendMail(mail);
				System.out.println(mail.getBody());
			}
		} catch (Exception e) {
			return "Exception";
		}

		return "Submitted";
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getExceptionStack() {
		return exceptionStack;
	}

	public void setExceptionStack(String exceptionStack) {
		this.exceptionStack = exceptionStack;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getUser_message() {
		return user_message;
	}

	public void setUser_message(String userMessage) {
		user_message = userMessage;
	}

	public String getFrom_address() {
		return from_address;
	}

	public void setFrom_address(String fromAddress) {
		from_address = fromAddress;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String userName) {
		user_name = userName;
	}
}
