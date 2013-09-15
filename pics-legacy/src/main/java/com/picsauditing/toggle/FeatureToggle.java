package com.picsauditing.toggle;

public interface FeatureToggle {
	String TOGGLE_BADGE = "Toggle.Badge_v2";
    String TOGGLE_V7CHARTS = "Toggle.ShowGoogleCharts";
	String TOGGLE_V7MENUS = "Toggle.v7Menus";
	String TOGGLE_LCCOR = "Toggle.LcCor_v2";
	String TOGGLE_SWITCHUSERSERVER = "Toggle.SwitchUserServer_v2";
	String TOGGLE_AUDITREJECTION = "Toggle.AuditRejection_v2";
	String TOGGLE_BASFDYNAMICREPORTSPREVIEW = "Toggle.BASFDynamicReportsPreview";
	String TOGGLE_BPROC_SUBSCRIPTIONEMAIL = "Toggle.BackgroundProcesses.SubscriptionEmail";
	String TOGGLE_BPROC_EMAILQUEUE = "Toggle.BackgroundProcesses.EmailQueue";
	String TOGGLE_BPROC = "Toggle.BackgroundProcesses";
	String TOGGLE_CONTRACTOR_CAMPAIGN = "Toggle.ContractorCampaign";
	String TOGGLE_INVOICE_COMMISSION = "Toggle.InvoiceCommission";
	String TOGGLE_INVOICE_COMMISSION_PHASE2 = "Toggle.InvoiceCommission.Phase2";
	String TOGGLE_REQUESTNEWCONTRACTORACCOUNT = "Toggle.RequestNewContractorAccount";
	String TOGGLE_TIMEZONE_LOOKUP = "Toggle.TimezoneLookup";
	String TOGGLE_CSR_SINGLE_ASSIGNMENT = "Toggle.BackgroundProcesses.Csr_SingleAssignment";
	String TOGGLE_CANONICAL_TIMEZONES = "Toggle.Canonical_Timezone";
	String TOGGLE_DR_STORAGE_BACKWARDS_COMPATIBILITY = "Toggle.DR_Persistence";
	String TOGGLE_READ_ONLY_DATASOURCE = "Toggle.Read_Only_DataSource";
	String TOGGLE_DO_NOT_USE_SWITCHTO_ACCOUNTS_IN_NOTE_RESTRICTION = "Toggle.NoteEdit.DoNotUseSwitchToInRestriction";
	String TOGGLE_SHOW_REVIEW_DOC_IN_AUDITGUARD = "Toggle.ShowReviewDocInAuditGUARD";
	String TOGGLE_USE_V7_MENU_COLUMN = "Toggle.UseVersion7MenuColumns";
	String TOGGLE_INVOICE_LANGUAGES_TO_EMAIL_VIA_BPROCS = "Toggle.InvoiceLanguagesToEmailViaBPROCS";
	String TOGGLE_RULES_BASED_INSURANCE_CRITERIA = "Toggle.RBIC";
	String TOGGLE_NEW_JS_TRANSLATIONS = "Toggle.NewJsTranslations";
	String TOGGLE_REGISTRATION_AUTOFILL = "Toggle.RegistrationAutoFill";
	String TOGGLE_SUPPRESS_WELCOME_EMAILS = "Toggle.SuppressWelcomeEmails";
	String TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER = "Toggle.TranslationServiceAdapter";
	String TOGGLE_COUNTRY_PHONE_NUMBER = "Toggle.CountryPhoneNumber";
	String TOGGLE_EMPTY_STRING_IS_VALID_TRANSLATION = "Toggle.EmptyStringIsValidTranslation";
	String TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE = "Toggle.UseNewTranslationsDataSource";
    String TOGGLE_USE_PERIODIC_AUDIT = "Toggle.PeriodicAudit";

	boolean isFeatureEnabled(String toggleName);

	void addToggleVariable(String name, Object value);
}