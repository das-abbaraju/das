package com.picsauditing.mail;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
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
 */
public class EmailBuilder {
	public static final String PERMISSIONS = "permissions";
	public static final String CONTRACTOR = "contractor";
	public static final String USER = "user";
	public static final String PRIMARY_CONTACT = "primaryContact";
	public static final String LOCALE = "locale";

	private String fromAddress = null;
	private String password = null;
	private EmailTemplate template;
	private List<Token> picsTags = null;
	private String toAddresses = null;
	private String ccAddresses = null;
	private String bccAddresses = null;
	private Map<String, Object> tokens;
	private int conID;

	private VelocityAdaptor velocityAdaptor;
	private TranslationActionSupport translationActionSupport;

	private Permissions permissions;

	private boolean debug = false;
	private boolean edited = false;

	public EmailBuilder() {
		velocityAdaptor = new VelocityAdaptor();
		clear();
	}

	public void clear() {
		tokens = new HashMap<String, Object>();
	}

	public void clearAll() {
		// tokens = new HashMap<String, Object>();

		fromAddress = null;
		password = null;
		template = null;
		picsTags = null;
		toAddresses = null;
		ccAddresses = null;
		bccAddresses = null;
		tokens = null;
		conID = 0;
	}

	public EmailQueue build() throws IOException {
		EmailQueue email = new EmailQueue();

		email.setEmailTemplate(template);
		email.setHtml(template.isHtml());
		email.setCreationDate(new Date());
		email.setFromAddress(fromAddress);
		email.setToAddresses(toAddresses);
		email.setCcAddresses(ccAddresses);
		email.setBccAddresses(bccAddresses);
		email.setFromPassword(password);
		if (conID > 0) {
			email.setContractorAccount(new ContractorAccount(conID));
		}

		if (permissions != null) {
			tokens.put(PERMISSIONS, permissions);
			if (permissions.getUserId() > 0) {
				email.setCreatedBy(new User());
				email.getCreatedBy().setId(permissions.getUserId());
			}
		}

		Locale locale = getUserLocale();
		String templateBody = template.getBody();
		String templateSubject = template.getSubject();

		// Include i18nCache for every email
		if (!tokens.containsKey("i18nCache")) {
			tokens.put("i18nCache", I18nCache.getInstance());
			tokens.put(LOCALE, locale);
		}

		// If we're using the default template, pull up the correct translation
		// for the user we're sending the email to.
		if (!edited && template.isTranslated()) {
			String translatedBodyForUser = null;
			String translatedSubjectForUser = null;

			// if (template.getTranslatedBody() != null) {
			// translatedBodyForUser = getUserTranslation(locale,
			// template.getTranslatedBody().getTranslations());
			// }
			//
			// if (template.getTranslatedSubject() != null) {
			// translatedSubjectForUser = getUserTranslation(locale,
			// template.getTranslatedSubject().getTranslations());
			// }

			if (template.getTranslatedBody() != null) {
				translatedBodyForUser = template.getTranslatedBody();
			}

			if (template.getTranslatedSubject() != null) {
				translatedSubjectForUser = template.getTranslatedSubject();
			}

			if (!I18nCache.DEFAULT_TRANSLATION.equals(translatedBodyForUser)) {
				templateBody = translatedBodyForUser;
			}
			if (!I18nCache.DEFAULT_TRANSLATION.equals(translatedSubjectForUser)) {
				templateSubject = translatedSubjectForUser;
			}
		}

		String subject = convertPicsTagsToVelocity(templateSubject, template.isAllowsVelocity());
		subject = velocityAdaptor.merge(subject, tokens);
		email.setSubject(subject);

		String body = convertPicsTagsToVelocity(templateBody, template.isAllowsVelocity());
		body = velocityAdaptor.merge(body, tokens);

		email.setBody(body);

		if (debug) {
			Logger logger = LoggerFactory.getLogger(EmailBuilder.class);
			logger.debug(email.getSubject());
			logger.debug(email.getBody());
		}
		return email;
	}

	private String getHost() {
		PicsActionSupport picsActionSupport = new PicsActionSupport();
		return picsActionSupport.getRequestHost();

	}

	// Custom token setters here
	// We may consider moving this to another class or back to the controllers
	public void setConAudit(ContractorAudit conAudit) throws EmailException {
		setContractor(conAudit.getContractorAccount(), OpPerms.ContractorSafety);
		addToken("audit", conAudit);
	}

