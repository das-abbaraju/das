package com.picsauditing.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.VelocityAdaptor;

public class EmailBean {
	protected Map<String, Object> tokens = new HashMap<String, Object>();
	protected EmailTemplates templateType;
	protected EmailQueue email;
	protected EmailQueueDAO emailQueueDAO;
	protected Permissions permissions;
	protected boolean testMode = false;
	protected UserDAO userDAO;
	protected AppPropertyDAO appPropertyDAO;
	protected String serverName;

	public EmailBean(UserDAO userDAO, AppPropertyDAO appPropertyDAO, EmailQueueDAO emailQueueDAO) {
		this.userDAO = userDAO;
		this.appPropertyDAO = appPropertyDAO;
		this.emailQueueDAO = emailQueueDAO;
		email = new EmailQueue();
		email.setCreationDate(new Date());
	}

	public void addToken(String key, Object value) {
		tokens.put(key, value);
	}

	public void sendMail() throws Exception {
		buildEmail();

		if (!testMode) {
			if (permissions != null && permissions.getUserId() > 0) {
				email.setCreatedBy(new User());
				email.getCreatedBy().setId(permissions.getUserId());
			}
			EmailSender.send(email);
		}
	}

	protected void buildEmail() throws Exception {
		tokens.put("permissions", permissions);

		String subject = getSubject();
		email.setSubject(subject);

		String body = getBody();
		email.setBody(body);
		email.setCreationDate(new Date());

		if (permissions != null && (email.getFromAddress().length() == 0)) {
			// If contractor causes the email it will be sent from
			// info@picsauditing.com
			if (!permissions.isContractor()) {
				int id = permissions.getUserId();
				User user = userDAO.find(id);
				email.setFromAddress(user.getEmail());
			}
		}

		if (testMode) {
			System.out.println(email.getSubject());
			System.out.println(email.getBody());
		}
	}

	private String getBody() throws Exception {
		final String signatureTag = "DEFAULT_SIGNATURE";

		AppProperty property = appPropertyDAO.find("email_" + templateType.name() + "_body");
		String body = property.getValue();

		if (body.contains(signatureTag)) {
			AppProperty signature = appPropertyDAO.find(signatureTag);
			// The body needs an email signature
			body = body.replace(signatureTag, signature.getValue());
		}
		body = VelocityAdaptor.mergeTemplate(body, tokens);
		return body;
	}

	private String getSubject() throws Exception {
		AppProperty property = appPropertyDAO.find("email_" + templateType.name() + "_subject");
		if (property == null)
			return "Email From PICS";
		String subject = VelocityAdaptor.mergeTemplate(property.getValue(), tokens);
		return subject;
	}

	/**
	 * Turn off email sending
	 * 
	 * @param testMode
	 */
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		addToken("permissions", permissions);
	}

	public EmailQueue getEmail() {
		return email;
	}

	/**
	 * HTML Helper method that returns who the email was sent to
	 * 
	 * @return
	 */
	public String getSentTo() {
		return email.getToAddresses();
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Map<String, Object> getTokens() {
		return tokens;
	}

	public void setTokens(Map<String, Object> tokens) {
		this.tokens = tokens;
	}

}
