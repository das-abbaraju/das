package com.picsauditing.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;

public class ReportFilterContractor extends ReportFilterAccount {
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_RISK = "- Risk Level -";

	// /////// Filter Visibility /////////////
	protected boolean showOperator = true;
	protected boolean showOperatorSingle = false;
	protected boolean showTrade = true;
	protected boolean showCerts = false;
	protected boolean showLicensedIn = true;
	protected boolean showWorksIn = true;
	protected boolean showOfficeIn = true;
	protected boolean showTaxID = true;
	protected boolean showFlagStatus = false;
	protected boolean showConAuditor = false;
	protected boolean showRiskLevel = false;

	// /////// Parameter Values /////////////////
	protected String performedBy;
	protected int[] trade;
	protected int[] operator;
	protected String certsOnly;
	protected int[] stateLicensedIn;
	protected int[] worksIn;
	protected String taxID = DEFAULT_TAX_ID;
	protected String flagStatus;
	protected int[] officeIn;
	protected int[] conAuditorId;
	protected int riskLevel;

	private AuditQuestionDAO aQuestionDAO = (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");

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

	public boolean isShowCerts() {
		return showCerts;
	}

	public void setShowCerts(boolean showCerts) {
		this.showCerts = showCerts;
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

	public String getCertsOnly() {
		return certsOnly;
	}

	public void setCertsOnly(String certsOnly) {
		this.certsOnly = certsOnly;
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
		}
		if (permissions.isCorporate()) {
		}
		if (permissions.isPicsEmployee()) {
			setShowConAuditor(true);
			setShowRiskLevel(true);
		}
		if (!permissions.hasPermission(OpPerms.InsuranceCerts)) {
			setShowCerts(false);
		}
	}

	// ///
	public List<AuditQuestion> getStateLicensesList() throws Exception {
		return aQuestionDAO.findQuestionByType("License");
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return aQuestionDAO.findQuestionByType("Service");
	}

	public List<AuditQuestion> getWorksInList() throws Exception {
		return aQuestionDAO.findQuestionByType("Office Location");
	}

	public List<AuditQuestion> getOfficeInList() throws Exception {
		return aQuestionDAO.findQuestionByType("Office Location");
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

	public Map<String, String> getStateList() {
		return State.getStates(true);
	}

	public String[] getCertsOptions() {
		return new String[] { DEFAULT_CERTS, "Yes", "Only Certs", "No", "Exclude Certs" };
	}
}
