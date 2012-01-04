package com.picsauditing.report;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jboss.util.Strings;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;

/**
 * TODO class refactoring This class is probably going to get quite large. Our intention was never to have all of it in
 * a single class. but until we write this out it was hard to see how to layout the class. In other words, we expect
 * there to be class refactoring later on.
 */
public class QueryRunner {
	private SelectSQL sql = new SelectSQL();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private Map<String, String> joins = new HashMap<String, String>();
	private Permissions permissions;
	private String defaultSort = null;
	private int allRows = 0;
	private List<SimpleReportField> columns;
	private BasicDAO dao;
	private Report report;

	public QueryRunner(Report report, Permissions permissions, BasicDAO dao) {
		this.permissions = permissions;
		this.dao = dao;
		this.report = report;

		buildBase(report.getBase());
	}

	public QueryData run() throws SQLException {
		Database db = new Database();
		long queryTime = Calendar.getInstance().getTimeInMillis();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		queryTime = Calendar.getInstance().getTimeInMillis() - queryTime;
		allRows = db.getAllRows();
		QueryData data = new QueryData(columns, rows);
		if (queryTime > 1000) {
			System.out.println("Slow Query: " + sql.toString());
			System.out.println("Time to query: " + queryTime + " ms");
		}
		return data;
	}

	public SelectSQL buildQueryWithoutLimits() {
		// TODO: Remove page from report definition.
		SimpleReportDefinition definition = createDefinitionFromReportParameters(report.getParameters());
		SimpleReportDefinition devDefinition = createDefinitionFromReportParameters(report.getDevParams());

		definition = mergeDefinition(definition, devDefinition);

		defaultColumns(definition);
		addGroupBy(definition);
		addHaving(definition);

		removeObsoleteColumns();
		addColumns();

		addOrderBy(definition);
		addRuntimeFilters(definition);

		return sql;
	}

	public SelectSQL buildQuery() {
		buildQueryWithoutLimits();

		// We may need to move this to a class field
		sql.setSQL_CALC_FOUND_ROWS(true);
		SimpleReportDefinition definition = createDefinitionFromReportParameters(report.getParameters());
		addLimits(definition);

		return sql;
	}

	private SimpleReportDefinition mergeDefinition(SimpleReportDefinition definition,
			SimpleReportDefinition devDefinition) {
		if (devDefinition != null) {
			if (!devDefinition.getColumns().isEmpty())
				definition.getColumns().addAll(devDefinition.getColumns());
			if (!devDefinition.getGroupBy().isEmpty())
				definition.getGroupBy().addAll(devDefinition.getGroupBy());
			if (!devDefinition.getOrderBy().isEmpty())
				definition.getOrderBy().addAll(devDefinition.getOrderBy());
			if (!devDefinition.getHaving().isEmpty())
				definition.getHaving().addAll(devDefinition.getHaving());
			if (!devDefinition.getFilters().isEmpty())
				definition.getFilters().addAll(devDefinition.getFilters());
			if (devDefinition.getFilterExpression() != null)
				definition.setFilterExpression(definition.getFilterExpression() + " AND "
						+ devDefinition.getFilterExpression());
		}

		return definition;
	}

	private void defaultColumns(SimpleReportDefinition definition) {
		if (columns == null || columns.isEmpty()) {
			columns = definition.getColumns();
		}

		if (columns.size() == 0) {
			columns.clear();
			for (String fieldName : availableFields.keySet()) {
				SimpleReportField column = new SimpleReportField();
				column.setQueryField(availableFields.get(fieldName));
				columns.add(column);
			}
		}
	}

	private void removeObsoleteColumns() {
		Iterator<SimpleReportField> iterator = columns.iterator();
		while (iterator.hasNext()) {
			SimpleReportField column = iterator.next();
			if (!availableFields.containsKey(column.field))
				iterator.remove();
		}
	}

	private void addRuntimeFilters(SimpleReportDefinition definition) {
		if (definition.getFilters().size() == 0) {
			return;
		}

		String where = definition.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = "";
			for (int i = 0; i < definition.getFilters().size(); i++) {
				where += "{" + i + "} AND ";
			}
			where = StringUtils.removeEnd(where, " AND ");
		}

