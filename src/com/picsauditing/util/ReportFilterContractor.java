package com.picsauditing.util;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;

import java.util.*;

public class ReportFilterContractor extends ReportFilterAccount {

	private static final long serialVersionUID = 1L;

	// /////// Filter Visibility /////////////
	protected boolean showContractor = false;
	protected boolean showOperator = true;
	protected boolean showOperatorSingle = false;
	protected boolean showAnyCAOOperator = false;
	protected boolean showTrade = true;
	protected boolean showTaxID = true;
	protected boolean showFlagStatus = false;
	protected boolean showConAuditor = false;
	protected boolean isNonContactUser = false;
	protected boolean showPolicyChangedDate = false;
	protected boolean showRiskLevel = true;
	protected boolean showProductRiskLevel = true;
	protected boolean showService = true;
	protected boolean showAssignedCon = false;
	protected boolean showInParentCorporation = false;
	protected boolean showWaitingOn = false;
	protected boolean showEmailTemplate = false;
	protected boolean showBillingCountrySubdivision = false;
	protected boolean showRegistrationDate = true;
	protected boolean showConWithPendingAudits = false;
	protected boolean showOpertorTagName = false;
	protected boolean showCcOnFile = false;
	protected boolean showInvoiceDueDate = false;
	protected boolean showDeactivationReason = false;
	protected boolean showMinorityOwned = false;
	protected boolean showWorkStatus = false;
	protected boolean showInsuranceLimits = false;
	protected boolean showOQ = false;
	protected boolean showHSE = false;
	protected boolean showAccountManager = false;
	protected boolean showCaoChangesFlagChanges = false;
	protected boolean showAuditStatusFlagChanges = false;
	protected boolean showAuditCreationFlagChanges = false;
	protected boolean showAuditQuestionFlagChanges = false;
	protected boolean showSoleProprietership = true;
	protected boolean showAccountLevel = false;
	protected boolean showLocation = false;
	protected boolean showFlagOverrideHistory = false;
	protected boolean showRequiredTags = false;
	protected boolean showGeneralContractors = false;
	protected boolean showInsideSales = false;

	// /////// Parameter Values /////////////////
	protected String performedBy;
	protected int[] trade;
	protected int[] contractor;
	protected int[] operator;
	protected int operatorSingle;
	protected String taxID;
	protected String[] flagStatus;
	protected int[] conAuditorId;
	protected Date policyChangedDate1;
	protected Date policyChangedDate2;
	protected int[] riskLevel;
	protected int[] productRiskLevel;
	protected String[] service;
	protected boolean onlySelectedServices = false;
	protected int[] accountManager;
	protected boolean assignedCon = false;
	protected boolean inParentCorporation = false;
	protected String waitingOn;
	protected Date emailSentDate;
	protected int emailTemplate;
	protected ListType emailListType;
	protected String billingCountrySubdivision = "All";
	protected Date registrationDate1;
	protected Date registrationDate2;
	protected boolean pendingPqfAnnualUpdate = false;
	protected int operatorTagName;
	protected int ccOnFile = 2;
	protected Date invoiceDueDate1;
	protected Date invoiceDueDate2;
	protected String deactivationReason;
	protected int[] minorityQuestion;
	protected ApprovalStatus workStatus;
	protected String glEachOccurrence;
	protected String glGeneralAggregate;
	protected String alCombinedSingle;
	protected String wcEachAccident;
	protected String exEachOccurrence;
	protected boolean oq = false;
	protected boolean hse = false;
	protected boolean caoChangesFlagChanges = false;
	protected boolean auditStatusFlagChanges = false;
	protected boolean auditCreationFlagChanges = false;
	protected boolean auditQuestionFlagChanges = false;
	protected boolean soleProprietership = false;
	protected String[] accountLevel;
	protected String riskType;
	protected boolean flagOverrideHistory = false;
	protected boolean requiredTags = false;
	protected int[] generalContractor;
	protected int[] insideSalesID;

