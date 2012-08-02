package com.picsauditing.actions;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ErrorLog;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.GridSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ExceptionAction extends PicsActionSupport {
	@Autowired
	private EmailSender emailSender;

	private Exception exception;
	private String exceptionStack;
	private int priority = 1;
	private String user_message;
	private String to_address = "errors@picsauditing.com";
	private String from_address;
	private String user_name;
	private String user = "info@picsauditing.com";
	private String password = "e3r4t5";

	private final Logger logger = LoggerFactory.getLogger(ExceptionAction.class);

	@Override
	@Anonymous
	public String execute() {
		try {
			loadPermissions();

			tryToSaveExceptionToDatabase();

			if (isSessionLessThanOneSecondOld()) {
				tryRedirectToHome();

				if (!Strings.isEmpty(url)) {
					return REDIRECT;
				}
			} else {
				HttpServletRequest request = ServletActionContext.getRequest();

				StringBuilder email = new StringBuilder();
				email.append("An error occurred on PICS\n\n");
				email.append(createExceptionMessage());
				email.append("\n\nServerName: " + request.getLocalName());
				email.append("\nRequestURI: " + request.getRequestURI());
				email.append("\nQueryString: " + request.getQueryString());
				email.append("\nUser IP: " + request.getRemoteAddr());
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

				if (!Strings.isEmpty(exceptionStack)) {
					email.append("\n\nTrace:\n");
					email.append(exceptionStack);
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
					logger.error("PICS Exception Handler ... sending email via SendGrid");
					GridSender sendMail = new GridSender(user, password);
					mail.setFromAddress("\"PICS Exception Handler\"<errors@picsauditing.com>");
					try {
						sendMail.sendMail(mail);
					} catch (MessagingException e1) {
						logger.error("{}", e1.getStackTrace());
					}
					logger.error(mail.getBody());
				}
			}
		} catch (Exception e) {
		}

		return "Exception";
	}

	private boolean isSessionLessThanOneSecondOld() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		Date currentTime = new Date();
		return (currentTime.getTime() - session.getCreationTime()) < 1000;
	}

	private void tryRedirectToHome() {
		// TODO Research this and see if it's still necessary
		try {
			setUrlForRedirect("http://www.picsorganizer.com/");
		} catch (IOException doNothing) {
			doNothing.printStackTrace();
		}
	}

	private String createExceptionMessage() {
		String message = "";
		if (exception != null) {
			if (exception.getMessage() != null)
				message = exception.getMessage();
			else
				message = exception.toString();
		}
		return message;
	}

	private void tryToSaveExceptionToDatabase() {
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
	}

	@Anonymous
	public String sendExceptionEmail() {
		try {
			loadPermissions();
			HttpServletRequest request = ServletActionContext.getRequest();

			StringBuilder email = new StringBuilder();
			email.append("A user has reported an error on PICS\n");
			email.append("\nServer: " + request.getLocalName());
			email.append("\nUser IP: " + request.getRemoteAddr());
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
				Logger logger = LoggerFactory.getLogger(ExceptionAction.class);
				logger.error("PICS Exception Handler ... sending email via SendGrid");
				GridSender sendMail = new GridSender(user, password);
				mail.setFromAddress("\"PICS Exception Handler\"<errors@picsauditing.com>");
				sendMail.sendMail(mail);
				logger.error(mail.getBody());
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
