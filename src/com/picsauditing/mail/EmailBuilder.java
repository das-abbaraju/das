package com.picsauditing.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Token;
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

	private List<Token> picsTags = null;

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
		email.setHtml(template.isHtml());
		email.setCreationDate(new Date());
		email.setFromAddress(fromAddress);
		email.setToAddresses(toAddresses);
		email.setCcAddresses(ccAddresses);
		email.setBccAddresses(bccAddresses);
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

		String subject = convertPicsTagsToVelocity(template.getSubject(), template.isAllowsVelocity());
		subject = velocityAdaptor.merge(subject, tokens);
		email.setSubject(subject);

		String body = convertPicsTagsToVelocity(template.getBody(), template.isAllowsVelocity());
		body = velocityAdaptor.merge(body, tokens);
		email.setBody(body);

		if (debug) {
			System.out.println(email.getSubject());
			System.out.println(email.getBody());
		}
		return email;
	}

	// Custom token setters here
	// We may consider moving this to another class or back to the controllers
	public void setConAudit(ContractorAudit conAudit) throws EmailException {
		setContractor(conAudit.getContractorAccount(), OpPerms.ContractorSafety);
		addToken("audit", conAudit);
	}

	public void setContractor(ContractorAccount contractor, OpPerms role) throws EmailException {
		conID = contractor.getId();
		addToken("contractor", contractor);

		Set<String> emails = new HashSet<String>();
		for (User user : contractor.getUsersByRole(role)) {
			if (Strings.isValidEmail(user.getEmail()))
				emails.add(user.getEmail());
		}
		if (emails.size() > 0)
			toAddresses = Strings.implode(emails, ", ");
		else {
			if (role.equals(OpPerms.ContractorAdmin)) {
				// The contractor doesn't have any admin users
				// so this results in an endless loop
				throw new EmailException("Could not find a valid email address for " + contractor.getName());
			}
			setContractor(contractor, OpPerms.ContractorAdmin);
		}
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

	private List<Token> getPicsTags() {
		if (picsTags == null) {
			TokenDAO dao = (TokenDAO) SpringUtils.getBean("TokenDAO");
			picsTags = dao.findByType(template.getListType());
		}
		return picsTags;
	}

	/**
	 * Convert tokens like this <TOKEN_NAME> in a given string to velocity tags
	 * like this ${token.name}
	 * 
	 * @param text
	 * @param allowsVelocity
	 * @return
	 */
	private String convertPicsTagsToVelocity(String text, boolean allowsVelocity) {
		if (!allowsVelocity) {
			// Strip out the velocity tags
			System.out.println("SUBJECT BEFORE: " + text);
			text = text.replace("${", "_");
			text = text.replace("}", "_");
			text = text.replace('“', '"');
			text = text.replace('”', '"');
			text = text.replace("`", "'");
			System.out.println("SUBJECT AFTER: " + text);
		}
		for (Token tag : getPicsTags()) {
			// This token is valid for this type of email template
			// Convert anything like this <Name> into something like this
			// ${person.name}
			String find = "<" + tag.getTokenName() + ">";
			text = text.replace(find, tag.getVelocityCode());
		}
		return text;
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

	public void addAllTokens(Map<? extends String, ? extends Object> m) {
		tokens.putAll(m);
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
