package com.picsauditing.mail;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.VelocityAdaptor;

public class EmailBean {
	protected Map<String, Object> tokens = new HashMap<String, Object>();
	protected EmailTemplates templateType;
	protected EmailMerge merge = new EmailMerge();
	protected Email email = new Email();
	protected Permissions permissions;
	protected boolean testMode = false;
	protected UserDAO userDAO;
	private AppPropertyDAO appPropertyDAO;

	public EmailBean(UserDAO userDAO, AppPropertyDAO appPropertyDAO) {
		this.userDAO = userDAO;
		this.appPropertyDAO = appPropertyDAO;
	}

	public void addToken(String key, Object value) {
		tokens.put(key, value);
	}
	
	public void sendMail() throws Exception {
		buildEmail();

		if (!testMode) {
			EmailSender sender = new EmailSender();
			sender.sendMail(email);
		}
	}

	protected void buildEmail() throws Exception {
		tokens.put("permissions", permissions);

		String subject = getSubject();
		email.setSubject(subject);

		String body = getBody();
		email.setBody(body);

		if (permissions != null && (email.getFromAddress().length() == 0)) {
			int id = permissions.getUserId();
			User user = userDAO.find(id);
			email.setFromAddress(user.getEmail());
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

	public Email getEmail() {
		return email;
	}

	/**
	 * HTML Helper method that returns who the email was sent to
	 * 
	 * @return
	 */
	public String getSentTo() {
		return email.getToAddress();
		// return John Doe <john@doe.org>
		// return this.aBean.name + " &lt;" + + "&gt;";
	}
}
