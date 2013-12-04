package com.picsauditing.jpa.entities;

import com.picsauditing.model.i18n.TranslatableString;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

import javax.persistence.Column;
import javax.persistence.*;
import java.util.*;

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
    public static final int FLAG_CHANGES_EMAIL_TEMPLATE = 55;
    public static final int NO_ACTION_EMAIL_TEMPLATE = 70;
    public static final int REMINDER_EMAIL_TEMPLATE = 12;
    public static final int OPEN_AUDITS_EMAIL_TEMPLATE = 6;
    public static final int IMPLEMENTATION_AUDIT_CONFIRMATION_EMAIL_TEMPLATE = 15;
    public static final int AUDIT_CONFIRMATION_EMAIL_TEMPLATE = 14;
    public static final int PQF_VERIFICATION_EMAIL_TEMPLATE = 11;
    public static final int TRIAL_ACCOUNT_APPROVAL_EMAIL_TEMPLATE = 73;
    public static final int DEACTIVATION_FOR_OPERATORS_EMAIL_TEMPLATE = 51;
    public static final int BRAIN_TREE_ERROR_EMAIL_TEMPLATE = 106;
    public static final int WELCOME_EMAIL_TEMPLATE = 2;
    public static final int SAFETY_SENSITIVE_DOWNGRADED_EMAIL_TEMPLATE = 367;
    public static final int RISK_LEVEL_DOWNGRADED_EMAIL_TEMPLATE = 159;
    public static final int CREDIT_CARD_EXPIRATION_EMAIL_TEMPLATE = 59;
    public static final int REQUEST_FOR_CLIENT_SITE_ADDITION_EMAIL_TEMPLATE = 321;
    public static final int NEW_USER_EMAIL_TEMPLATE = 5;
    public static final int PASSWORD_RESET_EMAIL_TEMPLATE = 85;
    public static final int INSURANCE_CERTIFICATE_EXPIRING_EMAIL_TEMPLATE = 10;
    public static final int CONTRACTOR_ADDED_EMAIL_TEMPLATE = 9;
    public static final int NOTICE_OF_FACILITY_REMOVAL = 47;
    public static final int CONTRACTOR_APPROVE_GC_RELATIONSHIP_EMAIL_TEMPLATE = 238;
    public static final int UPCOMING_IMPLEMENTATION_AUDIT_EMAIL_TEMPLATE = 247;

    private int accountID;
	private String templateName = "";
	private String subject;
	private String body;
	private String translatedSubject;
	private String translatedBody;
	private String recipient;
	private ListType listType;
	private boolean allowsVelocity = false;
	private boolean html = false;
	private boolean translated = false;

	private static final Set<Integer> PRIVATE_VALID_DEACTIVATED_EMAILS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(48, 51, 71, 85, 86)));

	@Column(nullable = false)
    @ReportField(type = FieldType.AccountID, importance = FieldImportance.Low)
	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	@Column(length = 150)
    @ReportField(importance = FieldImportance.Low)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

    @ReportField(importance = FieldImportance.Low)
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Transient
	public String getTranslatedSubject() {
		if (translatedSubject != null) {
			return translatedSubject;
		}

		return new TranslatableString(getI18nKey("translatedSubject")).toTranslatedString();
	}

	public void setTranslatedSubject(String translatedSubject) {
		this.translatedSubject = translatedSubject;
	}

    @Transient
    public String getTranslatedSubject(Locale locale) {
        return new TranslatableString(getI18nKey("translatedSubject")).toTranslatedString(locale);
    }

    @Transient
	public String getTranslatedBody() {
		if (translatedBody != null) {
			return translatedBody;
		}

		return new TranslatableString(getI18nKey("translatedBody")).toTranslatedString();
	}

	public void setTranslatedBody(String translatedBody) {
		this.translatedBody = translatedBody;
	}

    @Transient
    public String getTranslatedBody(Locale locale) {
        return new TranslatableString(getI18nKey("translatedBody")).toTranslatedString(locale);
    }

    @ReportField(importance = FieldImportance.Low)
    public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

    @ReportField()
	public String getTemplateName() {
		return templateName;
	}

	@Column(length = 50, nullable = false)
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Enumerated(EnumType.STRING)
    @ReportField(type = FieldType.ListType)
	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

    @ReportField(type = FieldType.Boolean)
	public boolean isAllowsVelocity() {
		return allowsVelocity;
	}

	public void setAllowsVelocity(boolean allowsVelocity) {
		this.allowsVelocity = allowsVelocity;
	}

    @ReportField(type = FieldType.Boolean)
	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

    @ReportField(type = FieldType.Boolean)
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