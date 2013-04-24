package com.picsauditing.actions.report;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;
import org.apache.commons.beanutils.DynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class ReportAccount extends ReportActionSupport implements Preparable {
	protected boolean runReport = true;
	protected boolean skipPermissions = false;
	protected Boolean showContactInfo = null;
	protected Boolean showTradeInfo = null;
	protected String reportAddresses = null;
	protected boolean searchForNew = false;

	// ?? may need to move to Filters
	protected List<Integer> ids = new ArrayList<Integer>();

	protected SelectAccount sql = new SelectAccount();
	protected List<SelectSQL> unionSql = new ArrayList<SelectSQL>();

	protected ReportFilterContractor filter = new ReportFilterContractor();

	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;

	public ReportAccount() {
		listType = ListType.Contractor;
		orderByDefault = "a.nameIndex";
	}

	/**
	 * @throws Exception
	 */
	protected void checkPermissions() throws Exception {
	}

	protected void buildQuery() {
		sql = new SelectAccount();
		if (permissions.getTopAccountID() == OperatorAccount.SUNCOR || permissions.isAdmin())
			getFilter().setShowAccountLevel(true);

		if (permissions.isAssessment())
			sql.addWhere("a.requiresOQ = 1");
		else {
			sql.setType(SelectAccount.Type.Contractor);
			sql.addField("c.riskLevel");
			sql.addField("c.safetyRisk");
			sql.addField("c.productRisk");
			sql.addJoin("LEFT JOIN users contact ON contact.id = a.contactID");
		}

		if (!skipPermissions)
			sql.setPermissions(permissions);

		if (download) {
			getFilter().setPrimaryInformation(true);
		}

		sql.addField("a.phone");
		sql.addField("a.fax");
		sql.addField("a.creationDate");

		addFilterToSQL();
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);
		// Add the following columns to the far right
		if (permissions.isOperatorCorporate()) {
			excelSheet.addColumn(new ExcelColumn("flag", "Flag", ExcelCellType.String), 400);
			excelSheet.addColumn(new ExcelColumn("workStatus", "Work Status"));
		}
		excelSheet.addColumn(new ExcelColumn("creationDate", "Creation Date", ExcelCellType.Date), 500);
		excelSheet.addColumn(new ExcelColumn("safetyRisk", "Safety Assessment", ExcelCellType.Enum));
		excelSheet.addColumn(new ExcelColumn("productRisk", "Product Assessment", ExcelCellType.Enum));
		if (isShowContact()) {
			excelSheet.addColumn(new ExcelColumn("contactname", "Primary Contact"));
			excelSheet.addColumn(new ExcelColumn("contactphone", "Phone"));
			excelSheet.addColumn(new ExcelColumn("contactemail", "Email"));
			excelSheet.addColumn(new ExcelColumn("address", "Address"));
			excelSheet.addColumn(new ExcelColumn("city", "City"));
			excelSheet.addColumn(new ExcelColumn("countrySubdivision", "CountrySubdivision"));
			excelSheet.addColumn(new ExcelColumn("zip", "Zip Code"));
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
	 * <p/>
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
		// Figure out if this is mailmerge call or not
		// This is not very robust, we should refactor this eventually
		// if (!filter.isAjax() && filter.isAllowMailMerge()) {
		if (button != null && button.contains("Write Email")) {
			// This condition only occurs when sending results to the mail merge
			// tool
			this.mailMerge = true;
		} else if (button != null && button.contains("Email Report")) {
			this.mailReport = true;
		}

		checkPermissions();

		if (runReport) {
			buildQuery();
			run(sql, unionSql);

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
			String filename = this.getClass().getSimpleName();
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
		addAccountFilters();
		addContractorSpecificFilters();
	}

	protected void addAccountFilters() {
		filterOnStartsWith();
		filterOnAccountName();
		filterOnStatus();
		filterOnCity();
		filterOnLocation();
		filterOnZip();
		filterShowPrimaryContactInformation();
		filterShowTradeInformation();
	}

	protected void addContractorSpecificFilters() {
		filterOnTrades();
		filterOnContractorIds();
		filterOnOperators();
		filterOnOperatorSingle();
		filterOnTaxId();
		filterOnFlagStatus();
		filterOnWaitingOn();
		filterOnAuditor();
		filterOnPolicyChangedDates();
		filterOnAccountLevels();
		filterOnRiskLevels();
		filterOnServiceType();
		filterOnEmailTemplate();
		filterOnRegistrationDates();
		filterOnPendingPQFAnnualUpdate();
		filterOnOperatorTagName();
		filterOnCreditCardOnFile();
		filterOnDeactivationReason();
		filterOnCustomAPI();
		filterOnMinorityQuestions();
		filterOnWorkStatus();
		filterOnInsuranceInformation();
		filterOnAccountProperties();
		filterOnInsideSales();
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
			return operatorAccountDAO.getContractorCount(permissions.getAccountId(), permissions);
		}
		// FIXME: this method call used to be getActiveContractorCounts("") which had nothing to do 
		// with "active" contractors but was instead with the "" a count of all contractors 
		// (unrestricted). This seems to me to be in contradiction with the comment above. Is the
		// comment wrong? Or was the implementation of the method?
		return contractorAccountDAO.findTotalContractorCount();
	}

	public List<Integer> getIds() {
		return ids;
	}

	public ReportFilterContractor getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterContractor filter) {
		this.filter = filter;
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

	public List<SelectSQL> getUnionSql() {
		return unionSql;
	}

	public void setUnionSql(List<SelectSQL> unionSql) {
		this.unionSql = unionSql;
	}

	public void addUnion(SelectAccount sql) {
		unionSql.add(sql);
	}

	public SelectSQL getUnion(int index) {
		return unionSql.get(index);
	}

	public SelectSQL removeUnion(int index) {
		return unionSql.remove(index);
	}

	public void clearUnion(SelectAccount sql) {
		unionSql.clear();
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

	public String getReportAddresses() {
		return reportAddresses;
	}

	public void setReportAddresses(String reportAddresses) {
		this.reportAddresses = reportAddresses;
	}

	protected void filterOnStartsWith() {
		if (filterOn(getFilter().getStartsWith())) {
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", getFilter().getStartsWith()));
		}
	}

	protected void filterOnAccountName() {
		if (filterOn(getFilter().getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = getFilter().getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Strings.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Strings.escapeQuotes(accountName) + "'", accountName));
			sql.addField("a.dbaName");
		}
	}

	protected void filterOnStatus() {
		String statusList = Strings.implodeForDB(getFilter().getStatus(), ",");
		if (filterOn(statusList)) {
			sql.addWhere("a.status IN (" + statusList + ")");
			setFiltered(true);
		}
	}

	protected void filterOnCity() {
		if (filterOn(getFilter().getCity(), ReportFilterAccount.getDefaultCity())) {
			report.addFilter(new SelectFilter("city", "a.city LIKE '%?%'", getFilter().getCity()));
		}
	}

	protected void filterOnLocation() {
		if (filterOn(getFilter().getLocation())) {
			List<String> countrySubdivisions = new ArrayList<String>();
			List<String> countries = new ArrayList<String>();

			for (String location : getFilter().getLocation()) {
				if (location.contains("_C")) {
					countries.add(location.replace("_C", ""));
				} else {
					countrySubdivisions.add(location);
				}
			}

			StringBuilder sb = new StringBuilder();

			filterOnCountries(countries, sb);
			filterOnCountrySubdivisions(countrySubdivisions, countries, sb);

			sql.addOrderBy("a.country");
			sql.addOrderBy("a.countrySubdivision");
			sql.addWhere(sb.toString());
			setFiltered(true);
		}
	}

	protected void filterOnCountries(List<String> countries, StringBuilder sb) {
		if (!countries.isEmpty()) {
			String countryList = Strings.implodeForDB(countries);
			sb.append("a.country IN (").append(countryList).append(")");
			sql.addOrderBy("CASE WHEN a.country IN (" + countryList + ") THEN 1 ELSE 2 END");
		}
	}

	protected void filterOnCountrySubdivisions(List<String> countrySubdivisions, List<String> countries,
											   StringBuilder sb) {
		if (!countrySubdivisions.isEmpty()) {
			if (!countries.isEmpty())
				sb.append(" OR ");

			String countrySubdivisionList = Strings.implodeForDB(countrySubdivisions);
			sb.append("a.countrySubdivision IN (").append(countrySubdivisionList).append(") OR ")
					.append("EXISTS (SELECT 'x' FROM pqfdata d ")
					.append("JOIN audit_question aq ON aq.id = d.questionID ").append("WHERE ca1.id = d.auditID ")
					.append("AND aq.uniqueCode IN (").append(countrySubdivisionList)
					.append(") AND d.answer != 'No' LIMIT 1) ");
			sql.addOrderBy("CASE WHEN a.countrySubdivision IN (" + countrySubdivisionList + ") THEN 1 ELSE 2 END");
		}
	}

	protected void filterOnZip() {
		if (filterOn(getFilter().getZip(), ReportFilterAccount.getDefaultZip())) {
			report.addFilter(new SelectFilter("zip", "a.zip LIKE '%?%'", getFilter().getZip()));
		}
	}

	protected void filterShowPrimaryContactInformation() {
		if (getFilter().isPrimaryInformation()) {
			sql.addField("contact.name AS contactname");
			sql.addField("contact.phone AS contactphone");
			sql.addField("contact.email AS contactemail");
			sql.addField("a.address");
			sql.addField("a.city");
			sql.addField("a.countrySubdivision");
			sql.addField("a.zip");
			sql.addField("a.web_URL");
		}
	}

	protected void filterShowTradeInformation() {
		if (getFilter().isTradeInformation()) {
			sql.addField("c.main_trade");
			sql.addField("c.tradesSelf");
			sql.addField("c.tradesSub");
		}
	}

	protected void filterOnTrades() {
		if (filterOn(getFilter().getTrade())) {
			for (int tradeID : getFilter().getTrade()) {
				SelectSQL tradeSQL = new SelectSQL("contractor_trade ct");
				tradeSQL.addJoin("JOIN ref_trade base ON ct.tradeID = base.id");
				tradeSQL.addJoin("JOIN ref_trade related ON base.indexStart <= related.indexStart and base.indexEnd >= related.indexEnd");
				tradeSQL.addWhere("a.id = ct.conID");
				// TODO allow users to search for Manufacture and Activity
				// Percent
				tradeSQL.addWhere("ct.activityPercent > 1");

				if (getFilter().getShowSelfPerformedTrade() == 1)
					tradeSQL.addWhere("ct.selfPerformed = 1");
				else if (getFilter().getShowSelfPerformedTrade() == 0)
					tradeSQL.addWhere("ct.selfPerformed = 0");

				tradeSQL.addWhere("related.id IN (" + tradeID + ")");
				sql.addWhere("EXISTS ( " + tradeSQL.toString() + ")");
			}
		}
	}

	protected void filterOnContractorIds() {
		if (filterOn(getFilter().getContractor())) {
			sql.addWhere(" a.id IN (" + Strings.implode(getFilter().getContractor()) + ")");

			setFiltered(true);
		}
	}

	protected void filterOnOperators() {
		if (filterOn(getFilter().getOperator())) {
			if (getFilter().isShowAnyOperator()) {
				sql.addWhere(" EXISTS (SELECT * FROM generalcontractors WHERE a.id = subID AND genID IN ("
						+ Strings.implode(getFilter().getOperator()) + "))");
			} else {
				for (int opID : getFilter().getOperator()) {
					sql.addWhere(" EXISTS (SELECT * FROM generalcontractors WHERE a.id = subID AND genID = " + opID
							+ ")");
				}
			}

			setFiltered(true);
		}
	}

	protected void filterOnOperatorSingle() {
		if (getFilter().getOperatorSingle() > 0) {
			sql.addWhere("a.id IN (SELECT subID FROM generalcontractors WHERE genID = "
					+ getFilter().getOperatorSingle() + ")");
		}
	}

	protected void filterOnTaxId() {
		if (filterOn(getFilter().getTaxID(), ReportFilterContractor.getDefaultTaxID())) {
			report.addFilter(new SelectFilter("taxID", "c.taxID = '?'", getFilter().getTaxID()));
		}
	}

	protected void filterOnFlagStatus() {
		if (!searchForNew && filterOn(getFilter().getFlagStatus())) {
			String list = Strings.implodeForDB(getFilter().getFlagStatus(), ",");
			if (sql.hasJoin("generalcontractors gc_flag"))
				sql.addWhere("gc_flag.flag IN (" + list + ")");
			else if (sql.hasJoin("generalcontractors gc"))
				sql.addWhere("gc.flag IN (" + list + ")");
			setFiltered(true);
		}
	}

	protected void filterOnWaitingOn() {
		if (filterOn(getFilter().getWaitingOn())) {
			report.addFilter(new SelectFilter("waitingOn", "gc.waitingOn = '?'", getFilter().getWaitingOn()));
		}
	}

	protected void filterOnAuditor() {
		if (filterOn(getFilter().getConAuditorId())) {
			String list = Strings.implode(getFilter().getConAuditorId(), ",");

			if (getFilter().isNonContactUser()) {
				sql.addWhere("u.id IN (" + list + ")");
			} else {
				sql.addJoin("JOIN account_user au on au.accountID = a.id and au.role='PICSCustomerServiceRep' and au.startDate < now() and au.endDate > now()");
				sql.addWhere("au.userID IN (" + list + ")");
			}
			setFiltered(true);
		}
	}

	protected void filterOnPolicyChangedDates() {
		if (filterOn(getFilter().getPolicyChangedDate1())) {
			report.addFilter(new SelectFilterDate("policyChangedDate1", "caow.creationDate > '?'", DateBean.format(
					getFilter().getPolicyChangedDate1(), "M/d/yy")));
		}

		if (filterOn(getFilter().getPolicyChangedDate2())) {
			report.addFilter(new SelectFilterDate("policyChangedDate2", "caow.creationDate < '?'", DateBean.format(
					getFilter().getPolicyChangedDate2(), "M/d/yy")));
		}
	}

	protected void filterOnAccountLevels() {
		if (filterOn(getFilter().getAccountLevel())) {
			sql.addWhere("c.accountLevel IN (" + Strings.implodeForDB(getFilter().getAccountLevel(), ",") + ")");
			setFiltered(true);
		}
	}

	protected void filterOnRiskLevels() {
		if (filterOn(getFilter().getRiskLevel())) {
			String list = Strings.implode(getFilter().getRiskLevel(), ",");
			sql.addWhere("c.safetyRisk IN (" + list + ")");
			setFiltered(true);
		}
		if (filterOn(getFilter().getProductRiskLevel())) {
			String list = Strings.implode(getFilter().getProductRiskLevel(), ",");
			sql.addWhere("c.productRisk IN (" + list + ")");
			setFiltered(true);
		}
	}

	protected void filterOnServiceType() {
		if (filterOn(getFilter().getService()) || getFilter().isOnlySelectedServices()) {
			List<String> clauses = new ArrayList<String>();

			boolean filterOnsite = false;
			boolean filterOffsite = false;
			boolean filterTransportation = false;
			boolean filterMaterial = false;

			for (String service : getFilter().getService()) {
				if ("Onsite".equals(service)) {
					filterOnsite = true;
					clauses.add("a.onsiteServices = 1");
				} else if ("Offsite".equals(service)) {
					filterOffsite = true;
					clauses.add("a.offsiteServices = 1");
				} else if ("Transportation".equals(service)) {
					filterTransportation = true;
					clauses.add("a.transportationServices = 1");
				} else if ("Material Supplier".equals(service)) {
					filterMaterial = true;
					clauses.add("a.materialSupplier = 1");
				}
			}

			if (!getFilter().isOnlySelectedServices()) {
				if (clauses.size() > 0) {
					sql.addWhere(Strings.implode(clauses, " OR "));
					setFiltered(true);
				}
			} else if (!filterOnsite && !filterOffsite && !filterTransportation && !filterMaterial) {
				clauses.add("a.onsiteServices = 1");
				clauses.add("a.offsiteServices = 1");
				clauses.add("a.transportationServices = 1");
				clauses.add("a.materialSupplier = 1");
				sql.addWhere(Strings.implode(clauses, " AND "));
				setFiltered(true);
			} else {
				if (!filterOnsite)
					clauses.add("a.onsiteServices = 0");
				if (!filterOffsite)
					clauses.add("a.offsiteServices = 0");
				if (!filterTransportation)
					clauses.add("a.transportationServices = 0");
				if (!filterMaterial)
					clauses.add("a.materialSupplier = 0");
				sql.addWhere(Strings.implode(clauses, " AND "));
				setFiltered(true);
			}
		}
	}

	protected void filterOnEmailTemplate() {
		if (getFilter().getEmailTemplate() > 0) {
			String emailQueueJoin = "LEFT JOIN email_queue eq on eq.conid = a.id AND eq.templateID = "
					+ getFilter().getEmailTemplate();
			if (filterOn(getFilter().getEmailSentDate())) {
				emailQueueJoin += " AND eq.sentDate >= '" + DateBean.format(getFilter().getEmailSentDate(), "yyyy-M-d")
						+ "'";
			}
			sql.addJoin(emailQueueJoin);
			sql.addWhere("eq.emailID IS NULL");
			setFiltered(true);
		}
	}

	protected void filterOnRegistrationDates() {
		if (filterOn(getFilter().getRegistrationDate1())) {
			report.addFilter(new SelectFilterDate("registrationDate1", "a.creationDate >= '?'", DateBean.format(
					getFilter().getRegistrationDate1(), "M/d/yy")));
		}

		if (filterOn(getFilter().getRegistrationDate2())) {
			report.addFilter(new SelectFilterDate("registrationDate2", "a.creationDate < '?'", DateBean.format(
					getFilter().getRegistrationDate2(), "M/d/yy")));
		}
	}

	protected void filterOnPendingPQFAnnualUpdate() {
		if (getFilter().isPendingPqfAnnualUpdate()) {
			String caopFilter = "";
			if (permissions.isOperatorCorporate()) {
				String opIDs = permissions.getAccountIdString();
				if (permissions.isCorporate())
					opIDs = Strings.implode(permissions.getOperatorChildren());

				caopFilter = " AND cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN ("
						+ opIDs + "))";
			}

			String query = "a.id IN (SELECT ca.conID FROM contractor_audit ca "
					+ "JOIN contractor_audit_operator cao ON cao.auditID = ca.id WHERE cao.status IN ('Pending','Incomplete','Resubmit') AND cao.visible = 1 AND ca.auditTypeID IN (1,11) AND ca.expiresDate > NOW()"
					+ caopFilter + ")";
			sql.addWhere(query);
		}
	}

	protected void filterOnOperatorTagName() {
		if (filterOn(getFilter().getOperatorTagName(), 0)) {
			String query = "a.id IN (SELECT ct.conID from contractor_tag ct " + "WHERE ct.tagID = "
					+ getFilter().getOperatorTagName() + ")";
			sql.addWhere(query);
		}
	}

	protected void filterOnCreditCardOnFile() {
		if (getFilter().getCcOnFile() < 2) {
			sql.addWhere("c.ccOnFile = " + getFilter().getCcOnFile());
		}
	}

	protected void filterOnDeactivationReason() {
		if (filterOn(getFilter().getDeactivationReason())) {
			report.addFilter(new SelectFilter("deactivationReason", "a.reason = '?'", getFilter()
					.getDeactivationReason()));
		}
	}

	protected void filterOnCustomAPI() {
		if (filterOn(getFilter().getCustomAPI()) && permissions.isAdmin()) {
			sql.addWhere(getFilter().getCustomAPI());
		}
	}

	protected void filterOnMinorityQuestions() {
		if (filterOn(getFilter().getMinorityQuestion(), 0)) {
			int[] questions = getFilter().getMinorityQuestion();
			sql.addJoin("LEFT JOIN contractor_audit casd ON casd.conID = a.id AND casd.auditTypeID = 1 ");
			for (int question : questions) {
				sql.addJoin("LEFT JOIN pqfdata pdsd" + question + " on casd.id = pdsd" + question + ".auditID AND pdsd"
						+ question + ".questionID = " + question);
			}
			StringBuilder where = new StringBuilder();
			for (int i = 0; i < questions.length; i++) {
				int question = questions[i];
				if (i != 0)
					where.append("OR ");

				where.append("pdsd").append(question);
				if ((question == 3543) || (question == 66) || (question == 77))
					where.append(".answer = 'X' ");
				else
					where.append(".answer = 'Yes' ");
			}
			sql.addWhere(where.toString());
		}
	}

	protected void filterOnWorkStatus() {
		if (filterOn(getFilter().getWorkStatus()) && permissions.isOperator()) {
			sql.addWhere("gc.workStatus LIKE '" + getFilter().getWorkStatus() + "%'");
		}
	}

	protected void filterOnInsuranceInformation() {
		if (getFilter().isShowInsuranceLimits()) {
			if (filterOn(getFilter().getGlEachOccurrence(), ReportFilterContractor.getDefaultAmount())) {
				sql.addAuditQuestion(2074, 13, true);
				sql.addWhere("REPLACE(q2074.answer,',','') >= " + getFilter().getGlEachOccurrence());
			}
			if (filterOn(getFilter().getGlGeneralAggregate(), ReportFilterContractor.getDefaultAmount())) {
				sql.addAuditQuestion(2079, 13, true);
				sql.addWhere("REPLACE(q2079.answer,',','') >= " + getFilter().getGlGeneralAggregate());
			}
			if (filterOn(getFilter().getAlCombinedSingle(), ReportFilterContractor.getDefaultAmount())) {
				sql.addAuditQuestion(2155, 15, true);
				sql.addWhere("REPLACE(q2155.answer,',','') >= " + getFilter().getAlCombinedSingle());
			}
			if (filterOn(getFilter().getWcEachAccident(), ReportFilterContractor.getDefaultAmount())) {
				sql.addAuditQuestion(2149, 14, true);
				sql.addWhere("REPLACE(q2149.answer,',','') >= " + getFilter().getWcEachAccident());
			}
			if (filterOn(getFilter().getExEachOccurrence(), ReportFilterContractor.getDefaultAmount())) {
				sql.addAuditQuestion(2161, 16, true);
				sql.addWhere("REPLACE(q2161.answer,',','') >= " + getFilter().getExEachOccurrence());
			}

			sql.addGroupBy("a.id");
		}
	}

	protected void filterOnAccountProperties() {
		if (getFilter().isOq())
			sql.addWhere("a.requiresOQ = 1");
		if (getFilter().isHse())
			sql.addWhere("a.requiresCompetencyReview = 1");
		if (getFilter().isSoleProprietership())
			sql.addWhere("c.soleProprietor = 1");
	}

	protected void filterOnInsideSales() {
		if (filterOn(getFilter().getInsideSalesID())) {
			sql.addJoin("JOIN account_user au ON au.accountID = a.id AND au.role='" +
					UserAccountRole.PICSInsideSalesRep.toString() + "' " +
					"AND au.startDate < now() and au.endDate > now()");
			sql.addWhere("au.userID IN (" + Strings.implode(getFilter().getInsideSalesID()) + ")");
			setFiltered(true);
		}
	}

	public List<User> getInsideSalesList() {
		return userDAO.findByGroup(User.GROUP_INSIDE_SALES);
	}
}