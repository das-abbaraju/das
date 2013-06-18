package com.picsauditing.jpa.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.model.i18n.LlewellynTranslatableString;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_template")
public class EmailTemplate extends BaseTableRequiringLanguages implements java.io.Serializable {

	public static final int PASSWORD_RESET = 85;
	public static final int USERNAME_REMINDER = 86;
	public static final int POSSIBLE_DUPLICATE_EMAIL_TEMPLATE = 234;
	public static final int FINAL_TO_OPERATORS_EMAIL_TEMPLATE = 240;
	public static final int REGISTRATION_REQUEST_FINAL_EMAIL_TEMPLATE = 241;
	public static final int regReqLastChanceEmailTemplate = 242;
	public static final int regReqReminderEmailTemplate = 243;
	public static final int pendingFinalEmailTemplate = 244;
	public static final int pendingLastChanceEmailTemplate = 245;
	public static final int pendingReminderEmailTemplate = 246;

	private int accountID;
	private String templateName = "";
	private String subject;
	private String body;

	// private TranslatableString translatedSubject;
	// private TranslatableString translatedBody;
	private String translatedSubject;
	private String translatedBody;

	private String recipient;
	private ListType listType;
	private boolean allowsVelocity = false;
	private boolean html = false;
	private boolean translated = false;

	private static final Set<Integer> PRIVATE_VALID_DEACTIVATED_EMAILS = Collections
			.unmodifiableSet(new HashSet<Integer>(Arrays.asList(48, 51, 71, 85, 86)));

	@Column(nullable = false)
	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	@Column(length = 150)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Transient
	public String getTranslatedSubject() {
		return new LlewellynTranslatableString(getI18nKey("translatedSubject")).toTranslatedString();
	}

	public void setTranslatedSubject(String translatedSubject) {
		this.translatedSubject = translatedSubject;
	}

	@Transient
	public String getTranslatedBody() {
		return new LlewellynTranslatableString("translatedBody").toTranslatedString();
	}

	public void setTranslatedBody(String translatedBody) {
		this.translatedBody = translatedBody;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getTemplateName() {
		return templateName;
	}

	@Column(length = 50, nullable = false)
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Enumerated(EnumType.STRING)
	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

	public boolean isAllowsVelocity() {
		return allowsVelocity;
	}

	public void setAllowsVelocity(boolean allowsVelocity) {
		this.allowsVelocity = allowsVelocity;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public boolean isTranslated() {
		return translated;
	}

	public void setTranslated(boolean translated) {
		this.translated = translated;
	}

	@Transient
	public static final Set<Integer> VALID_DEACTIVATED_EMAILS() {
		return PRIVATE_VALID_DEACTIVATED_EMAILS;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + id;
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	public void cascadeRequiredLanguages(List<String> add, List<String> remove) {
		return;
	}

	public boolean hasMissingChildRequiredLanguages() {
		return getLanguages().isEmpty();
	}
}