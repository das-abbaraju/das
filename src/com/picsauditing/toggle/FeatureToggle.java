package com.picsauditing.toggle;

public interface FeatureToggle {
	String TOGGLE_BADGE = "Toggle.Badge_v2";
	String TOGGLE_DYNAMICREPORTS = "Toggle.DynamicReports_v2";
	String TOGGLE_V7MENUS = "Toggle.v7Menus";
	String TOGGLE_LCCOR = "Toggle.LcCor_v2";
	String TOGGLE_MIBEW_CHAT = "Toggle.MibewChat";
	String TOGGLE_MIBEW_ON_REGISTRATION = "Toggle.MibewOnRegistration";
	String TOGGLE_SWITCHUSERSERVER = "Toggle.SwitchUserServer_v2";
	String TOGGLE_AUDITREJECTION = "Toggle.AuditRejection_v2";
	String TOGGLE_BASFDYNAMICREPORTSPREVIEW = "Toggle.BASFDynamicReportsPreview";
	String TOGGLE_BPROC_SUBSCRIPTIONEMAIL = "Toggle.BackgroundProcesses.SubscriptionEmail";
	String TOGGLE_BPROC_EMAILQUEUE = "Toggle.BackgroundProcesses.EmailQueue";
	String TOGGLE_BPROC = "Toggle.BackgroundProcesses";
	String TOGGLE_SESSION_COOKIE = "Toggle.SessionCookie";
	String TOGGLE_CONTRACTOR_CAMPAIGN = "Toggle.ContractorCampaign";
	String TOGGLE_PERMISSION_GROUPS = "Toggle.PermissionGroups";
	String TOGGLE_INVOICE_COMMISSION = "Toggle.InvoiceCommission";
	String TOGGLE_REQUESTNEWCONTRACTORACCOUNT = "Toggle.RequestNewContractorAccount";
	String TOGGLE_TIMEZONE_LOOKUP = "Toggle.TimezoneLookup";
	String TOGGLE_CSR_SINGLE_ASSIGNMENT = "Toggle.BackgroundProcesses.Csr_SingleAssignment";

	boolean isFeatureEnabled(String toggleName);

	void addToggleVariable(String name, Object value);
}