	public void setContractor(ContractorAccount contractor, OpPerms role) throws EmailException {
		conID = contractor.getId();
		addToken(CONTRACTOR, contractor);

		Set<String> emails = new HashSet<String>();
		for (User user : contractor.getUsersByRole(role)) {
			if (user.isActiveB()) {
				if (Strings.isValidEmail(user.getEmail())) {
					emails.add(user.getEmail());
				}
			}
		}
		if (emails.size() > 0) {
			toAddresses = Strings.implode(emails, ", ");
		} else {
			if (role.equals(OpPerms.ContractorAdmin)) {
				// The contractor doesn't have any admin users
				// so this results in an endless loop
				throw new EmailException("Could not find a valid email address for " + contractor.getName());
			}
			setContractor(contractor, OpPerms.ContractorAdmin);
		}
	}

	public void setUser(User user) {
		addToken(USER, user);
		toAddresses = user.getEmail();
	}

	// End of custom token setters

	public String getSentTo() {
		if (Strings.isEmpty(ccAddresses)) {
			return toAddresses;
		}
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
			text = text.replace("${", "_");
			text = text.replace("}", "_");
			text = text.replace('ì', '"');
			text = text.replace('î', '"');
			text = text.replace("`", "'");
		}
		for (Token tag : getPicsTags()) {
			// This token is valid for this type of email template
			// Convert anything like this <Name> into something like this
			// ${person.name}
			String find = "<" + tag.getName().toString() + ">";
			text = text.replace(find, tag.getVelocityCode().toString());
		}
		return text;
	}

	private Locale getUserLocale() {
		Locale locale = null;

		if (tokensContainsValueFor(PERMISSIONS) && tokens.get(PERMISSIONS) instanceof Permissions) {
			Permissions permissions = (Permissions) tokens.get(PERMISSIONS);
			locale = permissions.getLocale();
		}

		if (tokensContainsValueFor(CONTRACTOR) && tokens.get(CONTRACTOR) instanceof ContractorAccount) {
			ContractorAccount contractor = (ContractorAccount) tokens.get(CONTRACTOR);
			locale = contractor.getLocale();
		}

		if (tokensContainsValueFor(USER) && tokens.get(USER) instanceof User) {
			User user = (User) tokens.get(USER);
			locale = user.getLocale();
		}

		if (tokensContainsValueFor(PRIMARY_CONTACT) && tokens.get(PRIMARY_CONTACT) instanceof User) {
			User user = (User) tokens.get(PRIMARY_CONTACT);
			locale = user.getLocale();
		}

		if (tokensContainsValueFor(LOCALE) && tokens.get(LOCALE) instanceof Locale) {
			locale = (Locale) tokens.get(LOCALE);
		}

		if (locale == null) {
			locale = Locale.ENGLISH;
		}

		return locale;
	}

	private boolean tokensContainsValueFor(String key) {
		return tokens.containsKey(key) && tokens.get(key) != null;
	}

	// private String getUserTranslation(Locale locale, Collection<Translation>
	// translations) {
	// String english = I18nCache.DEFAULT_TRANSLATION;
	// String notVariantTranslation = I18nCache.DEFAULT_TRANSLATION;
	// for (Translation translation : translations) {
	// if (I18nCache.DEFAULT_LANGUAGE.equals(translation.getLocale())) {
	// english = translation.getValue();
	// }
	//
	// if (locale.getLanguage().equals(translation.getLocale())
	// && !I18nCache.DEFAULT_TRANSLATION.equals(translation.getValue())) {
	// notVariantTranslation = translation.getValue();
	// }
	//
	// if (Strings.isNotEmpty(locale.getCountry())) {
	// if (translation.getLocale().equals(locale.getLanguage() + "_" +
	// locale.getCountry())) {
	// return translation.getValue();
	// }
	// }
	// }
	//
	// if (!notVariantTranslation.equals(I18nCache.DEFAULT_TRANSLATION)) {
	// return notVariantTranslation;
	// }
	//
	// return english;
	// }

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		addToken(PERMISSIONS, permissions);
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

	public void setFromAddress(User u) {
		this.fromAddress = "\"" + u.getName() + "\"<" + u.getEmail() + ">";
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

	/**
	 * Default value is false.<br />
	 * <br />
	 * A flag for whether templates were edited, not using the default template
	 * subject and body. If edited, we're not going to pull up any translations
	 * and send out the email as is. MassMailer and everywhere else we edit the
	 * body/subject for an email should set this flag to true.
	 * 
	 * @param edited
	 */
	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public void setFromAddressAsCSRFor(ContractorAccount contractor) {
		setFromAddress("\"" + contractor.getCurrentCsr().getName() + "\"<" + contractor.getCurrentCsr().getEmail()
				+ ">");
	}
}
