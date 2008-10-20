package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.SearchBean;
import com.picsauditing.PICS.TradesBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ReportAccount extends ReportActionSupport implements Preparable {

	protected boolean forwardSingleResults = false;
	protected boolean skipPermissions = false;

	protected List<Integer> ids = new ArrayList<Integer>(); //?? may need to move to Filters

	protected SelectAccount sql = new SelectAccount();
	private ReportFilterContractor filter = new ReportFilterContractor();

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		System.out.println(ActionContext.getContext().getSession());
		if (!forceLogin())
			return LOGIN;
		if (!skipPermissions)
			sql.setPermissions(permissions);

		addFilterToSQL();
		
		if (this.orderBy == null)
			this.orderBy = "a.name";
		sql.setType(SelectAccount.Type.Contractor);
		this.run(sql);

		if (forwardSingleResults && this.data.size() == 1) {
			// Forward the user to the Contractor Details page
			ServletActionContext.getResponse().sendRedirect("ContractorView.action?id=" + this.data.get(0).get("id"));
		}

		if (filtered == null)
			filtered = false;

		if ("Draft Email".equals(button)) {
			for (DynaBean dynaBean : data) {
				ids.add((Integer) dynaBean.get("id"));
			}

			return "EmailSender";
		}
		return SUCCESS;
	}
	
	private void addFilterToSQL() {
		ReportFilterContractor f = getFilter();
		
		if (!Strings.isEmpty(f.getStartsWith()))
			report.addFilter(new SelectFilter("name", "a.name LIKE '?%'", getFilter().getStartsWith()));

		if (!Strings.isEmpty(f.getAccountName()) 
				&& !ReportFilterAccount.DEFAULT_NAME.equals(f.getAccountName()) )
			report.addFilter(new SelectFilter("accountName", "a.name LIKE '%?%'", f.getAccountName()));

		if (f.getIndustry() != null && f.getIndustry().si) 
	public void setIndustry(Industry[] industry) {
		String industryList = Strings.implodeForDB(industry, ",");
		sql.addWhere("a.industry IN (" + industryList + ")");
		filtered = true;
	}

	public void setTrade(int[] trade) {
		String performedBy = ServletActionContext.getRequest().getParameter("performedBy");
		String answerFilter = "";
		if (TradesBean.DEFAULT_PERFORMED_BY.equals(performedBy) || performedBy == null) {
			performedBy = TradesBean.DEFAULT_PERFORMED_BY;
			answerFilter = "_%";
		} else {
			if ("Sub Contracted".equals(performedBy))
				answerFilter = "%S";
			else if ("Self Performed".equals(performedBy))
				answerFilter = "C%";
		}
		String tradeList = Strings.implode(trade, ",");
		if (tradeList.equals("0"))
			return;

		this.trade = trade;
		createPqfDataClause(sql, "AND d.questionID IN (" + tradeList + ") AND d.answer LIKE '" + answerFilter + "'");
		filtered = true;
	}

	public void setOperator(int[] operator) {
		String operatorList = Strings.implode(operator, ",");
		if (operatorList.equals("0"))
			return;
		report.addFilter(new SelectFilter("generalContractorID",
				"a.id IN (SELECT subID FROM generalcontractors WHERE genID IN (?) )", operatorList));
		this.operator = operator;
	}

	public void setCity(String city) {
		if (city == null || city.length() == 0)
			city = ReportFilterAccount.DEFAULT_CITY;
		report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", city, ReportFilterAccount.DEFAULT_CITY, ReportFilterAccount.DEFAULT_CITY));
		this.city = city;
	}

	public void setState(String state) {
		report.addFilter(new SelectFilter("state", "a.state = '?'", state));
		this.state = state;
	}

	public void setZip(String zip) {
		if (zip == null || zip.length() == 0)
			zip = ReportFilterAccount.DEFAULT_ZIP;
		report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", zip, ReportFilterAccount.DEFAULT_ZIP, ReportFilterAccount.DEFAULT_ZIP));
		this.zip = zip;
	}

	public void setCertsOnly(String certsOnly) {
		report.addFilter(new SelectFilter("certsOnly", "c.isOnlyCerts = '?'", certsOnly, ReportFilterAccount.DEFAULT_CERTS, ReportFilterAccount.DEFAULT_CERTS));
		this.certsOnly = certsOnly;
	}

	public void setVisible(String visible) {
		report.addFilter(new SelectFilter("visible", "a.active = '?'", visible, SearchBean.DEFAULT_VISIBLE,
				SearchBean.DEFAULT_VISIBLE));
		this.visible = visible;
	}

	public void setStateLicensedIn(int[] stateLicensedIn) {
		String stateLicensedInList = Strings.implode(stateLicensedIn, ",");
		this.stateLicensedIn = stateLicensedIn;
		createPqfDataClause(sql, "AND d.questionID IN (" + stateLicensedInList + ") AND d.answer > ''");
		filtered = true;
	}

	private void createPqfDataClause(SelectSQL sql, String where) {
		String query = "a.id IN (SELECT ca.conID FROM contractor_audit ca JOIN pqfdata d USING (auditID) "
				+ "WHERE ca.auditStatus IN ('Active','Submitted') AND ca.auditTypeID = 1 " + where + ")";
		sql.addWhere(query);
	}

	public void setWorksIn(int[] worksIn) {
		String worksInList = Strings.implode(worksIn, ",");
		this.worksIn = worksIn;
		createPqfDataClause(sql, "AND d.questionID IN (" + worksInList + ") AND d.answer LIKE 'Yes%'");
		filtered = true;
	}

	public void setOfficeIn(int[] officeIn) {
		String officeInList = Strings.implode(officeIn, ",");
		this.officeIn = officeIn;
		createPqfDataClause(sql, "AND d.questionID IN (" + officeInList + ") AND d.answer LIKE 'Yes with Office'");
		filtered = true;
	}

	public void setTaxID(String taxID) {
		if (taxID == null || taxID.length() == 0)
			taxID = ReportFilterAccount.DEFAULT_TAX_ID;
		report.addFilter(new SelectFilter("taxID", "c.taxID = '?'", taxID, ReportFilterAccount.DEFAULT_TAX_ID, ReportFilterAccount.DEFAULT_TAX_ID));
		this.taxID = taxID;
	}

	public void setFlagStatus(String flagStatus) {
		this.flagStatus = flagStatus;
		report.addFilter(new SelectFilter("flagStatus", "flags.flag = '?'", flagStatus, FlagColor.DEFAULT_FLAG_STATUS,
				FlagColor.DEFAULT_FLAG_STATUS));
	}

	public void setConAuditorId(int[] conAuditorId) {
		String list = Strings.implode(conAuditorId, ",");
		this.conAuditorId = conAuditorId;
		sql.addWhere("c.welcomeAuditor_id IN (" + list + ")");
		filtered = true;
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
			OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
			return dao.getContractorCount(permissions.getAccountId(), permissions);
		}
		// This method shouldn't be used by Admins, auditors, and contractors so
		// just return 0
		return 0;
	}

	public void setRiskLevel(int riskLevel) {
		report.addFilter(new SelectFilterInteger("riskLevel", "c.riskLevel = '?'", riskLevel));
		this.riskLevel = riskLevel;
	}

	}

	public List<Integer> getIds() {
		return ids;
	}
	
	public ReportFilterContractor getFilter() {
		return filter;
	}

	@Override
	public void prepare() throws Exception {
		this.getPermissions();
		getFilter().setPermissions(permissions);
	}
}