		for (int i = 0; i < definition.getFilters().size(); i++) {
			SimpleReportFilter queryFilter = definition.getFilters().get(i);

			if (queryFilter.getOperator().equals(QueryFilterOperator.InReport)) {
				Report subReport = dao.find(Report.class, Integer.parseInt(queryFilter.getValue()));
				QueryRunner subRunner = new QueryRunner(subReport, permissions, dao);
				SelectSQL subSql = subRunner.buildQueryWithoutLimits();
				queryFilter.setValue(subSql.toString());
			}

			String filterExp = queryFilter.toExpression(availableFields);
			where = where.replace("{" + i + "}", "(" + filterExp + ")");
		}
		sql.addWhere(where);
	}

	private void addColumns() {
		Set<String> addedJoins = new HashSet<String>();
		for (SimpleReportField column : columns) {
			if (availableFields.keySet().contains(column.field)) {
				QueryField availableField = availableFields.get(column.field);

				column.setQueryField(availableField);

				addLeftJoins(addedJoins, availableField);

				sql.addField(column.toSQL(availableFields) + " AS " + column.field);
				// TODO: Think about case/if (IF function or CASE statement) and
				// calculated fields (2 fields coming out with a result)
			}
		}
	}

	private void addLeftJoins(Set<String> addedJoins, QueryField availableField) {
		if (availableField.requiresJoin()) {
			if (!addedJoins.contains(availableField.requireJoin)) {
				sql.addJoin(this.joins.get(availableField.requireJoin));
				addedJoins.add(availableField.requireJoin);
			}
		}
	}

	private void addOrderBy(SimpleReportDefinition definition) {
		if (definition.getOrderBy().size() == 0) {
			sql.addOrderBy(defaultSort);
			return;
		}

		for (SimpleReportField field : definition.getOrderBy()) {
			String orderBy = field.field;
			if (!columns.contains(field.field))
				orderBy = availableFields.get(field.field).sql;
			if (!field.ascending)
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
		}
	}

	private void addGroupBy(SimpleReportDefinition definition) {
		if (definition.getGroupBy().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getGroupBy()) {
			String groupBy = field.toSQL(availableFields);
			sql.addGroupBy(groupBy);
		}

		addTotalField();
	}

	private void addHaving(SimpleReportDefinition definition) {
		if (definition.getHaving().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getHaving()) {
			String having = field.toSQL(availableFields);
			sql.addHaving(having);
		}
	}

	private void addTotalField() {
		addQueryField("total", null, FilterType.Number);
		SimpleReportField total = new SimpleReportField();
		total.field = "total";
		total.function = QueryFunction.Count;
		columns.add(total);
	}

	private void addLimits(SimpleReportDefinition definition) {
		if (definition.getPage() > 1)
			sql.setStartRow((definition.getPage() - 1) * definition.getRowsPerPage());
		sql.setLimit(definition.getRowsPerPage());
	}

	private void buildBase(QueryBase base) {
		switch (base) {
		case Operators:
			buildOperatorBase();
			break;
		case Resources:
			buildResourceBase();
			break;
		case Contractors:
			buildContractorBase();
			break;
		case ContractorAudits:
			buildContractorAuditBase();
			break;
		case ContractorAuditData:
			buildContractorAuditDataBase();
			break;
		case ContractorAuditOperators:
			buildContractorAuditOperatorBase();
			break;
		case ContractorAuditOperatorWorkflows:
			buildContractorAuditOperatorWorkflowBase();
			break;
		case ContractorFees:
			buildContractorFeeBase();
			break;
		case ContractorTrades:
			buildContractorTradeBase();
			break;
		case Users:
			buildUserBase();
			break;
		case UserGroups:
			buildUserGroupBase();
			break;
		case UserAssignments:
			buildUserAssignmentBase();
			break;
		case Emails:
			buildEmailBase();
			break;
		case EmailExclusions:
			buildEmailExclusionBase();
			break;
		case EmailSubscriptions:
			buildEmailSubscriptionBase();
			break;
		case Employees:
			buildEmployeeBase();
			break;
		case EmployeeAssessments:
			buildEmployeeAssessmentBase();
			break;
		case ExceptionLogs:
			buildExceptionLogBase();
			break;
		case Invoices:
			buildInvoiceBase();
			break;
		case InvoiceItems:
			buildInvoiceItemBase();
			break;
		case RegistrationRequests:
			buildRegistrationRequestsBase();
			break;
		default:
			buildAccountBase();
			break;
		}
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType) {
		return addQueryField(dataIndex, sql, filterType, null, false);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin) {
		return addQueryField(dataIndex, sql, filterType, requireJoin, false);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, boolean makeDefault) {
		return addQueryField(dataIndex, sql, filterType, null, makeDefault);
	}

	private QueryField addQueryField(String dataIndex, String sql, FilterType filterType, String requireJoin,
			boolean makeDefault) {
		QueryField field = new QueryField(dataIndex, sql, filterType, requireJoin, makeDefault);
		availableFields.put(dataIndex, field);
		return field;
	}

	private QueryField replaceQueryField(String source, String target) {
		QueryField field = availableFields.remove(source);
		field.dataIndex = target;
		availableFields.put(target, field);
		return field;
	}

	private void buildAccountBase() {
		sql = new SelectSQL();
		sql.setFromTable("accounts a");

		addQueryField("accountID", "a.id", FilterType.Number, true);
		addQueryField("accountName", "a.name", FilterType.AccountName, true);
		addQueryField("accountStatus", "a.status", FilterType.Enum);
		addQueryField("accountType", "a.type", FilterType.Enum);
		addQueryField("accountPhone", "a.phone", FilterType.String);
		addQueryField("accountFax", "a.fax", FilterType.String);
		addQueryField("accountCreationDate", "a.creationDate", FilterType.Date);
		addQueryField("accountAddress", "a.address", FilterType.String);
		addQueryField("accountCity", "a.city", FilterType.String);
		addQueryField("accountState", "a.state", FilterType.String);
		addQueryField("accountZip", "a.zip", FilterType.String);
		addQueryField("accountCountry", "a.country", FilterType.String);
		addQueryField("accountWebsite", "a.web_url", FilterType.String).addRenderer("http://{0}\">{0}",
				new String[] { "accountWebsite" });
		addQueryField("accountDBAName", "a.dbaName", FilterType.AccountName);
		addQueryField("accountNameIndex", "a.nameIndex", FilterType.AccountName);
		addQueryField("accountReason", "a.reason", FilterType.String);
		addQueryField("accountOnsite", "a.onsiteServices", FilterType.Boolean);
		addQueryField("accountOffsite", "a.offsiteServices", FilterType.Boolean);
		addQueryField("accountTransportation", "a.transportationServices", FilterType.Boolean);
		addQueryField("accountMaterialSupplier", "a.materialSupplier", FilterType.Boolean);

		leftJoinToUser("accountContact", "a.contactID");

		joinToNAICS("naics", "a.naics");

		defaultSort = "a.nameIndex";
	}

	private void buildEmailBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailQueue eq");

		addQueryField("emailID", "eq.emailID", FilterType.Number, true);
		addQueryField("emailStatus", "eq.emailStatus", FilterType.Enum);
		addQueryField("emailFromAddress", "eq.emailFromAddress", FilterType.String, true);
		addQueryField("emailToAddresses", "eq.emailToAddresses", FilterType.String, true);
		addQueryField("emailCcAddresses", "eq.emailCcAddresses", FilterType.String);
		addQueryField("emailSubject", "eq.subject", FilterType.String, true);
		addQueryField("emailPriority", "eq.priority", FilterType.Number);
		addQueryField("emailCreationDate", "eq.creationDate", FilterType.Date);
		addQueryField("emailSentDate", "eq.sentDate", FilterType.Date);

		leftJoinToAccount("emailContractor", "eq.conID");
		leftJoinToEmailTemplate("et", "emailTemplate");

		defaultSort = "eq.priority DESC, eq.emailID";
	}

	private void buildEmailExclusionBase() {
		sql = new SelectSQL();
		sql.setFromTable("emailExclusions ee");

		addQueryField("emailExcluded", "ee.email", FilterType.String, true);

		defaultSort = "ee.email";
	}

	private void buildExceptionLogBase() {
		sql = new SelectSQL();
		sql.setFromTable("app_error_log as ael");

		addQueryField("errorLogID", "ael.id", FilterType.Number, true);
		addQueryField("errorLogCategory", "ael.category", FilterType.String, true);
		addQueryField("errorLogPriority", "ael.priority", FilterType.Number, true);
		addQueryField("errorLogStatus", "ael.status", FilterType.Enum);
		addQueryField("errorLogCreatedBy", "ael.createdBy", FilterType.Number);
		addQueryField("errorLogUpdatedBy", "ael.updatedBy", FilterType.Number);
		addQueryField("errorLogCreationDate", "ael.creationDate", FilterType.Date);
		addQueryField("errorLogUpdateDate", "ael.updateDate", FilterType.Date);
		addQueryField("errorLogMessage", "ael.message", FilterType.String);

		defaultSort = "ael.creationDate DESC";
	}

	private void buildRegistrationRequestsBase() {
		sql = new SelectSQL();
		sql.setFromTable("contractor_registration_request crr");

		addQueryField("requestID", "crr.id", FilterType.Number);

		addQueryField("requestedName", "crr.name", FilterType.String, true).addRenderer(
				"RequestNewContractor.action?newContractor={0}\">{1}", new String[] { "requestID", "requestedName" });
		addQueryField("requestedContact", "crr.contact", FilterType.String, true);
		addQueryField("requestedPhone", "crr.phone", FilterType.String, true);
		addQueryField("requestedEmail", "crr.email", FilterType.String);
		addQueryField("requestedTaxID", "crr.taxID", FilterType.String);
		addQueryField("requestedAddress", "crr.address", FilterType.String);
		addQueryField("requestedCity", "crr.city", FilterType.String);
		addQueryField("requestedState", "crr.state", FilterType.String);
		addQueryField("requestedZip", "crr.zip", FilterType.String);
		addQueryField("requestedCountry", "crr.country", FilterType.String);
		addQueryField("requestedNotes", "crr.notes", FilterType.String);
		addQueryField("requestedDeadline", "crr.deadline", FilterType.Date, true);
		addQueryField("requestedLastContactedByDate", "crr.lastContactDate", FilterType.String);
		addQueryField("requestedContactCount", "crr.contactCount", FilterType.Number);
		addQueryField("requestedMatchCount", "crr.matchCount", FilterType.Number);
		addQueryField("requestCreationDate", "crr.creationDate", FilterType.Date);
		addQueryField("requestStatus", "crr.status", FilterType.Enum);

		sql.addJoin("JOIN accounts op ON op.id = crr.requestedByID");
		addQueryField("requestedByOperatorID", "op.id", FilterType.Number);
		addQueryField("requestedByOperatorName", "op.name", FilterType.AccountName).addRenderer(
				"FacilitiesEdit.action?operator={0}\">{1}",
				new String[] { "requestedByOperatorID", "requestedByOperatorName" });

		leftJoinToUser("requestedBy", "crr.requestedByUserID");
		leftJoinToUser("contactedBy", "crr.lastContactedBy");

		leftJoinToAccount("requestedExisting", "crr.conID");
	}

	private void buildContractorBase() {
		buildAccountBase();

		sql.addJoin("JOIN contractor_info c ON a.id = c.id");
		sql.addWhere("a.type='Contractor'");
		availableFields.remove("accountType");
		replaceQueryField("accountName", "contractorName");
		availableFields.get("contractorName").addRenderer(
				new Renderer("ContractorView.action?id={0}\">{1}", new String[] { "accountID", "contractorName" }));
		addQueryField("contractorRiskLevel", "c.riskLevel", FilterType.Number);
		addQueryField("contractorSafetyRisk", "c.safetyRisk", FilterType.Number);
		addQueryField("contractorProductRisk", "c.productRisk", FilterType.Number);
		addQueryField("contractorMainTrade", "c.main_trade", FilterType.String);
		addQueryField("contractorTradesSelfPerformed", "c.tradesSelf", FilterType.String);
		addQueryField("contractorTradesSubContracted", "c.tradesSub", FilterType.String);
		addQueryField("contractorScore", "c.score", FilterType.Number);
		addQueryField("contractorPaymentExpires", "c.paymentExpires", FilterType.Date);
		addQueryField("contractorPaymentMethod", "c.paymentMethod", FilterType.String);
		addQueryField("contractorCreditCardOnFile", "c.ccOnFile", FilterType.Boolean);
		addQueryField("contractorCreditCardExpiration", "c.ccExpiration", FilterType.Date);
		addQueryField("contractorBalance", "c.balance", FilterType.Number);
		addQueryField("contractorAccountLevel", "c.accountLevel", FilterType.String);
		addQueryField("contractorRenew", "c.renew", FilterType.Boolean);
		addQueryField("contractorMustPay", "c.mustPay", FilterType.Boolean);
		addQueryField("contractorPayingFacilities", "c.payingFacilities", FilterType.Number);
		addQueryField("contractorMembershipDate", "c.membershipDate", FilterType.Date);
		addQueryField("contractorLastUpgradeDate", "c.lastUpgradeDate", FilterType.Date);
		addQueryField("contractorTRIRAverage", "c.trirAverage", FilterType.Number);

		leftJoinToUser("customerService", "c.welcomeAuditor_id");
		leftJoinToAccount("requestedByOperator", "c.requestedByID");

		joinToFlagCriteriaContractor("contractorFlagCriteria", "c.id");
		leftJoinToEmailQueue("contractorEmail", "c.id");
		joinToGeneralContractor("contractorOperator", "subID", "c.id");
		joinToContractorWatch("contractorWatch", "c.id");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());
	}

	private void buildEmailSubscriptionBase() {
		buildAccountBase();
		sql.addJoin("JOIN email_subscription es ON es.userID = u.id");

		addQueryField("emailSubscription", "es.subscription", FilterType.String, true);
		addQueryField("emailSubscriptionTimePeriod", "es.timePeriod", FilterType.String, true);
	}

	private void buildEmployeeBase() {
		buildAccountBase();
		sql.addJoin("JOIN employee e ON a.id = e.accountID");

		addQueryField("employeeID", "e.id", FilterType.Number, true);
		addQueryField("employeeFirstName", "e.firstName", FilterType.String, true);
		addQueryField("employeeLastName", "e.lastName", FilterType.String, true);
		addQueryField("employeeTitle", "e.title", FilterType.String, true);
		addQueryField("employeeBirthDate", "e.birthDate", FilterType.Date);
		addQueryField("employeeHireDate", "e.hireDate", FilterType.Date);
		addQueryField("employeeEmail", "e.email", FilterType.String);
		addQueryField("employeePhone", "e.phone", FilterType.String);
		addQueryField("employeeLocation", "e.location", FilterType.String);
		addQueryField("employeeSSN", "e.ssn", FilterType.Number);
		addQueryField("employeeTwicExpiration", "e.twicExpiration", FilterType.Date);
		QueryField employeeClassification = addQueryField("employeeClassification", "e.classification", FilterType.Enum);
		employeeClassification.translate("EmployeeClassification", "description");
		addQueryField("employeeActive", "e.active", FilterType.Boolean);
	}

	private void buildOperatorBase() {
		buildAccountBase();
		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addWhere("a.type IN ('Operator','Corporate')");
		replaceQueryField("accountName", "operatorName");
		availableFields.remove("accountType");

		joinToFacilities("operatorFacility", "opID", "a.id");
		joinToFacilities("corporateFacility", "corporateID", "a.id");
		joinToGeneralContractor("operatorContractor", "genID", "o.id");
	}

	private void buildResourceBase() {
		buildAccountBase();

		sql.addJoin("JOIN operatorforms of ON a.id = of.opID");

		addQueryField("resourceID", "of.id", FilterType.Number, true);
		addQueryField("resourceOperatorAccountID", "of.opID", FilterType.Number, true);
		addQueryField("resourceName", "of.formName", FilterType.String, true);
		addQueryField("resourceParentResourceID", "of.parentID", FilterType.Number);
		addQueryField("resourceLocale", "of.locale", FilterType.String);

		defaultSort = "o.formName";
	}

	private void buildUserBase() {
		buildAccountBase();
		sql.addJoin("JOIN users u ON u.accountID = a.id");

		addQueryField("userID", "u.id", FilterType.Number, true);
		addQueryField("userAccountID", "u.accountID", FilterType.Number, true);
		addQueryField("userName", "u.name", FilterType.String, true);
		addQueryField("userScreenName", "u.username", FilterType.String);
		addQueryField("userIsActive", "u.isActive", FilterType.String);
		addQueryField("userIsGroup", "u.isGroup", FilterType.String);
		addQueryField("userLastLogin", "u.lastLogin", FilterType.Date);

		joinToLoginLog("userLoginLog", "u.id");
	}

	private void buildUserAssignmentBase() {
		buildUserBase();
		sql.addJoin("JOIN user_assignment ua ON ua.userID = u.id");

		addQueryField("userAssignedCountry", "ua.country", FilterType.List, true);
		addQueryField("userAssignedState", "ua.state", FilterType.List, true);
		addQueryField("userAssignedPostalStart", "ua.postal_start", FilterType.String, true);
		addQueryField("userAssignedPostalEnd", "ua.postal_end", FilterType.String, true);
		addQueryField("userAssignedType", "ua.assignmentType", FilterType.Enum);

		joinToAuditType("userAssignedAuditType", "ua.auditTypeID");
		leftJoinToAccount("contractorAccount", "ua.conID");
	}

	private void buildUserGroupBase() {
		buildUserBase();
		sql.addJoin("JOIN usergroup ug ON u.id = ug.userID");

		leftJoinToUser("grp", "ug.groupID");
	}

	private void buildContractorAuditBase() {
		buildContractorBase();

		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		addQueryField("auditID", "ca.id", FilterType.Number, true);
		addQueryField("auditCreationDate", "ca.creationDate", FilterType.Date);
		addQueryField("auditExpirationDate", "ca.expiresDate", FilterType.Date);
		addQueryField("auditScheduledDate", "ca.scheduledDate", FilterType.Date);
		addQueryField("auditAssignedDate", "ca.assignedDate", FilterType.Date);
		addQueryField("auditLocation", "ca.auditLocation", FilterType.String);
		addQueryField("auditFor", "ca.auditFor", FilterType.String, true);
		addQueryField("auditScore", "ca.score", FilterType.Number);
		addQueryField("auditAuditorID", "ca.auditorID", FilterType.Number);
		addQueryField("auditClosingAuditorID", "ca.closingAuditorID", FilterType.Number);
		addQueryField("auditContractorConfirmation", "ca.contractorConfirm", FilterType.Date);
		addQueryField("auditAuditorConfirmation", "ca.auditorConfirm", FilterType.Date);

		QueryField auditTypeName = joinToAuditType("auditType", "ca.auditTypeID");
		auditTypeName.addRenderer("Audit.action?auditID={0}\">{1} {2}", new String[] { "auditID", "auditTypeName",
				"auditFor" });

		leftJoinToUser("auditor", "ca.auditorID");
		leftJoinToUser("closingAuditor", "ca.closingAuditorID");

		joinToOshaAudit("oshaAudit", "ca.id");
	}

	private void buildContractorTradeBase() {
		buildContractorBase();

		sql.addJoin("contractor_trade parent ON parent.conID = a.id");
		sql.addJoin("ref_trade tParent ON tParent.id = parent.tradeID");
		sql.addJoin("contractor_trade child ON child.conID = a.id");
		sql.addJoin("ref_trade tChild ON tChild.id = child.tradeID");

		QueryField contractorTradeParentID = addQueryField("contractorTradeParentID", "tParent.id", FilterType.Number, true);
		contractorTradeParentID.translate("Trade", "name");
		addQueryField("contractorTradeParentIndexStart", "tParent.indexStart", FilterType.Number);
		addQueryField("contractorTradeParentIndexEnd", "tParent.indexEnd", FilterType.Number);

		QueryField contractorTradeChildID = addQueryField("contractorTradeChildID", "tChild.id", FilterType.Number, true);
		contractorTradeChildID.translate("Trade", "name");
		addQueryField("contractorTradeChildIndexStart", "tChild.indexStart", FilterType.Number);
		addQueryField("contractorTradeChildIndexEnd", "tChild.indexEnd", FilterType.Number);
	}

	private void buildContractorFeeBase() {
		buildContractorBase();

		sql.addJoin("contractor_fee cf ON cf.conID = a.id");

		addQueryField("contractorFeeCurrentAmount", "cf.currentAmount", FilterType.Number, true);
		addQueryField("contractorFeeNewAmount", "cf.newAmount", FilterType.Number);

		joinToInvoiceFee("contractorFee", "cf.newLevel");
	}

	private void buildInvoiceBase() {
		buildContractorBase();

		sql.addJoin("JOIN invoice i on i.accountID = a.id");

		addQueryField("invoiceID", "i.id", FilterType.Number, true);
		addQueryField("invoiceAmountApplied", "i.amountApplied", FilterType.Number);
		addQueryField("invoiceTotalAmount", "i.totalAmount", FilterType.Number, true);
		addQueryField("invoiceDueDate", "i.dueDate", FilterType.Date);
		addQueryField("invoiceStatus", "i.status", FilterType.Enum);
		addQueryField("invoiceCreationDate", "i.creationDate", FilterType.Date);
		addQueryField("invoiceTableType", "i.tableType", FilterType.String);
	}

	private void buildInvoiceItemBase() {
		buildInvoiceBase();

		sql.addJoin("JOIN invoice_item ii on ii.invoiceID = i.id");

		addQueryField("invoiceItemPaymentExpires", "ii.paymentExpires", FilterType.Date, true);
		addQueryField("invoiceItemAmount", "ii.amount", FilterType.Number, true);

		joinToInvoiceFee("invoiceItemFee", "ii.feeID");
	}

	private void buildEmployeeAssessmentBase() {
		buildEmployeeBase();

		sql.addJoin("assessment_result ar ON ar.employeeID = e.id");
		sql.addJoin("assessment_test test ON test.id = ar.assessmentTestID");
		sql.addJoin("accounts center ON center.id = test.assessmentCenterID");

		addQueryField("assessmentResultExpirationDate", "ar.expirationDate", FilterType.Date);

		addQueryField("assessmentTestDescription", "test.description", FilterType.String, true);
		addQueryField("assessmentTestQualificationType", "test.qualificationType", FilterType.String, true);
		addQueryField("assessmentTestQualificationMethod", "test.qualificationMethod", FilterType.String);

		addQueryField("assessmentCenterName", "center.name", FilterType.String);
	}

	private void buildContractorAuditOperatorBase() {
		buildContractorAuditBase();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		addQueryField("auditOperatorID", "cao.id", FilterType.Number, true);
		addQueryField("auditOperatorStatus", "cao.status", FilterType.Enum, true);
		addQueryField("auditOperatorStatusChangedDate", "cao.statusChangedDate", FilterType.Date);
		addQueryField("auditOperatorVisible", "cao.visible", FilterType.Boolean);
		addQueryField("auditOperatorPercentComplete", "cao.percentComplete", FilterType.Number, true);

		leftJoinToAccount("caoAccount", "cao.opID");
	}

	private void buildContractorAuditOperatorWorkflowBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN contractor_audit_operator_workflow cao ON cao.id = caow.caoID");

		addQueryField("auditOperatorWorkflowStatus", "caow.status", FilterType.Enum, true);
		addQueryField("auditOperatorWorkflowPreviousStatus", "caow.previousStatus", FilterType.Enum, true);
		addQueryField("auditOperatorWorkflowCreationDate", "caow.creationDate", FilterType.Date, true);
		leftJoinToUser("auditOperatorWorkflowCreatedBy", "caow.createdBy");
	}

	private void buildContractorAuditDataBase() {
		buildContractorAuditOperatorBase();

		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		addQueryField("auditDataAnswer", "pd.answer", FilterType.String, true);
		addQueryField("auditDataDateVerified", "pd.dateVerified", FilterType.Date);
		addQueryField("auditDataQuestionID", "pd.questionID", FilterType.Number);
		QueryField auditDataQuestion = addQueryField("auditDataQuestion", "pd.questionID", FilterType.String, true);
		auditDataQuestion.translate("AuditQuestion", "name");
		addQueryField("auditDataUpdateDate", "pd.updateDate", FilterType.Date);
	}

	private QueryField joinToAuditType(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN audit_type " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", foreignKey, FilterType.Number, joinAlias, true);
		QueryField auditTypeName = addQueryField(joinAlias + "Name", foreignKey, FilterType.String, joinAlias, true);
		auditTypeName.translate("AuditType", "name");

		addQueryField(joinAlias + "ClassType", joinAlias + ".classType", FilterType.Enum, joinAlias);
		addQueryField(joinAlias + "IsScheduled", joinAlias + ".isScheduled", FilterType.Boolean, joinAlias);
		addQueryField(joinAlias + "HasAuditor", joinAlias + ".hasAuditor", FilterType.Boolean, joinAlias);
		addQueryField(joinAlias + "Scorable", joinAlias + ".scoreable", FilterType.Boolean, joinAlias);

		return auditTypeName;
	}

	private void joinToContractorWatch(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN contractor_watch " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);

		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "UserID", joinAlias + ".userID", FilterType.Number, joinAlias, true);
	}

	private void leftJoinToEmailQueue(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN email_queue " + joinAlias + " ON " + joinAlias + ".conID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "CreationDate", joinAlias + ".creationDate", FilterType.Date, joinAlias, true);
		addQueryField(joinAlias + "SentDate", joinAlias + ".sentDate", FilterType.Date, joinAlias, true);
		addQueryField(joinAlias + "CreatedBy", joinAlias + ".createdBy", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "ViewableBy", joinAlias + ".viewableBy", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Subject", joinAlias + ".subject", FilterType.String, joinAlias);
		addQueryField(joinAlias + "TemplateID", joinAlias + ".templateID", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Status", joinAlias + ".status", FilterType.Enum, joinAlias);
	}

	private void joinToFacilities(String joinAlias, String tableKey, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN facilities " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "OperatorID", joinAlias + ".opID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "CorporateID", joinAlias + ".corporateID", FilterType.Number, joinAlias, true);

		leftJoinToAccount("operatorChild", joinAlias + ".opID");
		leftJoinToAccount("corporateParent", joinAlias + ".corporateID");
	}

	private void joinToFlagCriteriaContractor(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN flag_criteria_contractor " + joinAlias + " ON " + joinAlias + ".conID = "
				+ foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, true);

		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "CriteriaID", joinAlias + ".criteriaID", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "Answer", joinAlias + ".answer", FilterType.String, joinAlias);
	}

	private void joinToGeneralContractor(String joinAlias, String tableKey, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN generalcontractors " + joinAlias + " ON " + joinAlias + "." + tableKey + " = "
				+ foreignKey);

		addQueryField(joinAlias + "ContractorID", joinAlias + ".subID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "OperatorID", joinAlias + ".genID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "FlagLastUpdated", joinAlias + ".flagLastUpdated", FilterType.Date, joinAlias);
		addQueryField(joinAlias + "Flag", joinAlias + ".flag", FilterType.String, joinAlias, true);

		leftJoinToAccount(joinAlias + "Operator", joinAlias + ".genID");
	}

	private void joinToInvoiceFee(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN invoice_fee " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);

		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "MaxFacilities", joinAlias + ".maxFacilities", FilterType.Number, joinAlias, true);
	}

	private void joinToLoginLog(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN loginlog " + joinAlias + " ON " + joinAlias + ".userID = " + foreignKey);
		addQueryField(joinAlias + "UserID", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "AdminID", joinAlias + ".adminID", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "LoginDate", joinAlias + ".loginDate", FilterType.Date, joinAlias);
		addQueryField(joinAlias + "RemoteAccess", joinAlias + ".remoteAccess", FilterType.String, joinAlias);
	}

	private void joinToOshaAudit(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN osha_audit " + joinAlias + " ON " + joinAlias + ".auditID = " + foreignKey);
		addQueryField(joinAlias + "ContractorID", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "SHAType", joinAlias + ".SHAType", FilterType.String, joinAlias);
		addQueryField(joinAlias + "Fatalities", joinAlias + ".fatalities", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "VerifiedDate", joinAlias + ".verifiedDate", FilterType.Date, joinAlias);
		addQueryField(joinAlias + "Location", joinAlias + ".location", FilterType.String, joinAlias);
		addQueryField(joinAlias + "Description", joinAlias + ".description", FilterType.String, joinAlias);
		addQueryField(joinAlias + "RecordableTotal", joinAlias + ".recordableTotal", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "ManHours", joinAlias + ".manHours", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "CAD7", joinAlias + ".cad7", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "NEER", joinAlias + ".neer", FilterType.Number, joinAlias);
	}

	private void joinToNAICS(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "JOIN naics " + joinAlias + " ON " + joinAlias + ".code = " + foreignKey);
		addQueryField(joinAlias + "Code", foreignKey, FilterType.Number, joinAlias, true);

		addQueryField(joinAlias + "TRIR", joinAlias + ".trir", FilterType.Number, joinAlias);
		addQueryField(joinAlias + "LWCR", joinAlias + ".lwcr", FilterType.Number, joinAlias);
	}

	private void leftJoinToAccount(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN accounts " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "Name", joinAlias + ".name", FilterType.AccountName, joinAlias, true);
		addQueryField(joinAlias + "Status", joinAlias + ".status", FilterType.Enum, joinAlias);
		addQueryField(joinAlias + "Type", joinAlias + ".type", FilterType.Enum, joinAlias);
	}

	private void leftJoinToUser(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN users " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "UserID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "UserName", joinAlias + ".name", FilterType.String, joinAlias, true).addRenderer(
				"UsersManage.action?user={0}\">{1}", new String[] { joinAlias + "UserID", joinAlias + "UserName" });

		addQueryField(joinAlias + "Phone", joinAlias + ".phone", FilterType.String, joinAlias);
		addQueryField(joinAlias + "Email", joinAlias + ".email", FilterType.String, joinAlias);
	}

	private void leftJoinToEmailTemplate(String joinAlias, String foreignKey) {
		joins.put(joinAlias, "LEFT JOIN email_template " + joinAlias + " ON " + joinAlias + ".id = " + foreignKey);
		addQueryField(joinAlias + "ID", joinAlias + ".id", FilterType.Number, joinAlias, true);
		addQueryField(joinAlias + "Name", joinAlias + ".templateName", FilterType.String, joinAlias, true);
	}

	public SimpleReportDefinition createDefinitionFromReportParameters(String parameters) {
		if (StringUtils.isEmpty(parameters))
			return null;

		SimpleReportDefinition definition = new SimpleReportDefinition();
		if (parameters != null) {
			JSONObject obj = (JSONObject) JSONValue.parse(parameters);
			if (obj != null) {
				definition.fromJSON(obj);
			}
		}
		return definition;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public List<SimpleReportField> getColumns() {
		return columns;
	}

	public int getAllRows() {
		return allRows;
	}

	public String getSQL() {
		return sql.toString().replace("\n", " ").replace("  ", " ");
	}
}