    protected String generalContractorAlias = "gc";

	// // setting the filter
	public boolean isShowContractor() {
		return showContractor;
	}

	public void setShowContractor(boolean showContractor) {
		this.showContractor = showContractor;
	}

	public boolean isShowOperator() {
		return showOperator;
	}

	public void setShowOperator(boolean showOperator) {
		this.showOperator = showOperator;
	}

	public boolean isShowOperatorSingle() {
		return showOperatorSingle;
	}

	public void setShowOperatorSingle(boolean showOperatorSingle) {
		this.showOperatorSingle = showOperatorSingle;
	}

	public boolean isShowAnyCAOOperator() {
		return showAnyCAOOperator;
	}

	public void setShowAnyCAOOperator(boolean showAnyCAOOperator) {
		this.showAnyCAOOperator = showAnyCAOOperator;
	}

	public boolean isShowTrade() {
		return showTrade;
	}

	public void setShowTrade(boolean showTrade) {
		this.showTrade = showTrade;
	}

	public boolean isShowTaxID() {
		return showTaxID;
	}

	public void setShowTaxID(boolean showTaxID) {
		this.showTaxID = showTaxID;
	}

	public boolean isShowFlagStatus() {
		return showFlagStatus;
	}

	public void setShowFlagStatus(boolean showFlagStatus) {
		this.showFlagStatus = showFlagStatus;
	}

	public boolean isShowConAuditor() {
		return showConAuditor;
	}

	public void setShowConAuditor(boolean showConAuditor) {
		this.showConAuditor = showConAuditor;
	}

	public boolean isNonContactUser() {
		return isNonContactUser;
	}

	public void setNonContactUser(boolean isNonContactUser) {
		this.isNonContactUser = isNonContactUser;
	}

	public boolean isShowPolicyChangedDate() {
		return showPolicyChangedDate;
	}

	public void setShowPolicyChangedDate(boolean showPolicyChangedDate) {
		this.showPolicyChangedDate = showPolicyChangedDate;
	}

	public boolean isShowRiskLevel() {
		return showRiskLevel;
	}

	public void setShowRiskLevel(boolean showRiskLevel) {
		this.showRiskLevel = showRiskLevel;
	}

	public boolean isShowProductRiskLevel() {
		return showProductRiskLevel;
	}

	public void setShowProductRiskLevel(boolean showProductRiskLevel) {
		this.showProductRiskLevel = showProductRiskLevel;
	}

	public boolean isShowService() {
		return showService;
	}

	public void setShowService(boolean showService) {
		this.showService = showService;
	}

	public boolean isShowInParentCorporation() {
		return showInParentCorporation;
	}

	public void setShowInParentCorporation(boolean showInParentCorporation) {
		this.showInParentCorporation = showInParentCorporation;
	}

	public boolean isShowWaitingOn() {
		return showWaitingOn;
	}

	public void setShowWaitingOn(boolean showWaitingOn) {
		this.showWaitingOn = showWaitingOn;
	}

	public boolean isShowEmailTemplate() {
		return showEmailTemplate;
	}

	public void setShowEmailTemplate(boolean showEmailTemplate) {
		this.showEmailTemplate = showEmailTemplate;
	}

	public boolean isShowBillingCountrySubdivision() {
		return showBillingCountrySubdivision;
	}

	public void setShowBillingCountrySubdivision(boolean showBillingCountrySubdivision) {
		this.showBillingCountrySubdivision = showBillingCountrySubdivision;
	}

	public boolean isShowRegistrationDate() {
		return showRegistrationDate;
	}

	public void setShowRegistrationDate(boolean showRegistrationDate) {
		this.showRegistrationDate = showRegistrationDate;
	}

	public boolean isShowConWithPendingAudits() {
		return showConWithPendingAudits;
	}

