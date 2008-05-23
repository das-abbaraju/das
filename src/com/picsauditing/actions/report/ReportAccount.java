package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.SearchBean;
import com.picsauditing.PICS.TradesBean;
import com.picsauditing.PICS.pqf.QuestionTypeList;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ReportAccount extends ReportActionSupport {

	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_ZIP = "- Zip -";
	public static final String DEFAULT_CITY = "- City -";
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_VISIBLE = "- Visible -";

	protected boolean forwardSingleResults = false;
	protected boolean skipPermissions = false;
	
	protected String startsWith;
	protected String accountName = DEFAULT_NAME;
	protected String industry;
	protected String performedBy;
	protected int trade;
	protected int[] operator;
	protected String city = DEFAULT_CITY;
	protected String state;
	protected String zip = DEFAULT_ZIP;
	protected String certsOnly;
	protected String visible;
	protected int stateLicensedIn;
	protected int worksIn;
	protected String taxID = DEFAULT_TAX_ID;
	protected String flagStatus;

	@Autowired
	protected SelectAccount sql = new SelectAccount();

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (!skipPermissions)
			sql.setPermissions(permissions);

		if (this.orderBy == null)
			this.orderBy = "a.name";
		sql.setType(SelectAccount.Type.Contractor);
		this.run(sql);

		if (forwardSingleResults
				&& !permissions.hasPermission(OpPerms.StatusOnly)
				&& this.data.size() == 1) {
			// Forward the user to the Contractor Details page
			ServletActionContext.getResponse().sendRedirect(
					"ContractorView.action?id=" + this.data.get(0).get("id"));
		}
		return SUCCESS;
	}

	// Getters for search lists
	public ArrayList<String> getIndustryList() {
		return Industry.getValuesWithDefault();
	}

	public Map<Integer, String> getStateLicensesList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("License", "- Licensed In -");
	}

	public Map<Integer, String> getTradeList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("Service", "- Trade -");
	}

	public Map<Integer, String> getWorksInList() throws Exception {
		QuestionTypeList questionList = new QuestionTypeList();
		return questionList.getQuestionMap("Office Location", "- Works In -");
	}

	public String[] getTradePerformedByList() throws Exception {
		return TradesBean.PERFORMED_BY_ARRAY;
	}

	public ArrayList<String> getFlagStatusList() throws Exception {
		return FlagColor.getValuesWithDefault();
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		List<OperatorAccount> opList = new ArrayList<OperatorAccount>();
		opList.add(new OperatorAccount(OperatorAccount.DEFAULT_NAME));

		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils
				.getBean("OperatorAccountDAO");
		opList.addAll(dao.findWhere("active='Y'"));
		return opList;

	}

	public Map<String, String> getStateList() {
		return State.getStates(true);
	}

	public String[] getCertsOptions() {
		return new String[] { DEFAULT_CERTS, "Yes", "Only Certs", "No",
				"Exclude Certs" };
	}

	public String[] getVisibleOptions() {
		return new String[] { DEFAULT_VISIBLE, "Y", "N" };
	}

	// Getters and setters for filter criteria
	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		report.addFilter(new SelectFilter("name", "a.name LIKE '?%'",
				startsWith));
		this.startsWith = startsWith;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		if(accountName == null || accountName.length() == 0)
			accountName = DEFAULT_NAME;
		report.addFilter(new SelectFilter("accountName", "a.name LIKE '%?%'",
				accountName, DEFAULT_NAME, DEFAULT_NAME));
		this.accountName = accountName;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		report
				.addFilter(new SelectFilter("industry", "a.industry = '?'",
						industry, Industry.DEFAULT_INDUSTRY,
						Industry.DEFAULT_INDUSTRY));
		this.industry = industry;
	}

	public String getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(String performedBy) {
		this.performedBy = performedBy;
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		String performedBy = ServletActionContext.getRequest().getParameter(
				"performedBy");
		String answerFilter = "";
		if (TradesBean.DEFAULT_PERFORMED_BY.equals(performedBy)
				|| performedBy == null) {
			performedBy = TradesBean.DEFAULT_PERFORMED_BY;
			answerFilter = "_%";
		} else {
			if ("Sub Contracted".equals(performedBy))
				answerFilter = "%S";
			else if ("Self Performed".equals(performedBy))
				answerFilter = "C%";
		}
		report.addFilter(new SelectFilterInteger("trade",
				"a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer LIKE '"
						+ answerFilter + "')", trade));

		this.trade = trade;
	}

	public int[] getOperator() {
		return operator;
	}

	public void setOperator(int[] operator) {
		String operatorList = Strings.implode(operator, ",");
		if (operatorList.equals("0"))
			return;
		report.addFilter(new SelectFilter(
						"generalContractorID",
						"a.id IN (SELECT subID FROM generalcontractors WHERE genID IN (?) )",
						operatorList));
		this.operator = operator;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		if(city == null || city.length() == 0)
			city = DEFAULT_CITY;
		report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", city,
				DEFAULT_CITY, DEFAULT_CITY));
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		report.addFilter(new SelectFilter("state", "a.state = '?'", state));
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		if(zip == null || zip.length() == 0)
			zip = DEFAULT_ZIP;
		report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", zip,
				DEFAULT_ZIP, DEFAULT_ZIP));
		this.zip = zip;
	}

	public String getCertsOnly() {
		return certsOnly;
	}

	public void setCertsOnly(String certsOnly) {
		report.addFilter(new SelectFilter("certsOnly", "c.isOnlyCerts = '?'",
				certsOnly, DEFAULT_CERTS, DEFAULT_CERTS));
		this.certsOnly = certsOnly;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		report.addFilter(new SelectFilter("visible", "a.active = '?'", visible,
				SearchBean.DEFAULT_VISIBLE, SearchBean.DEFAULT_VISIBLE));
		this.visible = visible;
	}

	public int getStateLicensedIn() {
		return stateLicensedIn;
	}

	public void setStateLicensedIn(int stateLicensedIn) {
		report
				.addFilter(new SelectFilterInteger(
						"stateLicensedIn",
						"a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer <> '')",
						stateLicensedIn));
		this.stateLicensedIn = stateLicensedIn;
	}

	public int getWorksIn() {
		return worksIn;
	}

	public void setWorksIn(int worksIn) {
		report
				.addFilter(new SelectFilterInteger(
						"worksIn",
						"a.id IN (SELECT conID FROM pqfdata WHERE questionID=? AND answer LIKE 'Yes%')",
						worksIn));
		this.worksIn = worksIn;
	}

	public String getTaxID() {
		return taxID;
	}

	public void setTaxID(String taxID) {
		if(taxID == null || taxID.length() == 0)
			taxID = DEFAULT_TAX_ID;
		report.addFilter(new SelectFilter("taxID", "c.taxID = '?'", taxID,
				DEFAULT_TAX_ID, DEFAULT_TAX_ID));
		this.taxID = taxID;
	}

	/**
	 * Flag Color needs to now include the status of current audits if the
	 * operator requires an audit, that audit must be Active/Exempt
	 */
	public String getFlagStatus() {
		return flagStatus;
	}

	public void setFlagStatus(String flagStatus) {
		this.flagStatus = flagStatus;
		report.addFilter(new SelectFilter("flagStatus", "flags.flag = '?'",
				flagStatus, FlagColor.DEFAULT_FLAG_STATUS,
				FlagColor.DEFAULT_FLAG_STATUS));

	}

	/**
	 * Return the number of active contractors visible to an Operator or a
	 * Corporate account This method shouldn't be use be Admins, auditors, and
	 * contractors
	 * 
	 * @return
	 */
	public int getContractorCount() {
		if (permissions.isOperator() || permissions.isCorporate()) {
			OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils
					.getBean("OperatorAccountDAO");
			return dao.getContractorCount(permissions.getAccountId());
		}
		// This method shouldn't be use be Admins, auditors, and contractors so
		// just return 0
		return 0;
	}
}
