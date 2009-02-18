package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class ReportFilterContractor extends ReportFilterAccount {
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_RISK = "- Risk Level -";
	public static final String DEFAULT_PERFORMED_BY = "- Performed By -";

	// /////// Filter Visibility /////////////
	protected boolean showOperator = true;
	protected boolean showOperatorSingle = false;
	protected boolean showTrade = true;
	protected boolean showLicensedIn = true;
	protected boolean showWorksIn = true;
	protected boolean showOfficeIn = true;
	protected boolean showTaxID = true;
	protected boolean showFlagStatus = false;
	protected boolean showConAuditor = false;
	protected boolean showRiskLevel = false;
	protected boolean showAssignedCon = false;
	protected boolean showInParentCorporation = false;
	protected boolean showWaitingOn = false;
	protected boolean showEmailTemplate = false;
	protected boolean showBillingState = false;

	// /////// Parameter Values /////////////////
	protected String performedBy;
	protected int[] trade;
	protected int[] operator;
	protected int[] stateLicensedIn;
	protected int[] worksIn;
	protected String taxID = DEFAULT_TAX_ID;
	protected String flagStatus;
	protected int[] officeIn;
	protected int[] conAuditorId;
	protected int riskLevel;
	protected boolean assignedCon = false;
	protected boolean inParentCorporation = false;
	protected String waitingOn;
	protected Date emailSentDate;
	protected int emailTemplate;
	protected ListType emailListType;
	protected String billingState = "All";

	//private AuditQuestionDAO aQuestionDAO = (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");

	// // setting the filter
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

	public String getFlagStatus() {
		return flagStatus;
	}

	public void setFlagStatus(String flagStatus) {
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

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public void setPermissions(Permissions permissions) {
		super.setPermissions(permissions);

		if (permissions.isOperator()) {
			setShowOperator(false);
			setShowFlagStatus(true);
			setShowWaitingOn(true);
			setShowRiskLevel(true);
		}
		if (permissions.isCorporate()) {
		}
		if (permissions.isPicsEmployee()) {
			setShowConAuditor(true);
			setShowRiskLevel(true);
		}
	}

	// ///
	private AuditQuestionDAO getQuestionDAO() {
		return (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");
	}

	public List<AuditQuestion> getStateLicensesList() throws Exception {
		return getQuestionDAO().findQuestionByType("License");
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
		String[] list = { "- Performed By -", "Self Performed", "Sub Contracted" };
		return list;
	}

	public ArrayList<String> getFlagStatusList() throws Exception {
		return FlagColor.getValuesWithDefault();
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "active='Y'", permissions);
	}

	public List<OperatorAccount> getOperatorListWithCorporate() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(true, "active='Y'", permissions);
	}

	public List<User> getAuditorList() throws Exception {
		UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
		return new AuditorCache(dao).getList();
	}

	public Map<String, String> getStateList() {
		return State.getStates(true);
	}

	public Map<Integer, WaitingOn> getWaitingOnList() throws Exception {
		return WaitingOn.getMap();
	}

	public List<EmailTemplate> getEmailTemplateList() throws Exception {
		EmailTemplateDAO dao = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
		return dao.findByAccountID(permissions.getAccountId(), getEmailListType());
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
	
}
