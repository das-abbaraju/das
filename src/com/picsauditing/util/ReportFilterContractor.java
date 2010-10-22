package com.picsauditing.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class ReportFilterContractor extends ReportFilterAccount {
	public static final String DEFAULT_AMOUNT = "- Enter Amount -";
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_PERFORMED_BY = "- No Preference -";
	public static final String DEFAULT_SELECT_PERFORMED_BY = "Self Performed";
	public static final String[] DEACTIVATION_REASON = { "ChargeBack", "Did not Complete PICS process",
			"Does not work for operator", "Duplicate/Merged Account", "Operator Exemption", "Payments not Current",
			"Bid Only Account" };

	// /////// Filter Visibility /////////////
	protected boolean showOperator = true;
	protected boolean showOperatorSingle = false;
	protected boolean showIndustry = true;
	protected boolean showTrade = true;
	protected boolean showLicensedIn = true;
	protected boolean showWorksIn = true;
	protected boolean showOfficeIn = true;
	protected boolean showTaxID = true;
	protected boolean showFlagStatus = false;
	protected boolean showConAuditor = false;
	protected boolean showRiskLevel = true;
	protected boolean showAssignedCon = false;
	protected boolean showInParentCorporation = false;
	protected boolean showWaitingOn = false;
	protected boolean showEmailTemplate = false;
	protected boolean showBillingState = false;
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

	// /////// Parameter Values /////////////////
	protected String performedBy;
	protected int[] industry;
	protected int[] trade;
	protected int[] operator;
	protected int operatorSingle;
	protected int[] stateLicensedIn;
	protected int[] worksIn;
	protected String taxID = DEFAULT_TAX_ID;
	protected String[] flagStatus;
	protected int[] officeIn;
	protected int[] conAuditorId;
	protected int[] riskLevel;
	protected boolean assignedCon = false;
	protected boolean inParentCorporation = false;
	protected String waitingOn;
	protected Date emailSentDate;
	protected int emailTemplate;
	protected ListType emailListType;
	protected String billingState = "All";
	protected Date registrationDate1;
	protected Date registrationDate2;
	protected boolean pendingPqfAnnualUpdate = false;
	protected int operatorTagName;
	protected int ccOnFile = 2;
	protected Date invoiceDueDate1;
	protected Date invoiceDueDate2;
	protected String deactivationReason;
	protected int minorityQuestion = 0;
	protected String workStatus;
	protected String glEachOccurrence = DEFAULT_AMOUNT;
	protected String glGeneralAggregate = DEFAULT_AMOUNT;
	protected String alCombinedSingle = DEFAULT_AMOUNT;
	protected String wcEachAccident = DEFAULT_AMOUNT;
	protected String exEachOccurrence = DEFAULT_AMOUNT;
	protected boolean oq = false;
	protected boolean hse = false;

	// private AuditQuestionDAO aQuestionDAO = (AuditQuestionDAO)
	// SpringUtils.getBean("AuditQuestionDAO");

	// // setting the filter
	public boolean isShowIndustry() {
		return showIndustry;
	}
	
	public void setShowIndustry(boolean showIndustry) {
		this.showIndustry = showIndustry;
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

	public boolean isShowTrade() {
		return showTrade;
	}

	public void setShowTrade(boolean showTrade) {
		this.showTrade = showTrade;
	}

	public boolean isShowLicensedIn() {
		return showLicensedIn;
	}

	public void setShowLicensedIn(boolean showLicensedIn) {
		this.showLicensedIn = showLicensedIn;
	}

	public boolean isShowWorksIn() {
		return showWorksIn;
	}

	public void setShowWorksIn(boolean showWorksIn) {
		this.showWorksIn = showWorksIn;
	}

	public boolean isShowOfficeIn() {
		return showOfficeIn;
	}

	public void setShowOfficeIn(boolean showOfficeIn) {
		this.showOfficeIn = showOfficeIn;
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

	public boolean isShowRiskLevel() {
		return showRiskLevel;
	}

	public void setShowRiskLevel(boolean showRiskLevel) {
		this.showRiskLevel = showRiskLevel;
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

	public boolean isShowBillingState() {
		return showBillingState;
	}

	public void setShowBillingState(boolean showBillingState) {
		this.showBillingState = showBillingState;
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

	public int[] getStateLicensedIn() {
		return stateLicensedIn;
	}

	public void setStateLicensedIn(int[] stateLicensedIn) {
		this.stateLicensedIn = stateLicensedIn;
	}

	public int[] getWorksIn() {
		return worksIn;
	}

	public void setWorksIn(int[] worksIn) {
		this.worksIn = worksIn;
	}

	public String getTaxID() {
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

	public int[] getOfficeIn() {
		return officeIn;
	}

	public void setOfficeIn(int[] officeIn) {
		this.officeIn = officeIn;
	}

	public int[] getConAuditorId() {
		return conAuditorId;
	}

	public void setConAuditorId(int[] conAuditorId) {
		this.conAuditorId = conAuditorId;
	}

	public int[] getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int[] riskLevel) {
		this.riskLevel = riskLevel;
	}

	public void setPermissions(Permissions permissions) {
		super.setPermissions(permissions);

		if (permissions.isOperator()) {
			setShowOperator(false);
			setShowFlagStatus(true);
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

		if (permissions.isCorporate()) {
		}
		if (permissions.isPicsEmployee()) {
			setShowConAuditor(true);
			setShowConWithPendingAudits(true);
		}
		if (permissions.hasPermission(OpPerms.Billing))
			setShowCcOnFile(true);
	}

	// ///
	private AuditQuestionDAO getQuestionDAO() {
		return (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");
	}

	public List<AuditQuestion> getStateLicensesList() throws Exception {
		return getQuestionDAO().findQuestionByType("License");
	}
	
	public int[] getIndustry() {
		return industry;
	}
	
	public void setIndustry(int[] industry) {
		this.industry = industry;
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return getQuestionDAO().findQuestionByType("Service");
	}

	public List<AuditQuestion> getWorksInList() throws Exception {
		return getQuestionDAO().findQuestionByType("Office Location");
	}

	public List<AuditQuestion> getOfficeInList() throws Exception {
		return getQuestionDAO().findQuestionByType("Office Location");
	}

	public String[] getTradePerformedByList() {
		String[] list = { DEFAULT_PERFORMED_BY, "Self Performed", "Sub Contracted" };
		return list;
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

	public String[] getDeactivationReasons() {
		return ReportFilterContractor.DEACTIVATION_REASON;
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

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
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

	public int getMinorityQuestion() {
		return minorityQuestion;
	}

	public void setMinorityQuestion(int minorityQuestion) {
		this.minorityQuestion = minorityQuestion;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	// Insurance Limit Filters

	public boolean isShowInsuranceLimits() {
		return showInsuranceLimits;
	}

	public void setShowInsuranceLimits(boolean showInsuranceLimits) {
		this.showInsuranceLimits = showInsuranceLimits;
	}

	public String getGlEachOccurrence() {
		return glEachOccurrence;
	}

	public void setGlEachOccurrence(String glEachOccurence) {
		this.glEachOccurrence = glEachOccurence.replaceAll("[^0-9]", "");
	}

	public String getGlGeneralAggregate() {
		return glGeneralAggregate;
	}

	public void setGlGeneralAggregate(String glGeneralAggregate) {
		this.glGeneralAggregate = glGeneralAggregate.replaceAll("[^0-9]", "");
	}

	public String getAlCombinedSingle() {
		return alCombinedSingle;
	}

	public void setAlCombinedSingle(String alCombinedSingle) {
		this.alCombinedSingle = alCombinedSingle.replaceAll("[^0-9]", "");
	}

	public String getWcEachAccident() {
		return wcEachAccident;
	}

	public void setWcEachAccident(String wcEachAccident) {
		this.wcEachAccident = wcEachAccident.replaceAll("[^0-9]", "");
	}

	public String getExEachOccurrence() {
		return exEachOccurrence;
	}

	public void setExEachOccurrence(String exEachOccurence) {
		this.exEachOccurrence = exEachOccurence.replaceAll("[^0-9]", "");
	}

	public static String getDefaultSelectPerformedBy() {
		return DEFAULT_SELECT_PERFORMED_BY;
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
	
	// Getters for search lists
	public List<AuditQuestion> getIndustryList() {
		// Do we want to hard code this?
		return getQuestionDAO().findWhere("categoryID = 423");
	} 
}