	public void setShowConWithPendingAudits(boolean showConWithPendingAudits) {
		this.showConWithPendingAudits = showConWithPendingAudits;
	}

	public boolean isPendingPqfAnnualUpdate() {
		return pendingPqfAnnualUpdate;
	}

	public void setPendingPqfAnnualUpdate(boolean pendingPqfAnnualUpdate) {
		this.pendingPqfAnnualUpdate = pendingPqfAnnualUpdate;
	}

	public boolean isShowOpertorTagName() {
		return showOpertorTagName;
	}

	public void setShowOpertorTagName(boolean showOpertorTagName) {
		this.showOpertorTagName = showOpertorTagName;
	}

	public boolean isShowCcOnFile() {
		return showCcOnFile;
	}

	public void setShowCcOnFile(boolean showCcOnFile) {
		this.showCcOnFile = showCcOnFile;
	}

	public boolean isShowInvoiceDueDate() {
		return showInvoiceDueDate;
	}

	public void setShowInvoiceDueDate(boolean showInvoiceDueDate) {
		this.showInvoiceDueDate = showInvoiceDueDate;
	}

	public boolean isShowDeactivationReason() {
		return showDeactivationReason;
	}

	public void setShowDeactivationReason(boolean showDeactivationReason) {
		this.showDeactivationReason = showDeactivationReason;
	}

	public boolean isShowMinorityOwned() {
		return showMinorityOwned;
	}

	public void setShowMinorityOwned(boolean showMinorityOwned) {
		this.showMinorityOwned = showMinorityOwned;
	}

	public boolean isShowWorkStatus() {
		return showWorkStatus;
	}

	public void setShowWorkStatus(boolean showWorkStatus) {
		this.showWorkStatus = showWorkStatus;
	}

	public boolean isShowOQ() {
		return showOQ;
	}

	public void setShowOQ(boolean showOQ) {
		this.showOQ = showOQ;
	}

	public boolean isShowHSE() {
		return showHSE;
	}

	public void setShowHSE(boolean showHSE) {
		this.showHSE = showHSE;
	}

	public boolean isShowAccountManager() {
		return showAccountManager;
	}

	public void setShowAccountManager(boolean showAccountManager) {
		this.showAccountManager = showAccountManager;
	}

	public boolean isShowCaoChangesFlagChanges() {
		return showCaoChangesFlagChanges;
	}

	public void setShowCaoChangesFlagChanges(boolean showCaoChangesFlagChanges) {
		this.showCaoChangesFlagChanges = showCaoChangesFlagChanges;
	}

	public boolean isShowAuditStatusFlagChanges() {
		return showAuditStatusFlagChanges;
	}

	public void setShowAuditStatusFlagChanges(boolean showAuditStatusFlagChanges) {
		this.showAuditStatusFlagChanges = showAuditStatusFlagChanges;
	}

	public boolean isShowAuditCreationFlagChanges() {
		return showAuditCreationFlagChanges;
	}

	public void setShowAuditCreationFlagChanges(boolean showAuditCreationFlagChanges) {
		this.showAuditCreationFlagChanges = showAuditCreationFlagChanges;
	}

	public boolean isShowAuditQuestionFlagChanges() {
		return showAuditQuestionFlagChanges;
	}

	public void setShowAuditQuestionFlagChanges(boolean showAuditQuestionFlagChanges) {
		this.showAuditQuestionFlagChanges = showAuditQuestionFlagChanges;
	}

	public boolean isShowSoleProprietership() {
		return showSoleProprietership;
	}

	public void setShowSoleProprietership(boolean showSoleProprietership) {
		this.showSoleProprietership = showSoleProprietership;
	}

	public boolean isShowAccountLevel() {
		return showAccountLevel;
	}

	public void setShowAccountLevel(boolean showAccountLevel) {
		this.showAccountLevel = showAccountLevel;
	}

	public boolean isShowGeneralContractors() {
		return showGeneralContractors;
	}

