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
	
	protected boolean operator = true;
	protected boolean operatorSingle = false;
	protected boolean performedBy = true;
	protected boolean trade = true;
	protected boolean certs = false;
	protected boolean licensedIn = true;
	protected boolean worksIn = true;
	protected boolean officeIn = true;
	protected boolean taxID = true;
	protected boolean flagStatus = false;
	protected boolean conAuditor = false;
	protected boolean riskLevel = false;
	
	
	protected String performedBy;
	protected int[] trade;
	protected int[] operator;
	protected String certsOnly;
	protected int[] stateLicensedIn;
	protected int[] worksIn;
	protected String taxID = ReportFilterAccount.DEFAULT_TAX_ID;
	protected String flagStatus;
	protected int[] officeIn;
	protected int[] conAuditorId;
	protected int riskLevel;

	private AuditQuestionDAO aQuestionDAO = (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");
	
	
	public boolean isOperator() {
		return operator;
	}

	public void setOperator(boolean operator) {
		this.operator = operator;
	}

	public boolean isOperatorSingle() {
		return operatorSingle;
	}

	public void setOperatorSingle(boolean operatorSingle) {
		this.operatorSingle = operatorSingle;
	}

	public boolean isPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(boolean performedBy) {
		this.performedBy = performedBy;
	}

	public boolean isTrade() {
		return trade;
	}

	public void setTrade(boolean trade) {
		this.trade = trade;
	}

	public boolean isCerts() {
		return certs;
	}

	public void setCerts(boolean certs) {
		this.certs = certs;
	}

	public boolean isLicensedIn() {
		return licensedIn;
	}

	public void setLicensedIn(boolean licensedIn) {
		this.licensedIn = licensedIn;
	}

	public boolean isWorksIn() {
		return worksIn;
	}

	public void setWorksIn(boolean worksIn) {
		this.worksIn = worksIn;
	}

	public boolean isOfficeIn() {
		return officeIn;
	}

	public void setOfficeIn(boolean officeIn) {
		this.officeIn = officeIn;
	}

	public boolean isTaxID() {
		return taxID;
	}

	public void setTaxID(boolean taxID) {
		this.taxID = taxID;
	}

	public boolean isFlagStatus() {
		return flagStatus;
	}

	public void setFlagStatus(boolean flagStatus) {
		this.flagStatus = flagStatus;
	}

	public boolean isConAuditor() {
		return conAuditor;
	}

	public void setConAuditor(boolean conAuditor) {
		this.conAuditor = conAuditor;
	}

	public boolean isRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(boolean riskLevel) {
		this.riskLevel = riskLevel;
	}

	public void setPermissions(Permissions permissions) {
		super.setPermissions(permissions);
		
		if (permissions.isOperator()) {
			setOperator(false);
			setFlagStatus(true);
		}
		if (permissions.isCorporate()) {
		}
		if (permissions.isPicsEmployee()) {
			setConAuditor(true);
			setRiskLevel(true);
		}
		if (!permissions.hasPermission(OpPerms.InsuranceCerts)) {
			setCerts(false);
		}
	}

	/////
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
