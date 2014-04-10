package com.picsauditing.toggle;

import com.picsauditing.access.Permissions;

public interface FeatureToggle {

	String TOGGLE_BADGE = "Toggle.Badge_v2";
    String TOGGLE_V7CHARTS = "Toggle.ShowGoogleCharts";
	String TOGGLE_V7MENUS = "Toggle.v7Menus";
	String TOGGLE_LCCOR = "Toggle.LcCor_v2";
	String TOGGLE_SWITCHUSERSERVER = "Toggle.SwitchUserServer_v2";
	String TOGGLE_AUDITREJECTION = "Toggle.AuditRejection_v2";
	String TOGGLE_BASFDYNAMICREPORTSPREVIEW = "Toggle.BASFDynamicReportsPreview";
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
	String TOGGLE_DISABLE_LOG_TRANSLATION_USAGE = "Toggle.DisableTranslationServiceLogUsage";
	String TOGGLE_COUNTRY_PHONE_NUMBER = "Toggle.CountryPhoneNumber";
	String TOGGLE_EMPTY_STRING_IS_VALID_TRANSLATION = "Toggle.EmptyStringIsValidTranslation";
	String TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE = "Toggle.UseNewTranslationsDataSource";
    String TOGGLE_USE_PERIODIC_AUDIT = "Toggle.PeriodicAudit";
	String TOGGLE_SAFETY_SENSITIVE_ENABLED = "Toggle.SafetySensitive.Enabled";
    String SAP_BIZ_UNITS_ENABLED = "SAP.BusinessUnits.Enabled";
    String SAP_BIZ_UNITS_SET_SYNC_TRUE_ENABLED = "SAP.BusinessUnits.SetSyncTrue.Enabled";
    String TOGGLE_DISABLE_TRANSLATION_SERVICE_CACHE_WARMING = "Toggle.DisableTranslationServiceCacheWarming";
    String TOGGLE_STRIKE_IRON = "Toggle.StrikeIron";
    String TOGGLE_USE_MOCK_PAYMENT_SERVICE = "Toggle.UseMockPaymentService";
	String TOGGLE_TURN_OFF_UNNECESSARY_MENUS = "Toggle.TurnOffUnnecessaryMenus";

	boolean isFeatureEnabled(String toggleName);

	void addToggleVariable(String name, Object value);

    void setPermissions(Permissions permissions);
}
