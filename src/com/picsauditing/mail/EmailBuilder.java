package com.picsauditing.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.VelocityAdaptor;

/**
 * Merges an email template with a map of token data and creates an email
 * 
 * @author Trevor
 * 
 */
public class EmailBuilder {
	private String fromAddress = null;
	private String password = null;
	protected EmailTemplate template;

	protected String toAddresses = null;
	protected String ccAddresses = null;
	protected String bccAddresses = null;
	protected Map<String, Object> tokens;
	protected int conID;

	private VelocityAdaptor velocityAdaptor;
	protected EmailQueueDAO emailQueueDAO;

	// Generic token data
	protected String serverName;
	protected Permissions permissions;

	private boolean debug = false;

	public EmailBuilder() {
		velocityAdaptor = new VelocityAdaptor();
		clear();
	}

	public void clear() {
		tokens = new HashMap<String, Object>();
	}

	public EmailQueue build() throws Exception {
		EmailQueue email = new EmailQueue();

		email.setEmailTemplate(template);
		email.setCreationDate(new Date());
		email.setToAddresses(toAddresses);
		email.setFromAddress(fromAddress);
		email.setFromPassword(password);
		if (conID > 0)
			email.setContractorAccount(new ContractorAccount(conID));

		if (permissions != null) {
			tokens.put("permissions", permissions);
			if (permissions.getUserId() > 0) {
				email.setCreatedBy(new User());
				email.getCreatedBy().setId(permissions.getUserId());
			}
		}

		String subject = velocityAdaptor.merge(template.getSubject(), tokens);
		email.setSubject(subject);
		String body = velocityAdaptor.merge(template.getBody(), tokens);
		email.setBody(body);

		if (debug) {
			System.out.println(email.getSubject());
			System.out.println(email.getBody());
		}
		return email;
	}

	// Custom token setters here
	// We may consider moving this to another class or back to the controllers

	public void setConAudit(ContractorAudit conAudit) {
		setContractor(conAudit.getContractorAccount());
		addToken("audit", conAudit);
	}

	public void setContractor(ContractorAccount contractor) {
		addToken("contractor", contractor);
		toAddresses = contractor.getEmail();
		ccAddresses = contractor.getSecondEmail();
		conID = contractor.getId();
	}

	public void setUser(User user) {
		addToken("user", user);
		toAddresses = user.getEmail();
	}

	// End of custom token setters

	public String getSentTo() {
		if (Strings.isEmpty(ccAddresses))
			return toAddresses;
		return toAddresses + ", " + ccAddresses;
	}

	public void setTemplate(EmailTemplate template) {
		this.template = template;
	}

	public void setTemplate(int id) {
		EmailTemplateDAO dao = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
		setTemplate(dao.find(id));
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		addToken("permissions", permissions);
	}

	public void setServerName(String serverName) {
		addToken("serverName", serverName);
	}

	public void addToken(String key, Object value) {
		tokens.put(key, value);
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public String getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(String bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