	public void setShowGeneralContractors(boolean showGeneralContractors) {
		this.showGeneralContractors = showGeneralContractors;
	}

	public boolean isShowInsideSales() {
		return showInsideSales;
	}

	public void setShowInsideSales(boolean showInsideSales) {
		this.showInsideSales = showInsideSales;
	}

	// Filter data
	public int[] getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(int[] accountManager) {
		this.accountManager = accountManager;
	}

	public String getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(String performedBy) {
		this.performedBy = performedBy;
	}

	public int[] getTrade() {
		return trade;
	}

	public void setTrade(int[] trade) {
		this.trade = trade;
	}

	public int[] getContractor() {
		return contractor;
	}

	public void setContractor(int[] contractor) {
		this.contractor = contractor;
	}

	public int[] getOperator() {
		return operator;
	}

	public void setOperator(int[] operator) {
		this.operator = operator;
	}

	public int getOperatorSingle() {
		return operatorSingle;
	}

	public void setOperatorSingle(int operatorSingle) {
		this.operatorSingle = operatorSingle;
	}

	public String getTaxID() {
		if (Strings.isEmpty(taxID))
			taxID = getDefaultTaxID();

		return taxID;
	}

	public void setTaxID(String taxID) {
		this.taxID = taxID;
	}

	public String[] getFlagStatus() {
		return flagStatus;
	}

	public void setFlagStatus(String[] flagStatus) {
		this.flagStatus = flagStatus;
	}

	public int[] getConAuditorId() {
		return conAuditorId;
	}

	public void setConAuditorId(int[] conAuditorId) {
		this.conAuditorId = conAuditorId;
	}

	public Date getPolicyChangedDate1() {
		return policyChangedDate1;
	}

	public void setPolicyChangedDate1(Date policyChangedDate1) {
		this.policyChangedDate1 = policyChangedDate1;
	}

	public Date getPolicyChangedDate2() {
		return policyChangedDate2;
	}

	public void setPolicyChangedDate2(Date policyChangedDate2) {
		this.policyChangedDate2 = policyChangedDate2;
	}

