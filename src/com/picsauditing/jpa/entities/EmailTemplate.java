package com.picsauditing.jpa.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_template")
@AttributeOverride(name = "id", column = @Column(name = "templateID"))
public class EmailTemplate extends BaseTableRequiringLanguages implements java.io.Serializable {
	private int accountID;
	private String templateName = "";
	private String subject;
	private String body;
	private TranslatableString translatedSubject;
	private TranslatableString translatedBody;
	private String recipient;
	private ListType listType;
	private boolean allowsVelocity = false;
	private boolean html = false;
	private boolean translated = false;

	private static final Set<Integer> PRIVATE_VALID_DEACTIVATED_EMAILS = new HashSet<Integer>(Arrays.asList(48, 51, 71,
			85, 86));

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
	public TranslatableString getTranslatedSubject() {
		return translatedSubject;
	}

	public void setTranslatedSubject(TranslatableString translatedSubject) {
		this.translatedSubject = translatedSubject;
	}

	@Transient
	public TranslatableString getTranslatedBody() {
		return translatedBody;
	}

	public void setTranslatedBody(TranslatableString translatedBody) {
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
		return Collections.unmodifiableSet(PRIVATE_VALID_DEACTIVATED_EMAILS);
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