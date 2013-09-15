package com.picsauditing.actions;

import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ErrorLog;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.GridSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ExceptionAction extends PicsActionSupport {
	@Autowired
	private EmailSender emailSender;

	private Exception exception;
	private String exceptionStack;
	private int priority = 1;
	private String user_message;
	private String from_address;
	private String user_name;
	private String user = EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS;
	private String password = EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS_PASSWORD;
	private GridSender gridSenderForTesting;

	private final Logger logger = LoggerFactory.getLogger(ExceptionAction.class);

	@Override
	@Anonymous
	public String execute() {
		try {
			loadPermissions();

			tryToSaveExceptionToDatabase();

			String email = buildEmail(false);
			sendEmail(email);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return "Exception";
	}

	@Anonymous
	public String sendExceptionEmail() {
		try {
			String email = buildEmail(true);
			sendEmail(email);
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

	public String getExceptionTranslationKey() {
		return "Exception." + exception.getClass().getSimpleName().replaceAll("Exception", "");
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

	@SuppressWarnings("rawtypes")
	private String buildEmail(boolean userReported) {
		loadPermissions();
		HttpServletRequest request = ServletActionContext.getRequest();

		StringBuilder email = new StringBuilder();

		if (userReported) {
			email.append("A user has reported an error on PICS\n");

			if (permissions.isLoggedIn()) {
				setFrom_address(permissions.getEmail());
			}
		} else {
			email.append("An error occurred on PICS\n\n");
		}

		email.append(createExceptionMessage());
		email.append("\n\nServerName: " + request.getLocalName());
		email.append("\nRequestURI: " + request.getRequestURI());
		email.append("\nQueryString: " + request.getQueryString());
		email.append("\nUser IP: " + request.getRemoteAddr());

		if (permissions.isLoggedIn()) {
			email.append("\nName: " + permissions.getName());
			email.append("\nUsername: " + permissions.getUsername());
			email.append("\nAccountID: " + permissions.getAccountId());

			if (permissions.getAdminID() > 0) {
				email.append("\nAdmin: " + permissions.getAdminID());
			}

			email.append("\nType: " + permissions.getAccountType());
		} else {
			if (userReported) {
				email.append("\nName: " + user_name);
			}

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
		}

		email.append("\n\nHeaders:");

		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			email.append("\nHeader-" + headerName + ": " + request.getHeader(headerName));
		}

		return email.toString();
	}

	private void sendEmail(String email) {
		EmailQueue mail = new EmailQueue();
		mail.setSubject("PICS Exception Error"
				+ (permissions.isLoggedIn() ? " - User ID " + permissions.getUserId() : ""));
		mail.setBody(email);
		mail.setToAddresses(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS);

		if (permissions.isLoggedIn()) {
			mail.setFromAddress(permissions.getEmail());
			if (!Strings.isEmpty(getFrom_address())) {
				mail.setBccAddresses(getFrom_address());
			}
		} else {
			mail.setFromAddress(getFrom_address());
		}

		mail.setPriority(priority * 10 + 50);

		try {
			emailSender.send(mail);
		} catch (Exception e) {
			logger.error("PICS Exception Handler ... sending email via SendGrid");
			GridSender sendMail = gridSender();
			mail.setFromAddress(EmailAddressUtils.PICS_EXCEPTION_HANDLER_EMAIL);

			try {
				sendMail.sendMail(mail);
			} catch (MessagingException e1) {
				logger.error("{}", e1.getStackTrace());
			}

			logger.error(mail.getBody());
		}
	}

	private GridSender gridSender() {
		if (gridSenderForTesting == null) {
			return new GridSender(user, password);
		}
		return gridSenderForTesting;
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
}