	public int[] getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int[] riskLevel) {
		this.riskLevel = riskLevel;
	}

	public int[] getProductRiskLevel() {
		return productRiskLevel;
	}

	public void setProductRiskLevel(int[] productRiskLevel) {
		this.productRiskLevel = productRiskLevel;
	}

	public String[] getService() {
		return service;
	}

	public void setService(String[] service) {
		this.service = service;
	}

	public boolean isOnlySelectedServices() {
		return onlySelectedServices;
	}

	public void setOnlySelectedServices(boolean onlySelectedServices) {
		this.onlySelectedServices = onlySelectedServices;
	}

	public void setPermissions(Permissions permissions) {
		super.setPermissions(permissions);
		setShowConWithPendingAudits(true);

		if (permissions.isOperatorCorporate())
			setShowFlagStatus(true);
		if (permissions.isOperator()) {
			setShowOperator(false);
			setShowWaitingOn(true);
			if (permissions.hasPermission(OpPerms.ViewUnApproved)) {
				setShowWorkStatus(true);
			}
		}
		if (permissions.hasPermission(OpPerms.ContractorDetails)) {
			setShowPrimaryInformation(true);
			setShowTradeInformation(true);
		}
		if (permissions.hasPermission(OpPerms.ContractorTags))
			setShowOpertorTagName(true);

		if (permissions.isPicsEmployee()) {
			setShowConAuditor(true);
		}
		if (permissions.hasPermission(OpPerms.Billing))
			setShowCcOnFile(true);
	}

	public String[] getTradePerformedByList() {
		String[] list = {getDefaultPerformedBy(), "Self Performed", "Sub Contracted"};
		return list;
	}

	public List<ContractorAccount> getContractorList() throws Exception {
		if (permissions == null)
			return null;
		ContractorAccountDAO dao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		return dao.findWhere("", permissions);
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "", permissions);
	}

	public List<OperatorAccount> getOperatorListWithCorporate() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(true, "", permissions);
	}

	public List<User> getAuditorList() throws Exception {
		UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
		return new AuditorCache(dao).getList();
	}

	public Map<Integer, WaitingOn> getWaitingOnList() throws Exception {
		return WaitingOn.getMap();
	}

	public List<EmailTemplate> getEmailTemplateList() throws Exception {
		EmailTemplateDAO dao = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
		return dao.findByAccountID(permissions.getAccountId(), getEmailListType());
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		OperatorTagDAO dao = (OperatorTagDAO) SpringUtils.getBean("OperatorTagDAO");
		return dao.findByOperator(permissions.getAccountId(), true);
	}

	public static Map<String, String> getDeactivationReasons() {
		I18nCache cache = I18nCache.getInstance();
		Locale locale = getLocaleStatic();

		Map<String, String> deactivationReasons = new HashMap<String, String>();
		deactivationReasons.put(AccountStatusChanges.CHARGEBACK_REASON,
				cache.getText("Filters.status.Deactivation.ChargeBack", locale));
		deactivationReasons.put(AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
				cache.getText("Filters.status.Deactivation.DidNotCompleteProcess", locale));
		deactivationReasons.put(AccountStatusChanges.DOES_NOT_WORK_FOR_OPERATOR_REASON,
				cache.getText("Filters.status.Deactivation.DoesNotWorkForOperator", locale));
		deactivationReasons.put(AccountStatusChanges.DUPLICATE_MERGED_ACCOUNT_REASON,
				cache.getText("Filters.status.Deactivation.DuplicateMerged", locale));
		deactivationReasons.put(AccountStatusChanges.OPERATOR_EXEMPTION_REASON,
				cache.getText("Filters.status.Deactivation.OperatorExemption", locale));
		deactivationReasons.put(AccountStatusChanges.PAYMENTS_NOT_CURRENT_REASON,
				cache.getText("Filters.status.Deactivation.PaymentsNotCurrent", locale));
		deactivationReasons.put(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON,
				cache.getText("Filters.status.Deactivation.BidOnlyAccount", locale));
		return Collections.unmodifiableMap(deactivationReasons);
	}

	public boolean isShowAssignedCon() {
		return showAssignedCon;
	}

	public void setShowAssignedCon(boolean showAssignedCon) {
		this.showAssignedCon = showAssignedCon;
	}

	public boolean isAssignedCon() {
		return assignedCon;
	}

	public void setAssignedCon(boolean assignedCon) {
		this.assignedCon = assignedCon;
	}

	public boolean isInParentCorporation() {
		return inParentCorporation;
	}

	public void setInParentCorporation(boolean inParentCorporation) {
		this.inParentCorporation = inParentCorporation;
	}

	public String getWaitingOn() {
		return waitingOn;
	}

	public void setWaitingOn(String waitingOn) {
		this.waitingOn = waitingOn;
	}

	public Date getEmailSentDate() {
		return emailSentDate;
	}

	public void setEmailSentDate(Date emailSentDate) {
		this.emailSentDate = emailSentDate;
	}

	public int getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(int emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public ListType getEmailListType() {
		return emailListType;
	}

	public void setEmailListType(ListType emailListType) {
		this.emailListType = emailListType;
	}

	public String getBillingCountrySubdivision() {
		return billingCountrySubdivision;
	}

	public void setBillingCountrySubdivision(String billingCountrySubdivision) {
		this.billingCountrySubdivision = billingCountrySubdivision;
	}

	public Date getRegistrationDate1() {
		return registrationDate1;
	}

	public void setRegistrationDate1(Date registrationDate1) {
		this.registrationDate1 = registrationDate1;
	}

	public Date getRegistrationDate2() {
		return registrationDate2;
	}

	public void setRegistrationDate2(Date registrationDate2) {
		this.registrationDate2 = registrationDate2;
	}

	public int getOperatorTagName() {
		return operatorTagName;
	}

	public void setOperatorTagName(int operatorTagName) {
		this.operatorTagName = operatorTagName;
	}

	public int getCcOnFile() {
		return ccOnFile;
	}

	public void setCcOnFile(int ccOnFile) {
		this.ccOnFile = ccOnFile;
	}

	public Date getInvoiceDueDate1() {
		return invoiceDueDate1;
	}

	public void setInvoiceDueDate1(Date invoiceDueDate1) {
		this.invoiceDueDate1 = invoiceDueDate1;
	}

	public Date getInvoiceDueDate2() {
		return invoiceDueDate2;
	}

	public void setInvoiceDueDate2(Date invoiceDueDate2) {
		this.invoiceDueDate2 = invoiceDueDate2;
	}

	public String getDeactivationReason() {
		return deactivationReason;
	}

	public void setDeactivationReason(String deactivationReason) {
		this.deactivationReason = deactivationReason;
	}

	public int[] getMinorityQuestion() {
		return minorityQuestion;
	}

	public void setMinorityQuestion(int[] minorityQuestion) {
		this.minorityQuestion = minorityQuestion;
	}

	public ApprovalStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(ApprovalStatus workStatus) {
		this.workStatus = workStatus;
	}

	// Insurance Limit Filters

	public boolean isShowFlagOverrideHistory() {
		return showFlagOverrideHistory;
	}

	public void setShowFlagOverrideHistory(boolean showFlagOverrideHistory) {
		this.showFlagOverrideHistory = showFlagOverrideHistory;
	}

	public boolean isFlagOverrideHistory() {
		return flagOverrideHistory;
	}

	public void setFlagOverrideHistory(boolean flagOverrideHistory) {
		this.flagOverrideHistory = flagOverrideHistory;
	}

	public boolean isShowInsuranceLimits() {
		return showInsuranceLimits;
	}

	public void setShowInsuranceLimits(boolean showInsuranceLimits) {
		this.showInsuranceLimits = showInsuranceLimits;
	}

	public String getGlEachOccurrence() {
		if (Strings.isEmpty(glEachOccurrence))
			glEachOccurrence = getDefaultAmount();

		return glEachOccurrence;
	}

	public void setGlEachOccurrence(String glEachOccurence) {
		this.glEachOccurrence = glEachOccurence.replaceAll("[^0-9]", "");
	}

	public String getGlGeneralAggregate() {
		if (Strings.isEmpty(glGeneralAggregate))
			glGeneralAggregate = getDefaultAmount();

		return glGeneralAggregate;
	}

	public void setGlGeneralAggregate(String glGeneralAggregate) {
		this.glGeneralAggregate = glGeneralAggregate.replaceAll("[^0-9]", "");
	}

	public String getAlCombinedSingle() {
		if (Strings.isEmpty(alCombinedSingle))
			alCombinedSingle = getDefaultAmount();

		return alCombinedSingle;
	}

	public void setAlCombinedSingle(String alCombinedSingle) {
		this.alCombinedSingle = alCombinedSingle.replaceAll("[^0-9]", "");
	}

	public String getWcEachAccident() {
		if (Strings.isEmpty(wcEachAccident))
			wcEachAccident = getDefaultAmount();

		return wcEachAccident;
	}

	public void setWcEachAccident(String wcEachAccident) {
		this.wcEachAccident = wcEachAccident.replaceAll("[^0-9]", "");
	}

	public String getExEachOccurrence() {
		if (Strings.isEmpty(exEachOccurrence))
			exEachOccurrence = getDefaultAmount();

		return exEachOccurrence;
	}

	public void setExEachOccurrence(String exEachOccurence) {
		this.exEachOccurrence = exEachOccurence.replaceAll("[^0-9]", "");
	}

	public boolean isOq() {
		return oq;
	}

	public void setOq(boolean oq) {
		this.oq = oq;
	}

	public boolean isHse() {
		return hse;
	}

	public void setHse(boolean hse) {
		this.hse = hse;
	}

	public boolean isCaoChangesFlagChanges() {
		return caoChangesFlagChanges;
	}

	public void setCaoChangesFlagChanges(boolean caoChangesFlagChanges) {
		this.caoChangesFlagChanges = caoChangesFlagChanges;
	}

	public boolean isAuditStatusFlagChanges() {
		return auditStatusFlagChanges;
	}

	public void setAuditStatusFlagChanges(boolean auditStatusFlagChanges) {
		this.auditStatusFlagChanges = auditStatusFlagChanges;
	}

	public boolean isAuditCreationFlagChanges() {
		return auditCreationFlagChanges;
	}

	public void setAuditCreationFlagChanges(boolean auditCreationFlagChanges) {
		this.auditCreationFlagChanges = auditCreationFlagChanges;
	}

	public boolean isAuditQuestionFlagChanges() {
		return auditQuestionFlagChanges;
	}

	public void setAuditQuestionFlagChanges(boolean auditQuestionFlagChanges) {
		this.auditQuestionFlagChanges = auditQuestionFlagChanges;
	}

	public boolean isSoleProprietership() {
		return soleProprietership;
	}

	public void setSoleProprietership(boolean soleProprietership) {
		this.soleProprietership = soleProprietership;
	}

	public Map<Integer, String> getMinorityQuestions() {
		Map<Integer, String> temp = new HashMap<Integer, String>();
		temp.put(2340, getText("Filters.status.SmallBusiness"));
		temp.put(2373, getText("Filters.status.WomenOwned"));
		temp.put(3543, getText("Filters.status.DisabledVeteranOwned"));
		temp.put(66, getText("Filters.status.UnionPersonnel"));
		temp.put(77, getText("Filters.status.NonUnionPersonnel"));
		temp.put(9672, getText("Filters.status.AboriginalOwned"));
		temp.put(9675, getText("Filters.status.AboriginalEmployees"));
		return Collections.unmodifiableMap(temp);
	}

	public String[] getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(String[] accountLevel) {
		this.accountLevel = accountLevel;
	}

	public boolean isShowLocation() {
		return showLocation;
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
	}

	public static String getDefaultAmount() {
		return cache.getText("JS.Filters.label.EnterAmount", getLocaleStatic());
	}

	public static String getDefaultTaxID() {
		return cache.getText("Filters.label.TaxID", getLocaleStatic());
	}

	public static String getDefaultPerformedBy() {
		return cache.getText("Filters.label.NoPreference", getLocaleStatic());
	}

	public static String getDefaultSelectPerformedBy() {
		return cache.getText("Filters.label.SelfPerformed", getLocaleStatic());
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public boolean isShowRequiredTags() {
		return showRequiredTags;
	}

	public void setShowRequiredTags(boolean showRequiredTags) {
		this.showRequiredTags = showRequiredTags;
	}

	public boolean isRequiredTags() {
		return requiredTags;
	}

	public void setRequiredTags(boolean requiredTags) {
		this.requiredTags = requiredTags;
	}

	public int[] getGeneralContractor() {
		return generalContractor;
	}

	public void setGeneralContractor(int[] generalContractor) {
		this.generalContractor = generalContractor;
	}

	public int[] getInsideSalesID() {
		return insideSalesID;
	}

	public void setInsideSalesID(int[] insideSalesID) {
		this.insideSalesID = insideSalesID;
	}

    public String getGeneralContractorAlias() {
        return generalContractorAlias;
    }

    public void setGeneralContractorAlias(String generalContractorAlias) {
        this.generalContractorAlias = generalContractorAlias;
    }
}