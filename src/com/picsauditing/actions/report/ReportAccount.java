package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAccount extends ReportActionSupport implements Preparable {

	protected boolean skipPermissions = false;

	// ?? may need to move to Filters
	protected List<Integer> ids = new ArrayList<Integer>();

	protected SelectAccount sql = new SelectAccount();
	private ReportFilterContractor filter = new ReportFilterContractor();

	public ReportAccount() {
		listType = ListType.Contractor;
		orderByDefault = "a.name";
	}

	/**
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	protected void checkPermissions() throws Exception {
	}

	protected boolean runReport() {
		return true;
	}

	protected void buildQuery() {
		sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		if (!skipPermissions)
			sql.setPermissions(permissions);

		if (download) {
			// Add a bunch more fields that we can show in the download page
			addDownload();
		}

		addFilterToSQL();
	}

	// TODO make this method final
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		// Figure out if this is mailmerge call or not
		// This is not very robust, we should refactor this eventually
		// if (!filter.isAjax() && filter.isAllowMailMerge()) {
		if (button != null && button.contains("Write Email")) {
			// This condition only occurs when sending results to the mail merge
			// tool
			this.mailMerge = true;
		}

		checkPermissions();
		
		if (runReport()) {
			buildQuery();
			run(sql);
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.clear();
			wizardSession.setFilter(listType, filter);

			return returnResult();
		}
		return SUCCESS;
	}

	protected String returnResult() throws IOException {
		if (mailMerge) {
			Set<Integer> ids = new HashSet<Integer>();
			for (DynaBean dynaBean : data) {
				ids.add((Integer) dynaBean.get("id"));
			}
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.setIds(ids);
			wizardSession.setListTypes(ListType.Contractor);
			ServletActionContext.getResponse().sendRedirect("MassMailer.action");
			this.addActionMessage("Redirected to MassMailer");
			return BLANK;
		}
		
		if (download) {
			String filename = this.getClass().getName().replace("com.picsauditing.actions.report.", "");
			filename += ".csv";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		}

		return SUCCESS;
	}

	@Deprecated
	public String executeOld() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (!skipPermissions)
			sql.setPermissions(permissions);

		addFilterToSQL();

		// Figure out if this is mailmerge call or not
		// This is not very robust, we should refactor this eventually
		// if (!filter.isAjax() && filter.isAllowMailMerge()) {
		if (button != null && button.contains("Write Email")) {
			// This condition only occurs when sending results to the mail merge
			// tool
			this.mailMerge = true;
		}

		sql.setType(SelectAccount.Type.Contractor);
		this.run(sql);

		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		wizardSession.clear();
		wizardSession.setFilter(listType, filter);

		return returnResult();
	}

	protected void addFilterToSQL() {
		ReportFilterContractor f = getFilter();

		/** **** Filters for Accounts ********** */
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "a.name LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.name LIKE '%?%'", accountName));
		}

		if (filterOn(f.getVisible(), ReportFilterAccount.DEFAULT_VISIBLE))
			report.addFilter(new SelectFilter("visible", "a.active = '?'", f.getVisible()));

		String industryList = Strings.implodeForDB(f.getIndustry(), ",");
		if (filterOn(industryList)) {
			sql.addWhere("a.industry IN (" + industryList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getCity(), ReportFilterAccount.DEFAULT_CITY))
			report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", f.getCity()));

		if (filterOn(f.getState()))
			report.addFilter(new SelectFilter("state", "a.state = '?'", f.getState()));

		if (filterOn(f.getZip(), ReportFilterAccount.DEFAULT_ZIP))
			report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", f.getZip()));

		/** **** Filters for Contractors ********** */

		if (filterOn(f.getTrade())) {
			String tradeList = Strings.implode(f.getTrade(), ",");
			String answerFilter = "";
			if (!filterOn(f.getPerformedBy(), ReportFilterContractor.DEFAULT_PERFORMED_BY))
				answerFilter = "_%";
			else {
				if ("Sub Contracted".equals(f.getPerformedBy()))
					answerFilter = "%S";
				else if ("Self Performed".equals(f.getPerformedBy()))
					answerFilter = "C%";
			}
			createPqfDataClause(sql, "AND d.questionID IN (" + tradeList + ") AND d.answer LIKE '" + answerFilter + "'");
		}

		if (filterOn(f.getOperator())) {
			String list = Strings.implode(f.getOperator(), ",");
			sql.addWhere("a.id IN (SELECT subID FROM generalcontractors WHERE genID IN (" + list + ") )");
			setFiltered(true);

		}

		if (filterOn(f.getStateLicensedIn())) {
			String list = Strings.implode(f.getStateLicensedIn(), ",");
			createPqfDataClause(sql, "AND d.questionID IN (" + list + ") AND d.answer > ''");
		}

		if (filterOn(f.getWorksIn())) {
			String list = Strings.implode(f.getWorksIn(), ",");
			createPqfDataClause(sql, "AND d.questionID IN (" + list + ") AND d.answer LIKE 'Yes%'");
		}

		if (filterOn(f.getOfficeIn())) {
			String list = Strings.implode(f.getOfficeIn(), ",");
			createPqfDataClause(sql, "AND d.questionID IN (" + list + ") AND d.answer LIKE 'Yes with Office'");
		}

		if (filterOn(f.getTaxID(), ReportFilterContractor.DEFAULT_TAX_ID))
			report.addFilter(new SelectFilter("taxID", "c.taxID = '?'", f.getTaxID()));

		if (filterOn(f.getFlagStatus(), FlagColor.DEFAULT_FLAG_STATUS))
			report.addFilter(new SelectFilter("flagStatus", "flags.flag = '?'", f.getFlagStatus()));

		if (filterOn(f.getWaitingOn()))
			report.addFilter(new SelectFilter("waitingOn", "flags.waitingOn = '?'", f.getWaitingOn()));

		if (filterOn(f.getConAuditorId())) {
			String list = Strings.implode(f.getConAuditorId(), ",");
			sql.addWhere("c.welcomeAuditor_id IN (" + list + ")");
			setFiltered(true);

		}

		if (filterOn(f.getRiskLevel(), 0))
			report.addFilter(new SelectFilterInteger("riskLevel", "c.riskLevel = '?'", f.getRiskLevel()));

		if((filterOn(f.getEmailtemplate()) && Integer.parseInt(f.getEmailtemplate()) > 0) || filterOn(f.getEmailSentDate1()) || filterOn(f.getEmailSentDate2())) {
			sql.addJoin("JOIN email_queue eq on eq.conid = a.id");	
		}
		
		if (filterOn(f.getEmailtemplate()) && Integer.parseInt(f.getEmailtemplate()) > 0) {
			sql.addWhere("eq.templateID = "+ f.getEmailtemplate());
			setFiltered(true);
		}

		if (filterOn(f.getEmailSentDate1())) {
			sql.addWhere("sentDate < '"+ DateBean.format(f.getEmailSentDate1(), "M/d/yy")+"'");
			setFiltered(true);
		}

		if (filterOn(f.getEmailSentDate2())) {
			sql.addWhere("sentDate >= '"+ DateBean.format(f.getEmailSentDate2(), "M/d/yy")+"'");
			setFiltered(true);
		}
	}

	private void createPqfDataClause(SelectSQL sql, String where) {
		String query = "a.id IN (SELECT ca.conID FROM contractor_audit ca JOIN pqfdata d on ca.id = d.auditID "
				+ "WHERE ca.auditStatus IN ('Active','Submitted') AND ca.auditTypeID = 1 " + where + ")";
		sql.addWhere(query);
		setFiltered(true);
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
		ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		return cAccountDAO.getActiveContractorCounts("");
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

	public SelectAccount getSql() {
		return sql;
	}

	public void setSql(SelectAccount sql) {
		this.sql = sql;
	}
	
	public void addDownload() {
		sql.addField("a.contact");
		sql.addField("a.address");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.zip");
		sql.addField("a.phone");
		sql.addField("a.phone2");
		sql.addField("a.fax");
		sql.addField("a.email");
		sql.addField("a.web_URL");
		sql.addField("a.creationDate");
		sql.addField("c.taxID");
		sql.addField("c.secondContact");
		sql.addField("c.secondPhone");
		sql.addField("c.secondEmail");
		sql.addField("c.billingContact");
		sql.addField("c.billingPhone");
		sql.addField("c.billingEmail");
		sql.addField("c.riskLevel");
	}
}
