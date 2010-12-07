package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.DynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAccount extends ReportActionSupport implements Preparable {

	protected boolean runReport = true;
	protected boolean skipPermissions = false;
	protected Boolean showContactInfo = null;
	protected Boolean showTradeInfo = null;

	// ?? may need to move to Filters
	protected List<Integer> ids = new ArrayList<Integer>();

	protected SelectAccount sql = new SelectAccount();
	private ReportFilterContractor filter = new ReportFilterContractor();

	public ReportAccount() {
		listType = ListType.Contractor;
		orderByDefault = "a.nameIndex";
	}

	/**
	 * 
	 * @throws Exception
	 */
	protected void checkPermissions() throws Exception {
	}

	protected void buildQuery() {
		sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);

		if (!skipPermissions)
			sql.setPermissions(permissions);

		if (download) {
			getFilter().setPrimaryInformation(true);
		}

		sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		sql.addField("a.phone");
		sql.addField("a.fax");
		sql.addField("a.creationDate");
		sql.addField("c.riskLevel");

		addFilterToSQL();
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);
		// Add the following columns to the far right
		if (permissions.isOperator()) {
			excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.Date), 400);
			if (permissions.hasPermission(OpPerms.ViewUnApproved)) {
				excelSheet.addColumn(new ExcelColumn("workStatus", "Work Status"));
			}
		}
		excelSheet.addColumn(new ExcelColumn("creationDate", "Creation Date", ExcelCellType.Date), 500);
		excelSheet.addColumn(new ExcelColumn("riskLevel", "Risk Level", ExcelCellType.Enum));
		if (isShowContact()) {
			excelSheet.addColumn(new ExcelColumn("contactname", "Primary Contact"));
			excelSheet.addColumn(new ExcelColumn("contactphone", "Phone"));
			excelSheet.addColumn(new ExcelColumn("contactemail", "Email"));
			excelSheet.addColumn(new ExcelColumn("address", "Address"));
			excelSheet.addColumn(new ExcelColumn("city", "City"));
			excelSheet.addColumn(new ExcelColumn("state", "State"));
			excelSheet.addColumn(new ExcelColumn("zip", "Zip Code", ExcelCellType.Integer));
			excelSheet.addColumn(new ExcelColumn("web_URL", "URL"));
		}
		if (isShowTrade()) {
			excelSheet.addColumn(new ExcelColumn("main_trade", "Main Trade"));
			excelSheet.addColumn(new ExcelColumn("tradesSelf", "Self Performed"));
			excelSheet.addColumn(new ExcelColumn("tradesSub", "Sub Contracted"));
		}
		excelSheet.addColumn(new ExcelColumn("phone", "Corporate Phone"));
		excelSheet.addColumn(new ExcelColumn("fax", "Fax Number"));

		// Add these to the beginning
		excelSheet.addColumn(new ExcelColumn("id", ExcelCellType.Integer), 0);
		excelSheet.addColumn(new ExcelColumn("name", "Contractor Name"));
	}

	// TODO make this method final
	/**
	 * Do not override this method!<br>
	 * <br>
	 * 
	 * 1) This method checks the user is logged in and has the appropriate
	 * permissions.<br>
	 * 2) Next it determines if the report should run by default.<br>
	 * 3) It builds the Query and runs the report<br>
	 * 4) Finally, it returns the results
	 * 
	 * @see checkPermissions()
	 * @see runReport
	 * @see buildQuery()
	 * @see run(SelectAccount sql)
	 * @see returnResult()
	 */
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

		if (runReport) {
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
			addExcelColumns();
			String filename = this.getClass().getName().replace("com.picsauditing.actions.report.", "");
			excelSheet.setName(filename);
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	protected void addFilterToSQL() {
		ReportFilterContractor f = getFilter();

		/** **** Filters for Accounts ********** */
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", f.getStartsWith()));

		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Utilities.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Utilities.escapeQuotes(accountName) + "'", accountName));
			sql.addField("a.dbaName");
		}

		String statusList = Strings.implodeForDB(f.getStatus(), ",");
		if (filterOn(statusList)) {
			sql.addWhere("a.status IN (" + statusList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getCity(), ReportFilterAccount.DEFAULT_CITY))
			report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", f.getCity()));

		String stateList = Strings.implodeForDB(f.getState(), ",");
		if (filterOn(stateList)) {
			sql.addWhere("a.state IN (" + stateList + ")");
			setFiltered(true);
		}

		String countryList = Strings.implodeForDB(f.getCountry(), ",");
		if (filterOn(countryList) && !filterOn(stateList)) {
			sql.addWhere("a.country IN (" + countryList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getZip(), ReportFilterAccount.DEFAULT_ZIP))
			report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", f.getZip()));

		if (f.isPrimaryInformation()) {
			sql.addField("contact.name AS contactname");
			sql.addField("contact.phone AS contactphone");
			sql.addField("contact.email AS contactemail");
			sql.addField("a.address");
			sql.addField("a.city");
			sql.addField("a.state");
			sql.addField("a.zip");
			sql.addField("a.web_URL");
		}

		if (f.isTradeInformation()) {
			sql.addField("c.main_trade");
			sql.addField("c.tradesSelf");
			sql.addField("c.tradesSub");
		}

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

		if (f.getOperatorSingle() > 0) {
			sql.addWhere("a.id IN (SELECT subID FROM generalcontractors WHERE genID = " + f.getOperatorSingle() + ")");
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

		if (filterOn(f.getFlagStatus())) {
			String list = Strings.implodeForDB(f.getFlagStatus(), ",");
			sql.addWhere("gc.flag IN (" + list + ")");
			setFiltered(true);
		}

		if (filterOn(f.getWaitingOn()))
			report.addFilter(new SelectFilter("waitingOn", "gc.waitingOn = '?'", f.getWaitingOn()));

		if (filterOn(f.getConAuditorId())) {
			String list = Strings.implode(f.getConAuditorId(), ",");
			sql.addWhere("c.welcomeAuditor_id IN (" + list + ")");
			setFiltered(true);

		}

		if (filterOn(f.getRiskLevel())) {
			String list = Strings.implode(f.getRiskLevel(), ",");
			sql.addWhere("c.riskLevel IN (" + list + ")");
			setFiltered(true);
		}

		if (f.getEmailTemplate() > 0) {
			String emailQueueJoin = "LEFT JOIN email_queue eq on eq.conid = a.id AND eq.templateID = "
					+ f.getEmailTemplate();
			if (filterOn(f.getEmailSentDate())) {
				emailQueueJoin += " AND eq.sentDate >= '" + DateBean.format(f.getEmailSentDate(), "yyyy-M-d") + "'";
			}
			sql.addJoin(emailQueueJoin);
			sql.addWhere("eq.emailID IS NULL");
			setFiltered(true);
		}

		if (filterOn(f.getRegistrationDate1())) {
			report.addFilter(new SelectFilterDate("registrationDate1", "a.creationDate >= '?'", DateBean.format(f
					.getRegistrationDate1(), "M/d/yy")));
		}

		if (filterOn(f.getRegistrationDate2())) {
			report.addFilter(new SelectFilterDate("registrationDate2", "a.creationDate < '?'", DateBean.format(f
					.getRegistrationDate2(), "M/d/yy")));
		}

		if (f.isPendingPqfAnnualUpdate()) {
			String query = "a.id IN (SELECT ca.conID FROM contractor_audit ca "
					+ "JOIN contractor_audit_operator cao ON cao.auditID = ca.id WHERE cao.status = 'Pending' AND cao.visible = 1 AND ca.auditTypeID IN (1,11))";
			sql.addWhere(query);
		}

		if (filterOn(f.getAccountManager())) {
			String list = Strings.implode(f.getAccountManager(), ",");
			sql.addWhere("a.id IN (SELECT accountID FROM account_user WHERE userID IN (" + list + ") AND role = 'PICSAccountRep' AND startDate < NOW() AND endDate > NOW())");
			setFiltered(true);
		}

		if (filterOn(f.getOperatorTagName(), 0)) {
			String query = "a.id IN (SELECT ct.conID from contractor_tag ct " + "WHERE ct.tagID = "
					+ f.getOperatorTagName() + ")";
			sql.addWhere(query);
		}

		if (f.getCcOnFile() < 2)
			sql.addWhere("c.ccOnFile = " + f.getCcOnFile());

		if (filterOn(f.getDeactivationReason()))
			report.addFilter(new SelectFilter("deactivationReason", "a.reason = '?'", f.getDeactivationReason()));

		if (filterOn(f.getCustomAPI()) && permissions.isAdmin())
			sql.addWhere(f.getCustomAPI());

		if (filterOn(getFilter().getMinorityQuestion(), 0)) {
			sql.addJoin("JOIN contractor_audit casd ON casd.conID = a.id AND casd.auditTypeID = 1 ");
			if (getFilter().getMinorityQuestion() != 3) {
				sql.addJoin("JOIN pqfdata pdsd on casd.id = pdsd.auditID AND pdsd.questionID = "
						+ getFilter().getMinorityQuestion());
				if (getFilter().getMinorityQuestion() == 3543)
					sql.addWhere("pdsd.answer = 'X'");
				else
					sql.addWhere("pdsd.answer = 'Yes'");
			} else {
				sql.addJoin("JOIN pqfdata pd2340 on casd.id = pd2340.auditID AND pd2340.questionID = 2340");
				sql.addJoin("JOIN pqfdata pd2354 on casd.id = pd2354.auditID AND pd2354.questionID = 2354");
				sql.addJoin("JOIN pqfdata pd2373 on casd.id = pd2373.auditID AND pd2373.questionID = 2373");
				sql.addJoin("LEFT JOIN pqfdata pd3543 on casd.id = pd3543.auditID AND pd3543.questionID = 3543");
				sql
						.addWhere("pd2340.answer = 'Yes' OR pd2354.answer = 'Yes' OR pd2373.answer = 'Yes' OR pd3543.answer = 'X'");
			}
		}

		if (filterOn(getFilter().getWorkStatus()) && permissions.isOperator()) {
			sql.addWhere("gc.workStatus = '" + getFilter().getWorkStatus() + "'");
		}

		if (getFilter().isShowInsuranceLimits()) {
			if (filterOn(getFilter().getGlEachOccurrence(), ReportFilterContractor.DEFAULT_AMOUNT)) {
				sql.addAuditQuestion(2074, 13, true);
				sql.addWhere("REPLACE(q2074.answer,',','') >= " + getFilter().getGlEachOccurrence());
			}
			if (filterOn(getFilter().getGlGeneralAggregate(), ReportFilterContractor.DEFAULT_AMOUNT)) {
				sql.addAuditQuestion(2079, 13, true);
				sql.addWhere("REPLACE(q2079.answer,',','') >= " + getFilter().getGlGeneralAggregate());
			}
			if (filterOn(getFilter().getAlCombinedSingle(), ReportFilterContractor.DEFAULT_AMOUNT)) {
				sql.addAuditQuestion(2155, 15, true);
				sql.addWhere("REPLACE(q2155.answer,',','') >= " + getFilter().getAlCombinedSingle());
			}
			if (filterOn(getFilter().getWcEachAccident(), ReportFilterContractor.DEFAULT_AMOUNT)) {
				sql.addAuditQuestion(2149, 14, true);
				sql.addWhere("REPLACE(q2149.answer,',','') >= " + getFilter().getWcEachAccident());
			}
			if (filterOn(getFilter().getExEachOccurrence(), ReportFilterContractor.DEFAULT_AMOUNT)) {
				sql.addAuditQuestion(2161, 16, true);
				sql.addWhere("REPLACE(q2161.answer,',','') >= " + getFilter().getExEachOccurrence());
			}

			sql.addGroupBy("a.id");
		}
		
		if (getFilter().isOq() && permissions.isOperatorCorporate())
			sql.addWhere("a.requiresOQ = 1");
		
		if (getFilter().isHse() && permissions.isOperatorCorporate())
			sql.addWhere("a.requiresCompetencyReview = 1");
	}

	private void createPqfDataClause(SelectSQL sql, String where) {
		String query = "a.id IN (SELECT ca.conID FROM contractor_audit ca JOIN pqfdata d on ca.id = d.auditID "
				+ "JOIN contractor_audit_operator cao ON cao.auditID = ca.id WHERE cao.status IN ('Complete','Submitted') AND cao.visible = 1  AND ca.auditTypeID = 1 " + where + ")";
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

	public boolean isShowContact() {
		if (getFilter() == null)
			return false;
		if (showContactInfo == null)
			showContactInfo = getFilter().isPrimaryInformation();
		return showContactInfo;
	}

	public boolean isShowTrade() {
		if (getFilter() == null)
			return false;
		if (showTradeInfo == null)
			showTradeInfo = getFilter().isTradeInformation();
		return showTradeInfo;
	}
}
